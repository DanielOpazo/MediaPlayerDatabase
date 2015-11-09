package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * 
 * @author Daniel
 * Class contains an arraylist of Datagram packets. the send() method sends all the packets in the list.
 */
public class UDPHelper{
	
	private DatagramSocket socket;
	private ArrayList<DatagramPacket> sendPackets;
	
	private static final Logger log = Logger.getLogger(UDPHelper.class.getName());
	
	/**
	 * 
	 * @param sockPort port for the socket to bind to
	 */
	public UDPHelper(int sockPort) {
		sendPackets = new ArrayList<DatagramPacket>();
		try {
			socket = new DatagramSocket(sockPort);
		} catch (SocketException e) {
			getLog().warning("Error creating Datagram Socket on port " + sockPort + "\n" + e.getMessage());
		}
	}
	
	public void addPacket(DatagramPacket pack) {
		sendPackets.add(pack);
	}
	
	public void clear() {
		sendPackets.clear();
	}
	
	public void run() {
	}
	
	public void send() throws IOException {
		for (DatagramPacket pack: sendPackets) {
			socket.send(pack);
		}
	}
	
	/**
	 * 
	 * @param bufSize size of buffer for receiving packet
	 * @return the contents of the buffer from the received packet
	 * @throws IOException
	 */
	public byte[] receive(int bufSize) throws IOException {
		byte[] buf = new byte[bufSize];
		DatagramPacket pack = new DatagramPacket(buf, buf.length);
		socket.receive(pack);
		return buf;
	}
	
	public Logger getLog() {
		return log;
	}
	
	public static void main(String[] args) {
		InetAddress address = null;
		try {
			address = InetAddress.getByName("192.168.0.100");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buf = new byte[256];
		for (int i = 0; i < 256; i++) {
			buf[i] = 'c';
		}
		byte[] buf2 = new byte[256];
		for (int i = 0; i < 256; i++) {
			buf2[i] = 'd';
		}
		UDPHelper udpServer = new UDPHelper(5000);
		DatagramPacket pack = new DatagramPacket(buf, buf.length, address, 5050);
		DatagramPacket pack2 = new DatagramPacket(buf2, buf2.length, address, 5050);
		udpServer.addPacket(pack);
		udpServer.addPacket(pack2);
		try {
			udpServer.send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
