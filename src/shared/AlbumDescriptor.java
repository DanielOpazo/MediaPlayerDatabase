package shared;

import java.io.Serializable;

/**
 * The following class is used to store the contents of a album. This includes the title, year
 * released, and database id.
 *
 * @author  TeamSDK
 * @since   November 15 2015
 * @version November 15 2015
 */
public class AlbumDescriptor implements Serializable {

    private static final long serialVersionUID = 465489; // serialization ID

    private String albumTitle; // String to store the album title
    private String artistName; // String to store the artist name
    private String releaseYear; // String to store the release year of the album
    private int id; // the id of album corresponding to the database id

    /**
     * Constructor to assign all fields in AlbumDescriptor.
     *
     * @param albumTitle - the name of the album to assign to the AlbumDescriptor object
     * @param releaseYear - the release year of the album to assign to the AlbumDescriptor object
     * @param id - the id to assign the AlbumDescriptor corresponding to its database id.
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public AlbumDescriptor(String albumTitle, String artistName, String releaseYear, int id) {

        this.albumTitle = albumTitle;
        this.artistName = artistName;
        this.releaseYear = releaseYear;
        this.id = id;

    } // end constructor

    /**
     * Getter method used to retrieve the name of the album.
     *
     * @return The name of the album.
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public String getAlbumTitle() {

        return albumTitle;

    } // end method

    /**
     * Getter method used to retrieve the artist of the album.
     *
     * @return The artist of the album.
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public String getArtist() {

        return artistName;

    } // end method

    /**
     * Getter method used to retrieve the release year of the album.
     *
     * @return The release year of the album.
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public String getReleaseYear() {

        return releaseYear;

    } // end method

    /**
     * Getter method used to retrieve the id of the album.
     *
     * @return The id of the album corresponding to the database id.
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public int getId() {

        return id;

    } // end method

    /**
     * Setter method used to set the title of the album.
     *
     * @param albumTitle - the title to assign to the album
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setAlbumTitle(String albumTitle) {

        this.albumTitle = albumTitle;

    } // end method

    /**
     * Setter method used to set the artist of the album.
     *
     * @param artistName - the artist to assign to the album
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setArtist(String artistName) {

        this.artistName = artistName;

    } // end method

    /**
     * Setter method used to set the release year of the album.
     *
     * @param releaseYear - the release year to assign to the album
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setReleaseYear(String releaseYear) {

        this.releaseYear = releaseYear;

    } // end method

    /**
     * Setter method used to set the database id of the album.
     *
     * @param id - the database id to assign to the album
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setId(int id) {

        this.id = id;

    } // end method

} // end class