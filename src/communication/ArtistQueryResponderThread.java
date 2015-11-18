package communication;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.logging.Level;

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
		getLog().log(Level.INFO, "Starting ArtistQueryResponderThread with argument " + getArg());
		LinkedList<ArtistDescriptor> artists = getCoreDataAccess().getArtists();
		sendListViaUdp(artists, getDestIp(), getDestPort());

	}

}
