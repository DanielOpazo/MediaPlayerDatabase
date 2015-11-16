package communication;

import java.net.InetAddress;
import java.util.LinkedList;

import Descriptors.VideoDescriptor;

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
		LinkedList<VideoDescriptor> videos = getCoreDataAccess().getVideosForCategory((String) getArg());
		sendListViaUdp(videos, getDestIp(), getDestPort());
	}
}
