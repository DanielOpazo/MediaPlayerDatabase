package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Daniel
 * Class waits for a query from the Android app, responds to the query, then resumes waiting mode
 */
public class MediaQueriesThread extends Thread{
	private int piPort;
	private DatagramSocket recvSock;
	private static final Logger log = Logger.getLogger(MediaQueriesThread.class.getName());
	private final int defaultBufSize = 1024;
	UDPHelper udpHelper;
	public enum queryCode {
		SONGS_FOR_ALBUM_QUERY, SONGS_FOR_ARTIST_QUERY, SONGS_QUERY, ALBUMS_FOR_ARTIST_QUERY, ALBUMS_QUERY, ARTISTS_QUERY, VIDEOS_QUERY, VIDEOS_FOR_CATEGORY_QUERY, CATEGORIES_QUERY, UNKNOWN_QUERY;
	}
	private Hashtable<Integer, queryCode> codeLookup;
	private final String QUERY_REGEX = "\\[(\\d)\\]\\[(\\w*)\\]";  
	
	public MediaQueriesThread(DatagramSocket recvSock) {
		codeLookup = new Hashtable<Integer, queryCode>();
		initializeCodeLookupTable(codeLookup);
		this.recvSock = recvSock;
	}
	
	private void initializeCodeLookupTable(Hashtable<Integer, queryCode> codeLookupTable) {
		codeLookup.put(0, queryCode.SONGS_FOR_ALBUM_QUERY);
		codeLookup.put(1, queryCode.SONGS_FOR_ARTIST_QUERY);
		codeLookup.put(2, queryCode.SONGS_QUERY);
		codeLookup.put(3, queryCode.ALBUMS_FOR_ARTIST_QUERY);
		codeLookup.put(4, queryCode.ALBUMS_QUERY);
		codeLookup.put(5, queryCode.ARTISTS_QUERY);
		codeLookup.put(6, queryCode.VIDEOS_QUERY);
		codeLookup.put(7, queryCode.VIDEOS_FOR_CATEGORY_QUERY);
		codeLookup.put(8, queryCode.CATEGORIES_QUERY);
	}

	/**
	 * Enter receiving mode. Wait for a query from the app, and extract the query from the message
	 * This method doesn't care what IP address is accessing it. it just responds to whoever sent the request
	 * @param destIP the method will record the sender's ip address in this variable
	 * @param destPort the method will record the sender's port number in this variable
	 */
	private AddressPortMessageTuple receiveQuery(DatagramSocket sock) {
		byte buf[] = new byte[defaultBufSize];
		AddressPortMessageTuple apt = new AddressPortMessageTuple();
		try {
			//Receive packet
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			sock.receive(pack);
			
			//get info of sender
			apt.addr = pack.getAddress();
			apt.portNum = pack.getPort();
		} catch (IOException e) {
			getLog().warning("Error receiving or sending Datagram packet on port " + piPort + "\n" + e.getMessage());
		}
		String message = new String(buf, StandardCharsets.UTF_8);
		apt.message = message;
		return apt;
	}
	
	private queryCode getOpCode(String message, Hashtable<Integer, queryCode> codeLookup) {
		Integer intCode = 0;
		String strOpCode = getOpCodeOrArgument(message, true);
		if (strOpCode == null) {
			getLog().warning("Could not find opcode in message: " + message);
			return queryCode.UNKNOWN_QUERY;
		}
		try {
			intCode = Integer.parseInt(getOpCodeOrArgument(message, true));
		}catch (NumberFormatException e) {
			getLog().warning("Could not parse Integer opcode for message " + message);
		}
		queryCode opCode = codeLookup.get(intCode);
		if (opCode == null) {
			return queryCode.UNKNOWN_QUERY;
		}
		return opCode;
	}
	
	private String getStringArgument(String message) {
		String str = getOpCodeOrArgument(message, false);
		if (str == null) getLog().warning("Argument in video query was null: " + message);
		return str;
	}
	
	private Integer getIntegerArgument(String message) {
		Integer arg = 0;
		String strArg = getOpCodeOrArgument(message, false);
		/*
		 * Returning 0 here means an invalid argument won't break the program, but the downside
		 * is that it will fail pretty silently. Need to find a way to fail in a way that notifies the app.
		 */
		if (strArg == null) {
			getLog().warning("Could not find Integer argument: " + message);
			/*
			 * I return 0 instead of null because returning null make a query return every song/album there is, 
			 * whereas 0 will make the queries return nothing
			 */
			return 0; 
		}
		try {
			arg = Integer.parseInt(getOpCodeOrArgument(message, false));
		}catch (NumberFormatException e) {
			getLog().warning("Could not parse Integer argument for message " + message);
		}
		return arg;
	}
	
	/**
	 * 
	 * @param message
	 * @param opCode if true -> find op code. if false -> find argument
	 * @return null if it can't find what it's looking for
	 */
	private String getOpCodeOrArgument(String message, boolean opCode) {
		Pattern basicPattern = Pattern.compile(QUERY_REGEX);
		Matcher matcher = basicPattern.matcher(message);
		if (matcher.find()) {
			if (opCode) {
				return matcher.group(1);
			}else {
				return matcher.group(2);
			}
		}else {
			getLog().warning("pattern " + message + "did not match the regex");
			return null;
		}
	}
	
	/**
	 * 
	 * @param message The query message sent from the Android App
	 * @param codeLookup table linking the op codes to an enum
	 * message formats:
	 * 	SONGS_FOR_ALBUM_QUERY: [0][album id]
	 *  SONGS_QUERY: [2][]
	 *  ALBUMS_FOR_ARTIST_QUERY: [3][artist id]
	 *  ALBUMS_QUERY: [4][]
	 *  ARTIST_QUERY: [5][]
	 *  VIDEOS_QUERY: [6][]
	 *  VIDEOS_FOR_CATEGORY [7][category]
	 *  CATEGORIES: [8][]
	 *  
	 */
	private void parseQuery(String message, Hashtable<Integer, queryCode> codeLookup, InetAddress destIP, Integer destPort) {
		//switch/case handing off operations to other methods
		//other methods will hand off to database lookups, then format the results
		queryCode opCode = getOpCode(message, codeLookup);
		getLog().log(Level.INFO, message);
		
		switch (opCode) {
			case SONGS_FOR_ALBUM_QUERY:
				Integer albumId = getIntegerArgument(message);
				SongQueryResponderThread songForAlbumResponder = new SongQueryResponderThread(albumId, destIP, destPort);
				songForAlbumResponder.start();
				break;
			case SONGS_FOR_ARTIST_QUERY:
				Integer artistIdForSong = getIntegerArgument(message);
				SongForArtistQueryResponderThread songForArtistResponder = new SongForArtistQueryResponderThread(artistIdForSong, destIP, destPort);
				songForArtistResponder.start();
				break;
			case SONGS_QUERY:
				SongQueryResponderThread songResponder = new SongQueryResponderThread(null, destIP, destPort);
				songResponder.start();
				break;
			case ALBUMS_FOR_ARTIST_QUERY:
				Integer artistId = getIntegerArgument(message);
				AlbumQueryResponderThread albumForArtistResponder = new AlbumQueryResponderThread(artistId, destIP, destPort);
				albumForArtistResponder.start();
				break;
			case ALBUMS_QUERY:
				AlbumQueryResponderThread albumResponder = new AlbumQueryResponderThread(null, destIP, destPort);
				albumResponder.start();
				break;
			case ARTISTS_QUERY:
				ArtistQueryResponderThread artistsResponder = new ArtistQueryResponderThread(null, destIP, destPort);
				artistsResponder.start();
				break;
			case VIDEOS_QUERY:
				VideoQueryResponderThread videoResponder = new VideoQueryResponderThread(null, destIP, destPort);
				videoResponder.start();
				break;
			case VIDEOS_FOR_CATEGORY_QUERY:
				String category = getStringArgument(message);
				VideoQueryResponderThread videoForCategoryResponder = new VideoQueryResponderThread(category, destIP, destPort);
				videoForCategoryResponder.start();
				break;
			case CATEGORIES_QUERY:
				CategoryQueryResponderThread categoryResponder = new CategoryQueryResponderThread(null, destIP, destPort);
				categoryResponder.start();
				break;
			case UNKNOWN_QUERY:
				getLog().warning("Unknown Query code " + message);
				break;
			default:
				//how did this happen. I used an enum.
				break;
		}	
	}
	
	
	
	public DatagramSocket getRecvSock() {
		return recvSock;
	}

	public void setRecvSock(DatagramSocket recvSock) {
		this.recvSock = recvSock;
	}

	private Logger getLog() {
		return log;
	}
	
	/**
	 * Basically a struct.
	 * Used to return three values from the method that received query packets,
	 *and then pass the values to the query parser
	 * @author Daniel
	 *
	 */
	private class AddressPortMessageTuple {
		private InetAddress addr;
		private int portNum;
		private String message;
	}
	
	/**
	 * start listening, hand off queries to new threads and resume listening 
	 */
	public void run() {
		getLog().log(Level.INFO, "Starting MediaQueryThread");
		while (true) {
			AddressPortMessageTuple apmt = receiveQuery(getRecvSock());
			getLog().info("Received query: " + apmt.message);
			parseQuery(apmt.message, codeLookup, apmt.addr, apmt.portNum);
		}
	}
	
	public static void main(String[] args) {
		DatagramSocket sock = null;
		try {
			sock = new DatagramSocket(8008);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MediaQueriesThread mqt = new MediaQueriesThread(sock);
		mqt.start();
	}

}
