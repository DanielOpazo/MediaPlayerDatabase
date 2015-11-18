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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import shared.SongDescriptor;
import communication.MediaQueriesThread.queryCode;
import database.CoreDataAccess;

public abstract class QueryResponderThread extends Thread{

	private static final Logger log = Logger.getLogger(QueryResponderThread.class.getName());
	private Object arg;//this can be whatever the subclass needs it to be. so far Integer and String. the subclass casts what it needs onto this argument
	InetAddress destIp;
	Integer destPort;
	private CoreDataAccess cda;
	DatagramSocket sock;
	private final int default_packet_size = 1024;
	
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
	 * 
	 * @param descriptorList a list of the descriptors to be sent
	 * @param destIP where to send
	 * @param destPort what port to send to
	 */
	public <T> void sendListViaUdp(LinkedList<T> descriptorList, InetAddress destIP, Integer destPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(default_packet_size);
		ObjectOutputStream oos = null;
		byte[] byteSerializedList = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(descriptorList);
			oos.close();
			byteSerializedList = baos.toByteArray();
			baos.close();
		}catch (IOException e) {
			getLog().log(Level.SEVERE, "Error with ObjectOutputStream", e);
		}
		int byteIndex = 0;
		//byte[] sendBuf = new byte[default_packet_size];
		ByteArrayOutputStream bos = new ByteArrayOutputStream(default_packet_size);
		byte[] recvBuf = new byte[default_packet_size];
		DatagramPacket recvPack = new DatagramPacket(recvBuf, recvBuf.length);
		DatagramPacket sendPack = null;
		int endOfArray = default_packet_size;
		while (byteIndex < byteSerializedList.length) {
			if (byteSerializedList.length <= default_packet_size) {
				byte[] smallPacket = new byte[byteSerializedList.length];
				System.arraycopy(byteSerializedList, byteIndex, smallPacket, 0, byteSerializedList.length);
				try {
					bos.write(smallPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				if (byteIndex + default_packet_size > byteSerializedList.length) {
					endOfArray = byteSerializedList.length - byteIndex;
				}
				byte[] sendBuf = new byte[default_packet_size];
				System.arraycopy(byteSerializedList, byteIndex, sendBuf, 0, endOfArray);
				try {
					bos.write(sendBuf);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sendPack = new DatagramPacket(bos.toByteArray(), bos.toByteArray().length, destIP, destPort);
			try {
				getSock().send(sendPack);
				//wait for ack
				getSock().receive(recvPack);
				getLog().log(Level.INFO, "Received: " + recvBuf.toString());
			}catch (IOException e) {
				getLog().log(Level.SEVERE, "Error with socket while transmitting serialized objects", e);
			}
			byteIndex += default_packet_size;
		}
		//If the buffer size is a multiple of 1024, send an empty packet to let the app know that information is done sending
		/*
		if (byteSerializedList.length % default_packet_size == 0) {
			sendBuf = new byte[default_packet_size];
			try {
				getSock().send(sendPack);
			} catch (IOException e) {
				getLog().log(Level.SEVERE, "error sending empty packet", e);
			}
		}
		*/
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
	
	public void finalize() {
		getSock().close();
	}
	
	public static void main(String[] args) {
		LinkedList<SongDescriptor> list = new LinkedList<SongDescriptor>();
		for (int i = 0 ;i < 40; i++) {
			list.add(new SongDescriptor("Song" + i, "The Beatles" + i, "Let It Be" + i, "1970" + i, "3" + i, 5));
		}
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName("192.168.0.17");
			byte[] byteAddr = "192.168.0.17".getBytes();
			addr = InetAddress.getByAddress(byteAddr);
			System.out.println(addr.toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SongQueryResponderThread sqrt = new SongQueryResponderThread(null, addr, 8000);
		sqrt.sendListViaUdp(list, addr, 8000);
		/*
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
		*/
		
		
		

	}

}
