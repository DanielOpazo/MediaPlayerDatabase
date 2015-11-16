package communication;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;

import Descriptors.SongDescriptor;

public class SongQueryResponderThread extends QueryResponderThread {

	//for song, the arg is album id
	
	public SongQueryResponderThread(Integer arg, InetAddress destIp, Integer destPort) {
		super(arg, destIp, destPort);
	}
	
	public void run() {
		LinkedList<SongDescriptor> songs = getCoreDataAccess().getSongsForAlbum((Integer) getArg());
		sendListViaUdp(songs, getDestIp(), getDestPort());
	}
}
