package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import shared.AlbumDescriptor;
import shared.ArtistDescriptor;
import shared.CategoryDescriptor;
import shared.SongDescriptor;
import shared.VideoDescriptor;
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
	private final String getAllSongsQuery = "select s.title, s.track_number, s.song_id, al.date, al.title, ar.name from song as s inner join album as al on s.album_id = al.album_id inner join artist as ar on al.artist_id = ar.artist_id;";
	private final String getAllSongsForAlbumQuery = "select s.title, s.track_number, s.song_id, al.date, al.title, ar.name from song as s inner join album as al on s.album_id = al.album_id inner join artist as ar on al.artist_id = ar.artist_id where al.album_id = ?;";
	private final String getAllAlbumsQuery = "select al.title, al.date, al.album_id, ar.name from album as al inner join artist as ar on al.artist_id = ar.artist_id;";
	private final String getAllAlbumsForArtistQuery = "select al.title, al.date, al.album_id, ar.name from album as al inner join artist as ar on al.artist_id = ar.artist_id where ar.artist_id = ?";
	private final String getAllArtistsQuery = "select artist_id, name from artist;";
	private final String getAllVideosQuery = "select title, category, release_date, video_id from video;";
	private final String getAllVideosForCategoryQuery = "select title, category, release_date, video_id from video where category = ?;";
	private final String getCategoriesQuery = "select distinct category from video;";
	
	private static final Logger log = Logger.getLogger(CoreDataAccess.class.getName());
	private final String nullDateString = "Unknown";
	
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
		Connection conn = null;
		boolean result = false;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(doesSongExistQuery);
			ps.setString(1, songInfo.getTitle());
			ps.setString(2, songInfo.getAlbum());
			ps.setString(3, songInfo.getArtist());
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				result = (rs.getInt(1) == 1) ? true: false;
			}
		} catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in doesSongExist", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
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
		Connection conn = null;
		int result = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(getAlbumIdQuery);
			ps.setString(1, songInfo.getAlbum());
			if (songInfo.getDate() != null){
				ps.setDate(2, new Date(songInfo.getDate().getTime()));
			}else {
				ps.setDate(2, null);
			}
			ps.setString(3, songInfo.getArtist());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1);
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in getAlbumPrimaryKey", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
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
		Connection conn = null;
		int key = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(getArtistIdQuery);
			ps.setString(1, songInfo.getArtist());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				key = rs.getInt(1);
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in getArtistPrimaryKey", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
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
		Connection conn = null;
		int key = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(getVideoIdQuery);
			ps.setString(1, videoTitle);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				key = rs.getInt(1);
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in getVideoPrimaryKey", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
		}
		return key;
	}
	
	/**
	 * 
	 * @param albumId The album whose songs have been requested. if albumId is null, return all songs, not just for an album 
	 * @return LinkedList of songs in the form of SongDescriptors
	 */
	public LinkedList<SongDescriptor> getSongsForAlbum(Integer albumId) {
		PreparedStatement ps = null;
		Connection conn = null;
		LinkedList<SongDescriptor> songs = new LinkedList<SongDescriptor>();
		try {
			conn = getConnection();
			String query = (albumId == null) ? getAllSongsQuery: getAllSongsForAlbumQuery;
			ps = conn.prepareStatement(query);
			if (albumId != null) ps.setInt(1, albumId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Date sqlDate = rs.getDate(4);
				String strDate = getYearOrUnknown(sqlDate);
				songs.add(new SongDescriptor(rs.getString(1), rs.getString(6), rs.getString(5), strDate, String.valueOf(rs.getInt(2)), rs.getInt(3)));
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in getSongsForAlbum", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
		}
		return songs;
	}
	
	/**
	 * 
	 * @param artistId artist id whose albums should be returned. If null, returns all albums
	 * @return LinkedList of albums in the form of AlbumDescriptors
	 */
	public LinkedList<AlbumDescriptor> getAlbumsForArtist(Integer artistId) {
		PreparedStatement ps = null;
		Connection conn = null;
		LinkedList<AlbumDescriptor> albums = new LinkedList<AlbumDescriptor>();
		try {
			conn = getConnection();
			String query = (artistId == null) ? getAllAlbumsQuery : getAllAlbumsForArtistQuery;
			ps = conn.prepareStatement(query);
			if (artistId != null) ps.setInt(1, artistId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Date sqlDate = rs.getDate(2);
				String strDate = getYearOrUnknown(sqlDate);
				albums.add(new AlbumDescriptor(rs.getString(1), rs.getString(4), strDate, rs.getInt(3)));
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in getAlbumsForArtist", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
		}
		return albums;
	}

	private String getYearOrUnknown(Date sqlDate) {
		String strDate = nullDateString;
		if (sqlDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(sqlDate);
			strDate = String.valueOf(cal.get(Calendar.YEAR));
		}
		return strDate;
	}
	
	/**
	 * 
	 * @return A LinkedList off all the artists in the form of ArtistDescriptors
	 */
	public LinkedList<ArtistDescriptor> getArtists() {
		PreparedStatement ps = null;
		Connection conn = null;
		LinkedList<ArtistDescriptor> artists = new LinkedList<ArtistDescriptor>();
		try {
			conn = getConnection();
			ps = conn.prepareStatement(getAllArtistsQuery);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				artists.add(new ArtistDescriptor(rs.getString(2), rs.getInt(1)));
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in getArtists", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
		}
		return artists;
	}
	
	/**
	 * 
	 * @param category Get videos matching this category. If category is null, return all videos
	 * @return A LinkedList of VideoDescriptors
	 */
	public LinkedList<VideoDescriptor> getVideosForCategory(String category) {
		PreparedStatement ps = null;
		Connection conn = null;
		LinkedList<VideoDescriptor> videos = new LinkedList<VideoDescriptor>();
		try {
			conn = getConnection();
			String query = (category == null) ? getAllVideosQuery : getAllVideosForCategoryQuery;
			ps = conn.prepareStatement(query);
			if (category != null) ps.setString(1, category);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Date sqlDate = rs.getDate(3);
				String strDate = getYearOrUnknown(sqlDate);
				videos.add(new VideoDescriptor(rs.getString(1), rs.getString(2), strDate, rs.getInt(4)));
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in getVideosForCategory", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
		}
		return videos;
	}
	
	public LinkedList<CategoryDescriptor> getCategories() {
		PreparedStatement ps = null;
		Connection conn = null;
		LinkedList<CategoryDescriptor> categories = new LinkedList<CategoryDescriptor>();
		try {
			conn = getConnection();
			ps = conn.prepareStatement(getCategoriesQuery);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				categories.add(new CategoryDescriptor(rs.getString(1)));
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in getCategories", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
		}
		return categories;
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
		Connection conn = null;
		try {
			conn = getConnection();
			if (doesSongExist(songInfo)) {
				getLog().log(Level.WARNING, "Trying to insert song that already exists. Ignoring new song");
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
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in insertSong", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
		}
	}
	
	/**
	 * Insert a new video into the databse.
	 * If a video with the same title already exists, do nothing
	 * @param videoInfo
	 */
	public void insertVideo(VideoFileInfo videoInfo) {
		PreparedStatement ps = null;
		Connection conn = null;
		try {
			conn = getConnection();
			if (getVideoPrimaryKey(videoInfo.getTitle()) == 0) {
				ps = conn.prepareStatement(insertVideoQuery);
				ps.setString(1, videoInfo.getFilePath());
				ps.setString(2, videoInfo.getTitle());
				if (videoInfo.getDate() != null) {
					ps.setDate(3, new Date(videoInfo.getDate().getTime()));
				}else {
					ps.setDate(3, null);
				}
				ps.setString(4, videoInfo.getCategory());
				ps.setString(5, null);
				ps.executeUpdate();
			}
		}catch (SQLException e) {
			getLog().log(Level.SEVERE, "SQL Error in insertVideo", e);
		}finally {
			try {
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			}catch (SQLException e) {
				getLog().log(Level.SEVERE, "Error closing JDBC resources\n", e);
			}
		}
		
	}
	
	private void createArtist(SongFileInfo songInfo, Connection conn, PreparedStatement ps) throws SQLException {
		ps = conn.prepareStatement(insertArtistQuery);
		ps.setString(1, songInfo.getArtist());
		ps.executeUpdate();
	}
	
	private void createAlbumForArtist(int artistId, SongFileInfo songInfo, Connection conn, PreparedStatement ps) throws SQLException {
		ps = conn.prepareStatement(insertAlbumIntoArtistQuery);
		if (songInfo.getDate() != null) {
			ps.setDate(1, new Date(songInfo.getDate().getTime()));
		}else {
			ps.setDate(1, null);
		}
		ps.setString(2, null);//TODO handle album pictures
		ps.setInt(3, artistId);
		ps.setString(4, songInfo.getAlbum());
		ps.executeUpdate();
	}
	
	private void createSongForAlbum(int albumId, SongFileInfo songInfo, Connection conn, PreparedStatement ps) throws SQLException {
		ps = conn.prepareStatement(insertSongIntoAlbumQuery);
		ps.setString(1, songInfo.getTitle());
		ps.setInt(2, albumId);
		ps.setInt(3, songInfo.getTrackNumber());
		ps.setString(4, songInfo.getFilePath());
		ps.executeUpdate();
	}

	public static Logger getLog() {
		return log;
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
