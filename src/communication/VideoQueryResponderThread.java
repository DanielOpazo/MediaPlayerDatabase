package communication;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.logging.Level;

import shared.VideoDescriptor;

public class VideoQueryResponderThread extends QueryResponderThread {

	/**
	 * 
	 * @param arg
	 * @param destIp
	 * @param destPort
	 */
	public VideoQueryResponderThread(String arg, InetAddress destIp, Integer destPort) {
		super(arg, destIp, destPort);
	}
	
	public void run() {
		getLog().log(Level.INFO, "Starting VideoQueryResponderThread with argument " + getArg());
		LinkedList<VideoDescriptor> videos = getCoreDataAccess().getVideosForCategory((String) getArg());
		sendListViaUdp(videos, getDestIp(), getDestPort());
	}
}
