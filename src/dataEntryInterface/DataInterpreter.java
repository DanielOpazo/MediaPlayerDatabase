package dataEntryInterface;

import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * 
 * @author Daniel
 * Class that reads the values from the hash created from the data in the metadata file.
 *
 */
public class DataInterpreter {
	private static final Logger log = Logger.getLogger(DataInterpreter.class.getName());
	private DataFromFile fileData;
	private final String titleKey = "title";
	private final int maxTitleLength = 30;
	private final String artistKey = "artist";
	private final String albumKey = "album";
	private final String trackNumberKey = "track";

	public DataInterpreter(String fileName) {
		setFileData(new DataFromFile(fileName));
	}
	
	public String readTitle() {
		String title = getFileData().getValuesHash().get(titleKey);
		if (title != null && !title.equalsIgnoreCase("")) {
			if (title.length() > maxTitleLength) {
				getLog().warning("Invalid title: title too long. Returning null title");
				return null;
			}
			return title;
		}else {
			getLog().warning("Invalid title: title is null or empty. Returning null title");
			return null;
		}
	}
	
	public DataFromFile getFileData() {
		return fileData;
	}

	public void setFileData(DataFromFile fileData) {
		this.fileData = fileData;
	}
	
	private Logger getLog() {
		return log;
	}
	public static void main(String[] args) {

	}

}
