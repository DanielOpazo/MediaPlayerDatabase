package Descriptors;

import java.io.Serializable;

/**
 * The following class is used to store the contents of a video. This includes the title, category,
 * and year released.
 *
 * @author  TeamSDK
 * @since   November 3 2015
 * @version November 15 2015
 */
public class VideoDescriptor implements Serializable {

    private static final long serialVersionUID = 465489; // serialization ID

    private String title; // String field to store video name
    private String category; // String field to store video category
    private String releaseYear; // String field to store video release year
    private int id; // the id of video corresponding to the database id

    /**
     * Constructor to assign all fields in VideoDescriptor.
     *
     * @param title - the title of the video to assign to the VideoDescriptor object
     * @param category - the category of the video to assign to the VideoDescriptor object
     * @param releaseYear - the release year of the video to assign to the VideoDescriptor object
     * @param id - the id to assign the VideoDescriptor corresponding to its database id.
     *
     * @since   November 3 2015
     * @version November 15 2015
     */
    public VideoDescriptor(String title, String category, String releaseYear, int id) {

        this.title = title;
        this.category = category;
        this.releaseYear = releaseYear;
        this.id = id;

    } // end constructor

    /**
     * Getter method used to retrieve the title of a video.
     *
     * @return The title of the video.
     *
     * @since   November 3 2015
     * @version November 3 2015
     */
    public String getTitle() {

        return title;

    } // end method

    /**
     * Getter method used to retrieve the category of a video.
     *
     * @return The category of the video.
     *
     * @since   November 3 2015
     * @version November 3 2015
     */
    public String getCategory() {

        return category;

    } // end method

    /**
     * Getter method used to retrieve the release year of a video.
     *
     * @return The release year of the video
     *
     * @since   November 3 2015
     * @version November 3 2015
     */
    public String getReleaseYear() {

        return releaseYear;

    } // end method

    /**
     * Getter method used to retrieve the id of the video.
     *
     * @return The id of the video corresponding to the database id.
     *
     * @since   November 14 2015
     * @version November 14 2015
     */
    public int getId() {

        return id;

    } // end method

    /**
     * Setter method used to set the title of the video.
     *
     * @param title - the title to assign to the video
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setTitle(String title) {

        this.title = title;

    } // end method

    /**
     * Setter method used to set the category of the video.
     *
     * @param category - the category to assign to the video
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setCategory(String category) {

        this.category = category;

    } // end method

    /**
     * Setter method used to set the release year of the video.
     *
     * @param releaseYear - the release year to assign to the video
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setReleaseYear(String releaseYear) {

        this.releaseYear = releaseYear;

    } // end method

    /**
     * Setter method used to set the database id of the video.
     *
     * @param id - the database id to assign to the video
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setId(int id) {

        this.id = id;

    } // end method

} // end class
