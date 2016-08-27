package net.snortum.homefinance.model;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * An entry item in the Home Finance system.  Entries can be one of three {@link EntryType}s,
 * each with the same data but different behaviors.  
 * 
 * @author Knute Snortum
 * @version 2016-08-19
 */
public interface Entry {
	
	/** SQL string to create table */
	static final String SQL_TO_CREATE =
			"CREATE TABLE entry ("
			+ "id          INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "description TEXT,"
			+ "type        TEXT    CHECK(type = 'in' OR type = 'out' OR type = 'balance'),"
			+ "recurring   BOOLEAN,"
			+ "amount      REAL    CHECK(amount >= 0),"
			+ "comment     TEXT,"
			+ "url         TEXT,"
			+ "paid        BOOLEAN,"
			+ "date        DATE,"
			+ "reconciled  BOOLEAN,"
			+ "category_id INTEGER NOT NULL,"
			+ "FOREIGN KEY (category_id) REFERENCES category(id)"
			+ ")";
	
	int getId();
	void setId(int id);
	EntryType getType();
	String getDescription();
	SimpleStringProperty descriptionProperty();
	boolean isRecurring();
	SimpleBooleanProperty recurringProperty();
	BigDecimal getAmount();
	SimpleObjectProperty<BigDecimal> amountProperty();
	String getComment();
	SimpleStringProperty commentProperty();
	Optional<URL> getUrl();
	SimpleObjectProperty<Optional<URL>> urlProperty();
	boolean isPaid();
	SimpleBooleanProperty paidProperty();
	LocalDate getDate();
	SimpleObjectProperty<LocalDate> dateProperty();
	boolean isReconciled();
	SimpleBooleanProperty recociledProperty();
	Optional<Category> getCategory();
	String getCategoryDesc();
	SimpleStringProperty categoryDescProperty();
}
