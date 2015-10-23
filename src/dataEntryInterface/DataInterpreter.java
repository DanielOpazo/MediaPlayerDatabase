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
	
	/**
	 * 
	 * @return the value of the title field. Null if no value, or value is ""
	 */
	public String readTitle() {
		String title = getValueOrNull(titleKey);
		if (title == null || title.length() > maxTitleLength) {
			getLog().warning("Invalid title");
			return null;
		}
		return title;
	}
	
	/**
	 * 
	 * @return the value of the title field. 
	 */
	public String readArtist() {
		String artist = getValueOrNull(artistKey);
		if (artist != null) {
			return artist;
		}
		return "";
	}
	
	/**
	 * 
	 * @param key The key of the value to be pulled from the hash
	 * @return the value corresponding to the key. Null if empty or blank
	 */
	private String getValueOrNull(String key) {
		String value = getFileData().getValuesHash().get(key);
		if (value != null && !value.equalsIgnoreCase("")) {
			return value;
		}else {
			getLog().warning("Invalid " + key + ": " + "value is null or empty. Returning null value");
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
