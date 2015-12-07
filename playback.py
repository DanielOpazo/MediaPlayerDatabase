#! /usr/bin/python

import subprocess, os, time, sys, select, socket, sys, re, MySQLdb
from random import shuffle

debug = True

#-----------------------------------------------------------------------------------------
#database functions

USER = "java"
PASSWORD = "password"
DB_NAME = "media_library"

def getDbConnection():
    db = MySQLdb.connect("localhost", USER, PASSWORD, DB_NAME)
    if (db is None):
        print 'ERROR could not connect to database'
    return db

#songId is a string
def getSongInfo(songId):
    songTitle = ""
    filePath = ""
    albumTitle = ""
    artistName = ""
    db = getDbConnection()
    cursor = db.cursor()
    try:
        cursor.execute("select s.title, s.file_path, al.title, ar.name from song as s inner join album as al on s.album_id = al.album_id inner join artist as ar on al.artist_id = ar.artist_id where s.song_id = %s", songId)
        results = cursor.fetchall()
        for row in results:
            songTitle = row[0]
            filePath = row[1]
            albumTitle = row[2]
            artistName = row[3]
            if debug: print songTitle, filePath, albumTitle, artistName
    except:
        print 'ERROR fetching data for song info'
    db.close()
    return songTitle, filePath, albumTitle, artistName

#videoId is a string
def getVideoInfo(videoId):
    videoTitle = ""
    filePath = ""
    release_date = ""
    category = ""
    db = getDbConnection()
    cursor = db.cursor()
    try:
        cursor.execute("select title, video_path, release_date, category from video where video_id = %s", videoId)
        results = cursor.fetchall()
        for row in results:
            videoTitle = row[0]
            filePath = row[1]
            release_date = row[2]
            category = row[3]
            if debug: print videoTitle, filePath, release_date, category
    except:
        print 'ERROR fetching data for video info'
    db.close()
    return videoTitle, filePath, release_date, category
        

def db_test():
    getVideoInfo('1')

#----------------------------------------------------------------------------------------
#functions for playing track and controlling playback

def child_start(cwd):
# create the subprocess which will call omxplayer
# a subprocess is needed because omxplayer controls must be piped
# if I specify omxplayer  as the first argument of Popen then
# I could not get the control commands to it hence the child is a shell scriptncix     
# which calls omxplayer.

    proc=subprocess.Popen([cwd + '/omxchild.sh'], shell=False, \
          stderr=subprocess.PIPE, \
          stdout=subprocess.PIPE, \
          stdin=subprocess.PIPE)
    pid = proc.pid
    if debug: print "PID: ", pid
    
    # wait until child has started otherwise commands sent get confused
    while proc.poll()!= None:
        time.sleep(0.1)
    if debug: print 'Subprocess ',pid," started"
    return proc

# poll child process to see if it is running
def child_running(proc):
        if proc.poll() != None:
            return False
        else:
            return True

# wait for the child process to terminate
def child_wait_for_terminate(proc):
    while proc.poll()!= None:
        time.sleep(0.1)
    if debug: print 'Child terminated'

# send a command to the child
def child_send_command (command, proc ):
    if debug: print "To child: "+ command
    proc.stdin.write(command)

# not used
def child_get_status():
    opp=proc.stdout.readline()
    print "From child:  " + opp


# PIPE TO OMXPLAYER

def omx_send_control(char,fifo):
    command="echo -n " + char + " > " + fifo
    if debug: print "To omx: " + command
    os.system(command)

def parsePlayBackCommand():
    print "not implemented"

def getPlaybackCommand():
    #sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    #sock.bind((Up, portNum))
    time.sleep(10)
    return 'q'

def lookupPlaybackCommand(command):
    return

def shufflePlaylist(startKey):
    global SHUFFLE_PLAYLIST, PLAYLIST
    SHUFFLE_PLAYLIST = list(PLAYLIST)
    if debug: print SHUFFLE_PLAYLIST
    shuffle(SHUFFLE_PLAYLIST)
    #place starting file in first position
    SHUFFLE_PLAYLIST[SHUFFLE_PLAYLIST.index(startKey)] = SHUFFLE_PLAYLIST[0]
    SHUFFLE_PLAYLIST[0] = startKey
    if debug: print SHUFFLE_PLAYLIST

def playNextSong(songOrVideo, playlist, playlistPosition, options):
    if debug: print songOrVideo, playlist, playlistPosition, options
    if (songOrVideo == 's'):
        songTitle, filePath, albumTitle, artistName = getSongInfo(playlist[playlistPosition])
    elif (songOrVideo == 'v'):
        videoTitle, filePath, release_date, category = getVideoInfo(playlist[playlistPosition])
    else:
        print "Unknown songOrVideo value: " + songOrVideo + "\n"
    if (filePath):
        filePath = filePath.replace(" ", "\ ")
        if debug: print filePath
        proc, fifo = playFile(options, filePath)
        return proc, fifo

def playFile(omxoptions, track):
    # current working directory
    cwd= os.getcwd()

    # create a FIFO to be used between the parent and omxplayer
    fifo = "/tmp/omxcmd"
    if os.path.exists (fifo): os.system("rm " + fifo )
    os.system("mkfifo " + fifo )

    # start omxchild the child process that will run omxplayer
    proc=child_start(cwd)

    #send a 'track' command to the child to start the video
    child_send_command("track " + omxoptions + " " + track + "\n", proc )

    # and send the start command to omxplayer through the FIFO
    omx_send_control('.',fifo)
    omx_send_control('1',fifo)


    return proc, fifo


def sendCommandForPlayback(proc, fifo, key):

    # has the child process terminated because the track has ended
    if child_running(proc) == False:
            if debug: print "child now not running"
            # close the FIFO
            os.system("rm " + fifo)
            return
        
    # potential race condition here as omxplayer may terminate here
    # so sending the control will fail

    if key != None:
        # send the control to omxplayer through the FIFO
        omx_send_control(key,fifo)
        if key == 'q':
            # q has been sent and omxplayer is terminating, wait for child to terminate
            # and then close the FIFO
            child_wait_for_terminate(proc)
            os.system("rm " + fifo)
            return
        
    time.sleep(0.1)

#---------------------------------------------------------------------------------------------------------------------

#Main functions
BUFFER_SIZE = 512
##compiled regexes
readCommandRegex = re.compile('\[(?P<opCode>\\d+)\](?P<arguments>\[.*\])')
newPlaylistRegex = re.compile('\[(?P<songOrVideo>[sv])\]\[(?P<ids>\\S+)\]\[(?P<startKey>\\d+)\]\[(?P<options>\\w*)\]')
playlistArgsRegex = re.compile('\[(?P<playlistCommand>\\d)\](?P<rest>.*)')

"""
playlist commands
[0][0][song/video][list of ints, $ separated][starting position][options]
[0][1]
[0][2]
"""
PLAYLIST_COMMAND = "0"
PLAY_NEW_PLAYLIST = "0"
PREVIOUS_FILE = "1"
NEXT_FILE = "2"

"""
playback commands
[1][command]
"""
PLAYBACK_COMMAND = "1"
PAUSEPLAY = "0"
FAST_FORWARD = "1"
REWIND = "2"

"""
request current playing file info
[2][s/v]
"""
CURRENT_PLAYBACK = "2"

"""
Playlist setting
[3][command]
"""
PLAYLIST_SETTING = "3"
REPEAT_ON = "0"
REPEAT_OFF = "1"
SHUFFLE_ON = "2"
SHUFFLE_OFF = "3"

"""
Media Player Configs
"""
SHUFFLE = False
REPEAT = False
PLAYLIST = []
SHUFFLE_PLAYLIST = []
PLAYLIST_POSITION = 0
SONG_OR_VIDEO = ""
CURRENT_FILE_ID = ""
PROC = None
FIFO = None

def readCommand(commandMessage, regex):
    match = regex.match(commandMessage)
    if (match):
        if debug: print 'opCode' + match.group('opCode') + 'arguments' + match.group('arguments')
        return match.group('opCode'), match.group('arguments')
    else:
        print "Didn't match regex. Unknown command format:\n" + commandMessage + "\n"
        return "unknown", "unknown"

def parseNewPlaylistArguments(arguments, regex):
    match = regex.match(arguments)
    if (match):
        if debug: print 'songOrVideo' + match.group('songOrVideo') + 'ids' + match.group('ids') + 'startKey' + match.group('startKey') + 'options' + match.group('options')
        return match.group('songOrVideo'), match.group('ids'), match.group('startKey'), match.group('options')
    else:
        print 'bad format for arguments for play new playlist:\n' + arguments + '\n'
        return "unknown", "unknown", "unknown", "unknown"

def parsePlaylistCommand(arguments, regex):
    match = regex.match(arguments)
    if (match):
        if debug: print 'playlist command' + match.group('playlistCommand') + 'rest' + match.group('rest') + '\n'
        return match.group('playlistCommand'), match.group('rest')
    else:
        print 'bad playlist argument format' +'\n' + arguments

def initialisePlaybackCommandsDict():
    commandsDict = {
        "4": "1", #decrease speed
        "3": "2", #increase speed
        "2": "'<'", #rewind
        "1": "'>'", #fast forward
        "9": "q", #exit omxplayer
        "0": "p", #pause\resume
        
        #unused currently
        "11": "-", #decrease volume
        "12": "+", #increase volume
        "5": "z", #show info
        "6": "s", #toggle subtitles
        "7": "w", #show subtitles
        "8": "x", #hide subtitles
    }
    return commandsDict    

def getCurrentPlayback(songOrVideo, id, sendAddr, songOrVideoApp):
    songTitle = None
    videoTitle = None
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    if id != 0 and songOrVideo == songOrVideoApp:
        if (songOrVideo == 's'):
            songTitle, filePath, albumTitle, artistName = getSongInfo(id);
        elif (songOrVideo == 'v'):
            videoTitle, filePath, release_date, category = getVideoInfo(id);
        else:
            print 'Unknown song or video: ' + songOrVideo
        if (videoTitle or songTitle):
            
            if (videoTitle):
                message = videoTitle + "$" + category + '$' + str(release_date) + '$'
            else:
                message = songTitle+ '$' + artistName + '$' + albumTitle + '$' + str(SHUFFLE) + '$' + str(REPEAT) + '$'
    else:
        message = "Nothing$Nothing$Nothing$" + str(SHUFFLE) + '$' + str(REPEAT) + '$'
    sock.sendto(message, sendAddr)
    sock.close()

def processCommand(command, arguments, playBackDict, proc, fifo, senderAddr):
    global PROC, FIFO, PLAYLIST_POSITION, PLAYLIST, SHUFFLE_PLAYLIST, SONG_OR_VIDEO, SHUFFLE, REPEAT
    if debug: print command, arguments
    if (command == PLAYLIST_COMMAND):
        if debug: print 'in playlist command'
        playlistCommand, playlistCommandArgs = parsePlaylistCommand(arguments, playlistArgsRegex)
        if (playlistCommand == PLAY_NEW_PLAYLIST):
            #if there is a file playing, stop it first
            if (proc is not None and child_running(proc)):
                sendCommandForPlayback(proc, fifo, "q")
            songOrVideo, listOfSongs, startKey, options = parseNewPlaylistArguments(playlistCommandArgs, newPlaylistRegex)
            PLAYLIST = listOfSongs.split("$")
            return True, songOrVideo, startKey, options
        elif (playlistCommand == PREVIOUS_FILE):
            if debug: print 'Shuffle is', SHUFFLE
            sendCommandForPlayback(proc, fifo, "q")
            if (PLAYLIST_POSITION -1 >= 0):
                PLAYLIST_POSITION -= 1
            else:
                #wrap around
                PLAYLIST_POSITION = len(PLAYLIST) - 1
            if (SHUFFLE):
                PROC, FIFO = playNextSong(SONG_OR_VIDEO, SHUFFLE_PLAYLIST, PLAYLIST_POSITION, "")
            else:
                PROC, FIFO = playNextSong(SONG_OR_VIDEO, PLAYLIST, PLAYLIST_POSITION, "")
            return False, "previous", 1, ""
        elif (playlistCommand == NEXT_FILE):
            if debug: print 'Shuffle is', SHUFFLE
            sendCommandForPlayback(proc, fifo, "q")
            if (PLAYLIST_POSITION < len(PLAYLIST) - 1):
                PLAYLIST_POSITION += 1
            else:
                #wrap around
                PLAYLIST_POSITION = 0
            if (SHUFFLE):
                print SHUFFLE_PLAYLIST, PLAYLIST_POSITION
                PROC, FIFO = playNextSong(SONG_OR_VIDEO, SHUFFLE_PLAYLIST, PLAYLIST_POSITION, "")
            else:
                PROC, FIFO = playNextSong(SONG_OR_VIDEO, PLAYLIST, PLAYLIST_POSITION, "")
            return False, "next", 1, ""
        else:
            print 'Unknown playlist command\n'

    elif (command == PLAYBACK_COMMAND):
        if debug: 'in playback command'
        argString = arguments.translate(None, '[]')
        if (len(argString) == 1):
            if (playBackDict.has_key(argString) and proc is not None and child_running(proc)):
                sendCommandForPlayback(proc, fifo, playBackDict[argString])
        else:
            print "bad format on argument for playback command:\n" + argString + "\n"
        return False, "", 1, ""
    
    elif (command == CURRENT_PLAYBACK):
        id = 0;
        songOrVideoApp = arguments.translate(None, '[]')
        if (SHUFFLE):
            if SHUFFLE_PLAYLIST:
                id = SHUFFLE_PLAYLIST[PLAYLIST_POSITION]
        else:
            if PLAYLIST:
                id = PLAYLIST[PLAYLIST_POSITION]
        getCurrentPlayback(SONG_OR_VIDEO, id, senderAddr, songOrVideoApp)
        return False, "", 1, ""
    elif (command == PLAYLIST_SETTING):
        argString = arguments.translate(None, '[]')
        if (argString == REPEAT_ON):
            REPEAT = True
            return False, "repeat on", 0, ""
        elif (argString == REPEAT_OFF):
            REPEAT = False
            return False, "repeat off", 0, ""
        elif (argString == SHUFFLE_ON):
            SHUFFLE = True
            shufflePlaylist(PLAYLIST[PLAYLIST_POSITION])
            PLAYLIST_POSITION = 0
            return False, "shuffle on", 0, ""
        elif (argString == SHUFFLE_OFF):
            PLAYLIST_POSITION = PLAYLIST.index(SHUFFLE_PLAYLIST[PLAYLIST_POSITION])
            SHUFFLE = False
            if debug: print 'Shuffle off, playlist is', PLAYLIST, PLAYLIST_POSITION
            return False, "shuffle off", 0, ""
        else:
            print "unrecognized command for playlist setting\n" + command + '\n'
            return False, "unknown playlist", 0, ""
    else:
        if debug: print "in don't know what"
        return False, "unknown command", 1, ""

def check_runnable():
    # current working directory
    cwd= os.getcwd()
    
    path = cwd + "/omxchild.sh"
    if os.path.exists(path) == False:
	print "omxchild.sh not found, must be in working directory"
        sys.exit()
    command = "chmod +x " + path
    os.system(command) 

def setupUDP(Ip, portNum):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((Ip, portNum))
    return sock

def main():
    global PROC, FIFO, PLAYLIST, SHUFFLE_PLAYLIST, PLAYLIST_POSITION, SONG_OR_VIDEO, CURRENT_FILE_ID, SHUFFLE, REPEAT
    if len(sys.argv) < 2:
        print "incorrect number of arguments. should be 2: IP and port"
    else:
        check_runnable()
        proc = None
        fifo = None
        ip = sys.argv[1]
        port = sys.argv[2]
        sock = setupUDP(ip, int(port))
        sock.settimeout(0.5)
        playbackCommandsDict = initialisePlaybackCommandsDict()
        
        while (True):
            #check if a song finished playing
            if (PROC is not None and not child_running(PROC)):
                #play the next song in the playlist
                if (PLAYLIST_POSITION < len(PLAYLIST) - 1):
                    if not REPEAT:
                        PLAYLIST_POSITION += 1
                else:
                    #restart the playlist
                    PLAYLIST_POSITION = 0
                if (SHUFFLE):
                    PROC, FIFO = playNextSong(SONG_OR_VIDEO, SHUFFLE_PLAYLIST, PLAYLIST_POSITION, "")
                else:
                     PROC, FIFO = playNextSong(SONG_OR_VIDEO, PLAYLIST, PLAYLIST_POSITION, "")
                    
            #check for new input
            try:
                message, addr = sock.recvfrom(BUFFER_SIZE)
                command, arguments = readCommand(message, readCommandRegex)
                playNewPlaylist, tempSongOrVideo, startKey, options = processCommand(command, arguments, playbackCommandsDict, PROC, FIFO, addr)
                if (tempSongOrVideo == 's' or tempSongOrVideo == 'v'):
                    SONG_OR_VIDEO = tempSongOrVideo
                if (playNewPlaylist):
                    if (SHUFFLE):
                        shufflePlaylist(startKey)
                        PLAYLIST_POSITION = 0
                    else:
                        PLAYLIST_POSITION = PLAYLIST.index(startKey)
                    if (SHUFFLE):
                        PROC, FIFO = playNextSong(SONG_OR_VIDEO, SHUFFLE_PLAYLIST, PLAYLIST_POSITION, options)
                    else:
                        PROC, FIFO = playNextSong(SONG_OR_VIDEO, PLAYLIST, PLAYLIST_POSITION, options)
            except socket.timeout:
                #this is going to happen a lot
                pass
            

    sock.close()
        
def testLists():
    listS = ['1', '2', '3', '4']
    print listS.index('3')
    print len(listS)
    


if __name__ == '__main__':
    main()
    #testLists()
    #db_test()
    #mediaFilePath = "/home/pi/videos/song.mp3"
    #mediaFilePath = "/home/pi/media_storage/03\ -\ Alive.mp3"
    #mediaFilePath = "/home/pi/media_storage/03\\ -\\ Alive.mp3"
    #proc, fifo = playFile("", mediaFilePath)
    #time.sleep(100)
