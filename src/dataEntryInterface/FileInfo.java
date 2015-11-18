package dataEntryInterface;

import java.util.Date;

public abstract class FileInfo {

	private String title;
	private String filePath;
	private Date date;

	public FileInfo(String title, Date date) {
		this.title = title;
		this.date = date;
	}

	public FileInfo() {
		title = null;
		date = null;
	}
	
	public boolean isValid() {
		return (title != null && filePath != null);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}