package communication;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * 
 * @author Daniel
 * Class waits for a query from the Android app, responds to the query, then resumes waiting mode
 */
public class MediaQueriesThread extends Thread{
	private InetAddress appAddress;
	private int appPort;
	private int piPort;
	private DatagramSocket sock;
	private static final Logger log = Logger.getLogger(MediaQueriesThread.class.getName());
	private final int defaultBufSize = 256;
	UDPHelper udpHelper;
	
	public MediaQueriesThread(InetAddress appAddress, int appPort, int piPort) {
		this.appAddress = appAddress;
		this.appPort = appPort;
		this.piPort = piPort;
		udpHelper = new UDPHelper(piPort);
		try {
			sock = new DatagramSocket(piPort);
		} catch (SocketException e) {
			getLog().warning("Error creating Datagram Socket on port " + piPort + "\n" + e.getMessage());
		}
	}
	
	/**
	 * Enter receiving mode. Wait for a query from the app, and extract the query from the message
	 */
	private String receiveQuery() {
		byte buf[] = new byte[defaultBufSize];
		try {
			buf = udpHelper.receive(defaultBufSize);
		} catch (IOException e) {
			getLog().warning("Error receiving Datagram packet on port " + piPort + "\n" + e.getMessage());
		}
		String message = new String(buf, StandardCharsets.UTF_8);
		return message;
	}
	
	private void parseQuery(String query) {
		//switch/case handing off operations to other methods
		//other methods will hand off to database lookups, then format the results
	}
	
	
	private Logger getLog() {
		return log;
	}
	
	public void run() {
		/*
		 * start listening, and switch back and forth between listening and sending
		 */
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
