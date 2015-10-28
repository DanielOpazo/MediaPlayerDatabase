package media;

public class Song {
	private int songId, trackNumber;
	
	private String filePath, title;
	private Album album;
	
	/**
	 * 
	 * @param songID
	 * @param trackNumber
	 * @param filePath
	 * @param songTitle
	 * @param album
	 */
	public Song(int songID, int trackNumber, String filePath, String songTitle, Album album) {
		this.songId = songID;
		this.trackNumber = trackNumber;
		this.filePath = filePath;
		this.title = songTitle;
		this.album = album;
	}
	
	public int getSongId() {
		return songId;
	}

	public void setSongId(int songId) {
		this.songId = songId;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String songTitle) {
		this.title = songTitle;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}
}
