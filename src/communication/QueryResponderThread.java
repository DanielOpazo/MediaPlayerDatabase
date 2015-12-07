package communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import database.CoreDataAccess;

public abstract class QueryResponderThread extends Thread{

	private static final Logger log = Logger.getLogger(QueryResponderThread.class.getName());
	private Object arg;//this can be whatever the subclass needs it to be. so far Integer and String. the subclass casts what it needs onto this argument
	private InetAddress destIp;
	private Integer destPort;
	private CoreDataAccess cda;
	private Socket sock;
	
	public QueryResponderThread(Object arg, InetAddress destIP, Integer destPort) {
		this.arg = arg;
		this.destIp = destIP;
		this.destPort = destPort;
		Boolean streamConnected = false;
		cda = new CoreDataAccess();
		while (!streamConnected) {
			try {
				sock = new Socket(destIP, destPort);
				streamConnected = true;
			} catch (SocketException e) {
				getLog().log(Level.SEVERE, "Could not create socket for QueryResponderThread",e);
				getLog().log(Level.INFO, "Trying again to create TCP socket");
			} catch (IOException ioe) {
				getLog().log(Level.SEVERE, "Error creating TCP socket", ioe);
			}
		}
	}
	

	/**
	 * 
	 * @param descriptorList a list of the descriptors to be sent
	 * @param destIP where to send
	 * @param destPort what port to send to
	 */
	public <T> void sendListViaUdp(LinkedList<T> descriptorList, InetAddress destIP, Integer destPort) {
		try {
			if (getSock() != null) {
				OutputStream oStream = getSock().getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(oStream);
				oos.writeObject(descriptorList);
				oos.close();
				oStream.close();
			}
		} catch (IOException e) {
			getLog().log(Level.SEVERE, "error sending serialized list via TCP", e);
		}finally {
			if (getSock() != null)
				try {
					getSock().close();
				} catch (IOException e) {
					getLog().log(Level.SEVERE, "Error closing tcp socket", e);
				}
		}
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


	public Socket getSock() {
		return sock;
	}


	public void setSock(Socket sock) {
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
	}

}
