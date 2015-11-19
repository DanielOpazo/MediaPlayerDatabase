package dataEntryInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.util.Date;

/**
 * 
 * @author Daniel
 * Class that reads the values from the hash created by fileData from the data in the metadata file.
 * Performs input validation on values read from file.
 * Stores the values in a SongFileInfo object, which is then passed to CoreDataAccess
 */
public class DataInterpreter {
	private static final Logger log = Logger.getLogger(DataInterpreter.class.getName());
	private DataFromFile fileData;
	private FileInfo fileInfo;
	private final String videoTitleKey = "vtitle";
	private final String songTitleKey = "mtitle";
	private final int maxTitleLength = 30;
	private final String artistKey = "artist";
	private final String albumKey = "album";
	private final String trackNumberKey = "track";
	private final String dateKey = "date";
	private final String categoryKey = "category";

	/**
	 * Immediately tries to get a FileData for the given fileName
	 * @param fileName containing the song data
	 */
	public DataInterpreter(String fileName) {
		setFileData(new DataFromFile(fileName));
		fileInfo = null;
	}
	
	/**
	 * Creates a SongFileInfo instance if the metadata file is for a song,
	 * or create a VideoFileInfo instance if the file is for a video.
	 */
	public void getValuesFromFile() {
		if (isSong()) {
			SongFileInfo songFileInfo = new SongFileInfo();
			songFileInfo.setTitle(readTitle(songTitleKey));
			songFileInfo.setArtist(readArtist());
			songFileInfo.setAlbum(readAlbum());
			songFileInfo.setTrackNumber(readTrack());
			songFileInfo.setDate(readDate());
			fileInfo = songFileInfo;
		}else if (isVideo()) {
		    VideoFileInfo videoFileInfo = new VideoFileInfo(); 
			videoFileInfo.setTitle(readTitle(videoTitleKey));
			videoFileInfo.setDate(readDate());
			videoFileInfo.setCategory(readCategory());
		}
	}
	
	public Date readDate() {
		String strDate = getValueOrNull(dateKey);
		Date releaseDate = null;
		if (strDate == null) {
			return releaseDate;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			releaseDate = dateFormat.parse(strDate);
		} catch (ParseException e) {
			getLog().warning("Could not parse date: " + strDate + "\n" + e.getMessage());
		}
		return releaseDate;
		
	}
	
	
	public String readCategory() {
		return getValueOrUnknown(categoryKey);
	}
	
	public boolean isSong() {
		return getFileData().getValuesHash().get(songTitleKey) != null;
	}
	
	public boolean isVideo() {
		return getFileData().getValuesHash().get(videoTitleKey) != null;
	}
	
	/**
	 * 
	 * @return the value of the title field. Null if no value, or if value is ""
	 */
	public String readTitle(String titleKey) {
		String title = getValueOrNull(titleKey);
		if (title == null || title.length() > maxTitleLength) {
			getLog().warning("Invalid title");
			return null;
		}
		return title;
	}
	
	/**
	 * 
	 * @return the value of the Artist field. returns "Unknown" if empty or blank 
	 */
	public String readArtist() {
		return getValueOrUnknown(artistKey);
	}
	
	/**
	 * 
	 * @return the value of the Album field. returns "Unknown" if empty or blank
	 */
	public String readAlbum() {
		return getValueOrUnknown(albumKey);
	}
	
	public int readTrack() {
		int track = 0;
		try {
			track = Integer.parseInt(getValueOrNull(trackNumberKey));
		}catch (NumberFormatException e) {
			getLog().warning("Error converting track number to Integer\n" + e.getMessage());
			track = 0;
		}
		if (track < 1 ) {
			getLog().warning("Track must be greater than 1");
			track = 0;
		}
		return track;
	}
	
	/**
	 * 
	 * @param key The field to be fetched from the Hash
	 * @return the value, or "Unknown" if the value is null
	 */
	private String getValueOrUnknown(String key) {
		String value = getValueOrNull(key);
		if (value == null) 
			value = "Unknown";
		return value;
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
	
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}
	
	private Logger getLog() {
		return log;
	}
	public static void main(String[] args) {
	}

}
