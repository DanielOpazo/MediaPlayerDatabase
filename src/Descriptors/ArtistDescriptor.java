package Descriptors;

import java.io.Serializable;

/**
 * The following class is used to store the contents of a artist. This includes the artist name, and
 * database id.
 *
 * @author  TeamSDK
 * @since   November 15 2015
 * @version November 15 2015
 */
public class ArtistDescriptor implements Serializable {

    private static final long serialVersionUID = 465489; // serialization ID

    private String artistName; // String field to store the artist name
    private int id;// the id of artist corresponding to the database id

    /**
     * Constructor to assign all fields in ArtistDescriptor.
     *
     * @param artistName - the name of the artist to assign to the ArtistDescriptor object
     * @param id - the id to assign the ArtistDescriptor corresponding to its database id.
     *
     * @since November 15 2015
     * @version November 15 2015
     */
    public ArtistDescriptor(String artistName, int id) {

        this.artistName = artistName;
        this.id = id;

    } // end constructor

    /**
     * Getter method used to retrieve the name of the artist.
     *
     * @return The name of the artist.
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public String getArtistName() {

        return artistName;

    } // end method

    /**
     * Getter method used to retrieve the id of the artist.
     *
     * @return The id of the artist corresponding to the database id.
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public int getId() {

        return id;

    } // end method

    /**
     * Setter method used to set the name of the artist.
     *
     * @param artistName - the name to assign to the artist
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setArtistName(String artistName) {

        this.artistName = artistName;

    } // end method

    /**
     * Setter method used to set the database id of the artist.
     *
     * @param id - the database id to assign to the artist
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setId(int id) {

        this.id = id;

    } // end method

} // end class
