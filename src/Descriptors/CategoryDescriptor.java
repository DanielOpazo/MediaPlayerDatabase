package Descriptors;

import java.io.Serializable;

/**
 * The following class is used to store the contents of a category. This includes the category
 * name.
 *
 * @author  TeamSDK
 * @since   November 15 2015
 * @version November 15 2015
 */
public class CategoryDescriptor implements Serializable {

    private static final long serialVersionUID = 465489; // serialization ID

    private String categoryName; // String field to store the category name

    /**
     * Constructor to assign all fields in CategoryDescriptor.
     *
     * @param categoryName - the name of the category to assign to the CategoryDescriptor object
     *
     * @since November 15 2015
     * @version November 15 2015
     */
    public CategoryDescriptor(String categoryName) {

        this.categoryName = categoryName;

    } // end constructor

    /**
     * Getter method used to retrieve the name of the category.
     *
     * @return The name of the category.
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public String getCategoryName() {

        return categoryName;

    } // end method

    /**
     * Setter method used to set the name of the category.
     *
     * @param categoryName - the name to assign to the category
     * @return NONE
     *
     * @since   November 15 2015
     * @version November 15 2015
     */
    public void setCategoryName(String categoryName) {

        this.categoryName = categoryName;

    } // end method

} // end class
