package communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import Descriptors.SongDescriptor;
import communication.MediaQueriesThread.queryCode;
import database.CoreDataAccess;

public abstract class QueryResponderThread extends Thread{

	private static final Logger log = Logger.getLogger(QueryResponderThread.class.getName());
	private Object arg;//this can be whatever the subclass needs it to be. so far Integer and String. the subclass casts what it needs onto this argument
	InetAddress destIp;
	Integer destPort;
	private CoreDataAccess cda;
	DatagramSocket sock;
	private final int default_packet_size = 256;
	
	public QueryResponderThread(Object arg, InetAddress destIP, Integer destPort) {
		this.arg = arg;
		this.destIp = destIP;
		this.destPort = destPort;
		cda = new CoreDataAccess();
		try {
			sock = new DatagramSocket();
		} catch (SocketException e) {
			getLog().warning("Could not create socket for QueryResponderThread\n" + e.getMessage());
		}
	}
	

	/**
	 * method takes in a list of descriptors, serialized them, and transmits them 1 by 1
	 * after transmitting a descriptor, it waits for an ack from the destination, then sends the enxt packet
	 * @param descriptorList a list of the descriptors to be sent
	 * @param destIP where to send
	 * @param destPort what port to send to
	 */
	public <T> void sendListViaUdp(LinkedList<T> descriptorList, InetAddress destIP, Integer destPort) {
		byte[] sendBuf = null;
		byte[] recvBuf = new byte[default_packet_size];
		DatagramPacket sendPack = new DatagramPacket(sendBuf, sendBuf.length, destIP, destPort);
		DatagramPacket recvPack = new DatagramPacket(recvBuf, recvBuf.length);
		for (T item: descriptorList) {
			//put T into buf. serialization goes here
			sendBuf = serializeDescriptor(item);
			try {
				getSock().send(sendPack);
				//wait for ack
				sock.receive(recvPack); //implement a timeout here, and resend after
				//is it even worth parsing the ack packet?
			} catch (IOException e) {
				getLog().warning("error sending or receiving packet\n" + e.getMessage());
			}
		}
	}
	
	/**
	 * 
	 * @param descriptor the object to be serialized. must implement serializeable
	 * @return
	 */
	private <T> byte[] serializeDescriptor(T descriptor) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(descriptor);
			oos.close();
			byte[] obj = baos.toByteArray();
			baos.close();
			return obj;
		}catch (Exception e) {
			getLog().warning("error with serialization\n" + e.getMessage());
		}
		return null;
	}
	
	public abstract void run();
	
	public InetAddress getDestIp() {
		return destIp;
	}


	public void setDestIp(InetAddress destIp) {
		this.destIp = destIp;
	}


	public Integer getDestPort() {
		return destPort;
	}


	public void setDestPort(Integer destPort) {
		this.destPort = destPort;
	}


	public CoreDataAccess getCoreDataAccess() {
		return cda;
	}


	public void setCoreDataAccess(CoreDataAccess cda) {
		this.cda = cda;
	}


	public DatagramSocket getSock() {
		return sock;
	}


	public void setSock(DatagramSocket sock) {
		this.sock = sock;
	}


	public static Logger getLog() {
		return log;
	}


	public Object getArg() {
		return arg;
	}
	public void setArg(Object arg) {
		this.arg = arg;
	}
	
	
	public static void main(String[] args) {
		LinkedList<SongDescriptor> list = new LinkedList<SongDescriptor>();
		SongDescriptor s1 = new SongDescriptor("Stairway To Heaven", "Led Zeppelin", "Led Zeppelin IV", "1971", "5", 1);
		SongDescriptor s2 = new SongDescriptor("Let It Be", "The Beatles", "Let It Be", "1968", "7", 2);
		SongDescriptor s3 = new SongDescriptor("Accross The Universe", "The Beatles", "The White Album", "1968", "1", 3);
		SongDescriptor s4 = new SongDescriptor("Black Dog", "Led Zeppelin", "Led Zeppelin IV", "1971", "8", 4);
		SongDescriptor s5 = new SongDescriptor("A Day In The Life", "The Beatles", "Let It Be", "1970", "3", 5);
		list.add(s1);
		list.add(s2);
		list.add(s3);
		list.add(s4);
		list.add(s5);
		System.out.println(s5.getClass().getName());
		byte[] obj = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(list);
			oos.close();
			obj = baos.toByteArray();
			baos.close();
		}catch (Exception e) {
			getLog().warning("error with serialization\n" + e.getMessage());
		}
		InetAddress kais = null;
		try {
			kais = InetAddress.getByName("192.168.0.20");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatagramPacket sendPack = new DatagramPacket(obj, obj.length, kais, 8008);
		System.out.println(obj.length);
		DatagramSocket sock = null;
		try {
			sock = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sock.send(sendPack);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
