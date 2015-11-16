package communication;

import java.net.InetAddress;
import java.util.LinkedList;

import Descriptors.ArtistDescriptor;

public class ArtistQueryResponderThread extends QueryResponderThread {

	/**\
	 * 
	 * @param arg
	 * @param destIp
	 * @param destPort
	 */
	public ArtistQueryResponderThread(Integer arg, InetAddress destIp, Integer destPort) {
		super(arg, destIp, destPort);
	}
	
	@Override
	public void run() {
		LinkedList<ArtistDescriptor> artists = getCoreDataAccess().getArtists();
		sendListViaUdp(artists, getDestIp(), getDestPort());

	}

}
