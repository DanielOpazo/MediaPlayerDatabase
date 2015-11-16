package communication;

import java.net.InetAddress;
import java.util.LinkedList;

import Descriptors.CategoryDescriptor;


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
		LinkedList<CategoryDescriptor> categories = getCoreDataAccess().getCategories();
		sendListViaUdp(categories, getDestIp(), getDestPort());
	}

}
