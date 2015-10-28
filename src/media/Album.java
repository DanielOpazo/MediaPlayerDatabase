package media;

import java.util.ArrayList;
import java.util.Date;

public class Album {
	private int albumId;
	private String title, coverPath;
	private ArrayList<Song> songs;
	private Artist artist;
	private Date releaseDate;
	
	public Album(int albumID, String albumTitle, String coverPath, ArrayList<Song> songs, Artist artist, Date releaseDate) {
		this.albumId = albumID;
		this.title = albumTitle;
		this.coverPath = coverPath;
		this.songs = songs;
		this.artist = artist;
		this.releaseDate = releaseDate;
	}
	
	public int getAlbumId() {
		return albumId;
	}
	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String albumTitle) {
		this.title = albumTitle;
	}
	public String getcoverPath() {
		return coverPath;
	}
	public void setcoverPath(String coverPath) {
		this.coverPath = coverPath;
	}
	public ArrayList<Song> getSongs() {
		return songs;
	}
	public void setSongs(ArrayList<Song> songs) {
		this.songs = songs;
	}
	public Artist getArtist() {
		return artist;
	}
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

}
