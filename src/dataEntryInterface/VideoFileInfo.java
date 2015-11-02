package dataEntryInterface;

import java.util.Date;

/**
 * 
 * @author Daniel
 * This class collects the data read from the metadata file.
 * An instance of this class is passed to CoreDataAccess when creating or updating a video.
 * The data in this class should have been input validated by DataInterpreter.
 */
public class VideoFileInfo extends FileInfo {
	private String category;
	public VideoFileInfo(String title, String category, Date date) {
		super(title, date);
		this.category = category;
	}

	public VideoFileInfo() {
		super();
		this.category = null;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
}
