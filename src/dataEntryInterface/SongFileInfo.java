package dataEntryInterface;

import java.util.Date;

/**
 * 
 * @author Daniel
 * This class collects the data read from the metadata file.
 * An instance of this class is passed to CoreDataAccess when creating or updating a song.
 * The data in this class should have been input validated by DataInterpreter.
 * 
 */
public class SongFileInfo {
	private String title, artist, album;
	private int track;
	private Date releaseDate;

	public SongFileInfo(String title, String artist, String album, int trackNumber, Date releaseDate) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.track = trackNumber;
		this.releaseDate = releaseDate;
	}
	
	public SongFileInfo() {
		this.title = null;
		this.artist = null;
		this.album = null;
		this.track = 0;
		this.releaseDate = null;
	}
	
	/**
	 * The entry is not valid if the title is null
	 * @return
	 */
	public boolean isValidEntry() {
		return (title != null);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getTrackNumber() {
		return track;
	}

	public void setTrackNumber(int trackNumber) {
		this.track = trackNumber;
	}
	
	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	
}
