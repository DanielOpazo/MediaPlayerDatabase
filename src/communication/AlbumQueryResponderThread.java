package communication;

import java.net.InetAddress;
import java.util.LinkedList;

import Descriptors.AlbumDescriptor;

public class AlbumQueryResponderThread extends QueryResponderThread {

	/**\
	 * 
	 * @param arg the artist id of the artist whose albums are requested
	 * @param destIp
	 * @param destPort
	 */
	public AlbumQueryResponderThread(Integer arg, InetAddress destIp, Integer destPort) {
		super(arg, destIp, destPort);
	}
	@Override
	public void run() {
		LinkedList<AlbumDescriptor> albums = getCoreDataAccess().getAlbumsForArtist((Integer) getArg());
		sendListViaUdp(albums, getDestIp(), getDestPort());

	}

}
