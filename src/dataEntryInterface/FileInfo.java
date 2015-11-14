package dataEntryInterface;

import java.util.Date;

public abstract class FileInfo {

	private String title;
	private Date date;

	public FileInfo(String title, Date date) {
		this.title = title;
		this.date = date;
	}

	public FileInfo() {
		title = null;
		date = null;
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

}