package communication;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.logging.Level;

import shared.SongDescriptor;

public class SongForArtistQueryResponderThread extends QueryResponderThread {

	public SongForArtistQueryResponderThread(Integer arg, InetAddress destIp, Integer destPort) {
		super(arg, destIp, destPort);
	}
	
	public void run() {
		getLog().log(Level.INFO, "Starting SongForArtistQueryResponderThread with argument " + getArg());
		LinkedList<SongDescriptor> songs = getCoreDataAccess().getSongsForArtist((Integer) getArg());
		sendListViaUdp(songs, getDestIp(), getDestPort());
	}

}
