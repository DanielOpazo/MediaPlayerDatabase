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
public class SongFileInfo extends FileInfo{
	private String artist, album;
	private int track;

	public SongFileInfo(String title, String artist, String album, int trackNumber, Date releaseDate) {
		super(title, releaseDate);
		this.artist = artist;
		this.album = album;
		this.track = trackNumber;
	}
	
	public SongFileInfo() {
		super();
		this.artist = null;
		this.album = null;
		this.track = 0;
	}
	
	/**
	 * The entry is not valid if the title is null
	 * @return
	 */
	public boolean isValidEntry() {
		return (getTitle() != null);
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
	
}
