package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Hashtable;
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
	private final int defaultBufSize = 256;
	UDPHelper udpHelper;
	public enum queryCode {
		SONG_QUERY,ALBUM_QUERY,ARTIST_QUERY,VIDEO_QUERY, UNKNOWN_QUERY;
	}
	private Hashtable<Integer, queryCode> codeLookup;
	private final String QUERY_REGEX = "\\[(\\d)\\]\\[(\\d*)\\]";  
	
	public MediaQueriesThread(InetAddress appAddress, int appPort, int piPort) {
		this.piPort = piPort;
		udpHelper = new UDPHelper(piPort);
		codeLookup = new Hashtable<Integer, queryCode>();
		initializeCodeLookupTable(codeLookup);
		try {
			recvSock = new DatagramSocket(piPort);
		} catch (SocketException e) {
			getLog().warning("Error creating Datagram Socket on port " + piPort + "\n" + e.getMessage());
		}
	}
	
	private void initializeCodeLookupTable(Hashtable<Integer, queryCode> codeLookupTable) {
		codeLookup.put(1, queryCode.SONG_QUERY);
		codeLookup.put(2, queryCode.ALBUM_QUERY);
		codeLookup.put(3, queryCode.ARTIST_QUERY);
		codeLookup.put(4, queryCode.VIDEO_QUERY);
	}

	/**
	 * Enter receiving mode. Wait for a query from the app, and extract the query from the message
	 * Send ack packet back to the App for every packet received
	 * This method doesn't care what devide is accessing it. it just responds to whoever sent the request
	 */
	private String receiveQuery(DatagramSocket sock) {
		byte buf[] = new byte[defaultBufSize];
		try {
			//Receive packet
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			sock.receive(pack);
			
			//Send ack
			InetAddress ackAddress = pack.getAddress();
			int ackPort = pack.getPort();
			//need to make sure this pads with null
			byte[] ackBuf = Arrays.copyOf("ack".getBytes(), defaultBufSize);
			DatagramPacket ackPacket = new DatagramPacket(ackBuf, ackBuf.length, ackAddress, ackPort);
			sock.send(ackPacket);
		} catch (IOException e) {
			getLog().warning("Error receiving Datagram packet on port " + piPort + "\n" + e.getMessage());
		}
		String message = new String(buf, StandardCharsets.UTF_8);
		return message;
	}
	
	private queryCode getOpCode(String message, Hashtable<Integer, queryCode> codeLookup) {
		Integer intCode = 0;
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
	
	private Integer getArgument(String message) {
		Integer arg = 0;
		try {
			arg = Integer.parseInt(getOpCodeOrArgument(message, false));
		}catch (NumberFormatException e) {
			getLog().warning("Could not parse Integer argument for message " + message);
		}
		return arg;
	}
	
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
			return "0";
		}
	}
	
	/**
	 * 
	 * @param message The query message sent from the Android App
	 * @param codeLookup table linking the op codes to an enum
	 * message formats:
	 * 	SONG_QUERY: [1][album id]
	 *  ALBUM_QUERY: [2][artist id]
	 *  ARTIST_QUERY: [3][]
	 *  VIDEO_QUERY: [4][]
	 *  
	 */
	private void parseQuery(String message, Hashtable<Integer, queryCode> codeLookup) {
		//switch/case handing off operations to other methods
		//other methods will hand off to database lookups, then format the results
		queryCode opCode = getOpCode(message, codeLookup);
		switch (opCode) {
			case SONG_QUERY:
				break;
			case ALBUM_QUERY:
				break;
			case ARTIST_QUERY:
				break;
			case VIDEO_QUERY:
				break;
			case UNKNOWN_QUERY:
				break;
			default:
				//how did this happen. I used an enum.
				break;
		}	
	}
	
	
	private void handleSongQuery(String message) {
		Integer albumId = getArgument(message);
	}
	
	private void handleAlbumQuery(String message) {
		Integer artistId = getArgument(message);
	}
	
	private void handleArtistQuery(String message) {
		
	}
	
	private void handleVideoQuery(String message) {
		
	}
	private Logger getLog() {
		return log;
	}
	
	/**
	 * start listening, hand off queries to new threads and resume listening 
	 */
	public void run() {
		while (true) {
			String message = receiveQuery(recvSock);
			parseQuery(message, codeLookup);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
