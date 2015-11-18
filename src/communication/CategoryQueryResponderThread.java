package communication;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.logging.Level;

import shared.CategoryDescriptor;


public class CategoryQueryResponderThread extends QueryResponderThread {

	/**
	 * 
	 * @param arg unused
	 * @param destIp
	 * @param destPort
	 */
	public CategoryQueryResponderThread(Object arg, InetAddress destIp, Integer destPort) {
		super(arg, destIp, destPort);
	}
	
	public void run() {
		getLog().log(Level.INFO, "Starting CategoryQueryResponderThread with argument " + getArg());
		LinkedList<CategoryDescriptor> categories = getCoreDataAccess().getCategories();
		sendListViaUdp(categories, getDestIp(), getDestPort());
	}

}
