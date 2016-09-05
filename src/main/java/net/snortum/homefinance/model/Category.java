package net.snortum.homefinance.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Categories are used to group entries and create budgets.
 *  
 * @author Knute Snortum
 * @version 2016-06-09
 */
public class Category {
	
	/** The SQL statement used to create this table */
	public static final String SQL_TO_CREATE =
			"CREATE TABLE category ("
    		+ "id          INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ "description TEXT UNIQUE NOT NULL"
    		+ ")";
	
	// Fields
	private IntegerProperty id;
	private StringProperty description;
	
	/**
	 * Create a category object.  Normally, we don't know the ID before we
	 * insert the record, so use {@link #Category(String)} to initially 
	 * insert, capture the ID, then use {@link #Category(int, Category)}
	 * to update the ID.
	 * 
	 * @param id The ID for this object.  May be zero.
	 * @param description the description of this category.
	 */
	public Category(int id, String description) {
		if (description == null || description.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Description must not be null and cannot be empty");
		}
		this.id = new SimpleIntegerProperty(id);
		this.description = new SimpleStringProperty(description);
	}
	
	/**
	 * Create a category object with a zero ID.
	 * 
	 * @param description
	 */
	public Category(String description) {
		this(0, description);
	}
	
	// Getters and Setters
	
	public int getId() {
		return id.get();
	}
	
	public void setId(int id) {
		this.id.set(id);
	}
	
	public IntegerProperty idProperty() {
		return id;
	}
	
	public String getDescription() {
		return description.get();
	}
	
	public void setDescription(String description) {
		this.description.set(description);
	}
	
	public StringProperty descriptionProperty() {
		return description; 
	}
	
	@Override
	public String toString() {
		return getId() + ", " + getDescription();
	}
}
