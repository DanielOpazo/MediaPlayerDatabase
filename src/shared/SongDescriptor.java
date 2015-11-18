package shared;

import java.io.Serializable;

/**
 * The following class is used to store the contents of a song. This includes the title, artist,
 * album, year released, track number, and database id.
 *
 * @author  TeamSDK
 * @since   October 24 2015
 * @version November 15 2015
 */
public class SongDescriptor implements Serializable {

    private static final long serialVersionUID = 465489; // serialization ID

    private String title; // String field to store song title
    private String artist; // String field to store song artist
    private String album; // String field to store song album
    private String releaseYear; // String field to store song release year
    private String trackNumber; // String field to store song track number
    private int id; // the id of song corresponding to the database id

    /**
     * Constructor to assign all fields in SongDescriptor.
     *
     * @param title - the title of the song to assign to the SongDescriptor object
     * @param artist - the name of the artist of the song to assign to the SongDescriptor object
     * @param album - the name of the album of the song to assign to the SongDescriptor object
     * @param releaseYear - the release year of the song to assign to the SongDescriptor object
     * @param trackNumber - the track number of the song to assign to the SongDescriptor object
     * @param id - the id to assign the SongDescriptor corresponding to its database id.
     *
     * @since   October 24 2015
     * @version November 15 2015
     */
    public SongDescriptor(String title, String artist, String album, String releaseYear, String trackNumber, int id) {

        this.title = title;
        this.artist = artist;
        this.album = album;
        this.releaseYear = releaseYear;
        this.trackNumber = trackNumber;
        this.id = id;

    } // end constructor

    /**
     * Getter method used to retrieve the title of a song.
     *
     * @return The title of the song.
     *
     * @since   October 24 2015
     * @version October 30 2015
     */
    public String getTitle() {

        return title;

    } // end method

    /**
     * Getter method used to retrieve the artist of a song.
     *
     * @return The artist of the song.
     *
     * @since   October 24 2015
     * @version October 30 2015
     */
    public String getArtist() {

        return artist;

    } // end method

    /**
     * Getter method used to retrieve the album of the song
     *
     * @return The album of the song
     *
     * @since   October 24 2015
     * @version October 30 2015
     */
    public String getAlbum() {

        return album;

    } // end method

    /**
     * Getter method used to retrieve the release year of the song
     *
     * @return The release year of the song
     *
     * @since   November 3 2015
     * @version November 3 2015
     */
    public String getReleaseYear() {

        return releaseYear;

    } // end method

    /**
     * Getter method used to retrieve the track number of the song
     *
     * @return The track number of the song
     *
     * @since   November 3 2015
     * @version November 3 2015
     */
    public String getTrackNumber() {

        return trackNumber;

    } // end method

    /**
     * Getter method used to retrieve the id of the song.
     *
     * @return The id of the song corresponding to the database id.
     *
     * @since   November 14 2015
     * @version November 14 2015
     */
    public int getId() {

        return id;

    } // end method

    /**
     * Setter method used to set the title of the song.
     *
     * @param title - the title to assign to the song
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setTitle(String title) {

        this.title = title;

    } // end method

    /**
     * Setter method used to set the artist of the song.
     *
     * @param artist - the artist to assign to the song
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setArtist(String artist) {

        this.artist = artist;

    } // end method

    /**
     * Setter method used to set the album of the song.
     *
     * @param album - the album to assign to the song
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setAlbum(String album) {

        this.album = album;

    } // end method

    /**
     * Setter method used to set the release year of the song.
     *
     * @param releaseYear - the release year to assign to the song
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setReleaseYear(String releaseYear) {

        this.releaseYear = releaseYear;

    } // end method

    /**
     * Setter method used to set the track number of the song.
     *
     * @param trackNumber - the track number to assign to the song
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setTrackNumber(String trackNumber) {

        this.trackNumber = trackNumber;

    } // end method

    /**
     * Setter method used to set the database id of the song.
     *
     * @param id - the database id to assign to the song
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setId(int id) {

        this.id = id;

    } // end method

} // end class