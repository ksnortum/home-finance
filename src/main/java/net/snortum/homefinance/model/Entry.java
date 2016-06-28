package net.snortum.homefinance.model;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

/**
 * An entry item in the Home Finance system.  Entries can be one of three {@link EntryType}s,
 * each with the same data but different behaviors.  
 * 
 * @author Knute Snortum
 * @version 2016-06-11
 */
public interface Entry {
	
	/** SQL string to create table */
	static final String SQL_TO_CREATE =
			"CREATE TABLE entry ("
			+ "id          INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "description TEXT,"
			+ "type        TEXT    CHECK(type = 'in' OR type = 'out' OR type = 'balance'),"
			+ "recurring   INTEGER CHECK(recurring = 0 OR recurring = 1),"
			+ "amount      REAL    CHECK(amount >= 0),"
			+ "comment     TEXT,"
			+ "url         TEXT,"
			+ "paid        INTEGER CHECK(paid = 0 OR paid = 1),"
			+ "date        INTEGER,"
			+ "reconciled  INTEGER CHECK(reconciled = 0 OR reconciled = 1),"
			+ "category_id INTEGER NOT NULL,"
			+ "FOREIGN KEY (category_id) REFERENCES category(id)"
			+ ")";
	
	int getId();
	String getDescription();
	boolean isRecurring();
	double getAmount();
	String getComment();
	Optional<URL> getUrl();
	boolean isPaid();
	LocalDate getDate();
	Optional<Category> getCategory();
	String getCategoryDesc();
	int getCategoryId();
	EntryType getType();
}
