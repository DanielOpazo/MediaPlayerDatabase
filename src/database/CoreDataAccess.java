package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import dataEntryInterface.SongFileInfo;
import dataEntryInterface.VideoFileInfo;

public class CoreDataAccess {
	private final String USERNAME = "java";
	private final String PASSWORD = "password";
	private final String DATABASE_NAME = "media_library";
	
	//SQL Queries
	private final String doesSongExistQuery = "select count(*) from song as s inner join album as al on s.album_id = al.album_id inner join artist as ar on al.artist_id = ar.artist_id where s.title = ? and al.title = ? and ar.name = ?";
	private final String getAlbumIdQuery = "select al.album_id from album as al inner join artist as ar on al.artist_id = ar.artist_id where al.title = ? and al.date = ? and ar.name = ?";
	private final String getArtistIdQuery = "select artist_id from artist where name = ?";
	private final String getVideoIdQuery = "select video_id from video where title = ?";
	private final String insertSongIntoAlbumQuery = "insert into song (title, album_id, track_number, file_path) values (?, ?, ?, ?)";
	private final String insertAlbumIntoArtistQuery = "insert into album (date, album_cover_path, artist_id, title) values (?, ?, ?, ?)";
	private final String insertArtistQuery = "insert into artist (name) values (?)";
	private final String insertVideoQuery = "insert into video (video_path, title, release_date, category, cover_picture_path) values (?, ?, ?, ?, ?)";
	
	/**
	 * 
	 * @return Connection to the database
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", USERNAME);
		connectionProps.put("password", PASSWORD);
		
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE_NAME, connectionProps);
		System.out.println("Connected to database");
		conn.setAutoCommit(true);
		return conn;
	}

	/**
	 * A song exists if in the database there is a song with the same title, 
	 * album, artist, and track number
	 * @param song
	 */
	public boolean doesSongExist(SongFileInfo songInfo) {
		PreparedStatement ps = null;
		boolean result = false;
		try {
			Connection conn = getConnection();
			ps = conn.prepareStatement(doesSongExistQuery);
			ps.setString(1, songInfo.getTitle());
			ps.setString(2, songInfo.getAlbum());
			ps.setString(3, songInfo.getArtist());
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				result = (rs.getInt(1) == 1) ? true: false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * An album exists if there is an album with the same name and release date, and by an artist with the same name
	 * @param songInfo
	 * @return The primary key if the album exists. 0 if the album does not exist
	 */
	public int getAlbumPrimaryKey(SongFileInfo songInfo) {
		PreparedStatement ps = null;
		int result = 0;
		try {
			Connection conn = getConnection();
			ps = conn.prepareStatement(getAlbumIdQuery);
			ps.setString(1, songInfo.getAlbum());
			ps.setDate(2, new Date(songInfo.getDate().getTime()));
			ps.setString(3, songInfo.getArtist());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * An artist exists if there is an artist with the same name. Artist names are unique
	 * @param songInfo
	 * @return The primary key if the artist exists. 0 otherwise
	 */
	public int getArtistPrimaryKey(SongFileInfo songInfo) {
		PreparedStatement ps = null;
		int key = 0;
		try {
			Connection conn = getConnection();
			ps = conn.prepareStatement(getArtistIdQuery);
			ps.setString(1, songInfo.getArtist());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				key = rs.getInt(1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return key;
	}
	
	/**
	 * 
	 * @param videoTitle
	 * @return the primary key if the video exists. 0 otherwise
	 */
	public int getVideoPrimaryKey(String videoTitle) {
		PreparedStatement ps = null;
		int key = 0;
		try {
			Connection conn = getConnection();
			ps = conn.prepareStatement(getVideoIdQuery);
			ps.setString(1, videoTitle);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				key = rs.getInt(1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return key;
	}
	
	/**
	 * possibilities when entering a song:
	 * Does Song already exist?
	 * --yes -> do nothing?? delete old and use new? update select fields?
	 * --no  -> does album exist?
	 * ------------yes -> create song in that album
	 * ------------no  -> does artist exist?
	 * -------------------------yes -> create album for artist, and song in album
	 * -------------------------no  -> create new artist, album, and song
	 * @param song
	 */
	public void insertSong(SongFileInfo songInfo) {
		PreparedStatement ps = null;
		try {
			Connection conn = getConnection();
			if (doesSongExist(songInfo)) {
				//TODO hmm
				System.out.println("Song already exists");
			}else {
				int albumId = getAlbumPrimaryKey(songInfo);
				if (albumId != 0) { //Album exists
					createSongForAlbum(albumId, songInfo, conn, ps);
				}else {
					int artistId = getArtistPrimaryKey(songInfo);
					if (artistId != 0) { //Artist exists
						createAlbumForArtist(artistId, songInfo, conn, ps);
						int albumIdNew = getAlbumPrimaryKey(songInfo);
						createSongForAlbum(albumIdNew, songInfo, conn, ps);
					}else {
						createArtist(songInfo, conn, ps); //nothing exists
						int artistIdNew = getArtistPrimaryKey(songInfo);
						createAlbumForArtist(artistIdNew, songInfo, conn, ps);
						int albumIdNew = getAlbumPrimaryKey(songInfo);
						createSongForAlbum(albumIdNew, songInfo, conn, ps);
					}
				}
				conn.close();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Insert a new video into the databse.
	 * If a video with the same title already exists, do nothing
	 * @param videoInfo
	 */
	public void insertVideo(VideoFileInfo videoInfo) {
		PreparedStatement ps = null;
		try {
			Connection conn = getConnection();
			if (getVideoPrimaryKey(videoInfo.getTitle()) == 0) {
				ps = conn.prepareStatement(insertVideoQuery);
				ps.setString(1, null);
				ps.setString(2, videoInfo.getTitle());
				ps.setDate(3, new Date(videoInfo.getDate().getTime()));
				ps.setString(4, videoInfo.getCategory());
				ps.setString(5, null);
				ps.executeUpdate();
			}
			conn.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void createArtist(SongFileInfo songInfo, Connection conn, PreparedStatement ps) throws SQLException {
		ps = conn.prepareStatement(insertArtistQuery);
		ps.setString(1, songInfo.getArtist());
		ps.executeUpdate();
	}
	
	private void createAlbumForArtist(int artistId, SongFileInfo songInfo, Connection conn, PreparedStatement ps) throws SQLException {
		ps = conn.prepareStatement(insertAlbumIntoArtistQuery);
		ps.setDate(1, new Date(songInfo.getDate().getTime()));
		ps.setString(2, null); //TODO handle file paths
		ps.setInt(3, artistId);
		ps.setString(4, songInfo.getAlbum());
		ps.executeUpdate();
	}
	
	private void createSongForAlbum(int albumId, SongFileInfo songInfo, Connection conn, PreparedStatement ps) throws SQLException {
		ps = conn.prepareStatement(insertSongIntoAlbumQuery);
		ps.setString(1, songInfo.getTitle());
		ps.setInt(2, albumId);
		ps.setInt(3, songInfo.getTrackNumber());
		ps.setString(4, null); //TODO handle file paths
		ps.executeUpdate();
		
	}
	
	public static void main(String[] args) {
		CoreDataAccess cda = new CoreDataAccess();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date releaseDate = null;
		try {
			releaseDate = format.parse("2005-01-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//SongFileInfo song = new SongFileInfo("A Day In The Life", "The Beatles", "The White Album", 1, releaseDate);
		//SongFileInfo song = new SongFileInfo("Let It Be", "The Beatles", "Let It Be", 6, releaseDate);
		SongFileInfo song = new SongFileInfo("Stairway to Heaven", "Led Zeppelin", "Led Zeppelin IV", 4, releaseDate);
		int primaryKey = cda.getArtistPrimaryKey(song);
		//cda.insertSong(song);
		//VideoFileInfo vfi = new VideoFileInfo("Batman Begins", "drama", releaseDate);
		//cda.insertVideo(vfi);
		System.out.println(primaryKey);
	}

}
