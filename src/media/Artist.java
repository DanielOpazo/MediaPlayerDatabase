package media;

import java.util.ArrayList;

public class Artist {
	private int artistId;
	private String name;
	private ArrayList<Album> albums;
	
	public Artist(int artistId, String name, ArrayList<Album> albums) {
		this.artistId = artistId;
		this.name = name;
		this.albums = albums;
	}
	public int getArtistId() {
		return artistId;
	}
	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	public void setAlbums(ArrayList<Album> albums) {
		this.albums = albums;
	}

}
