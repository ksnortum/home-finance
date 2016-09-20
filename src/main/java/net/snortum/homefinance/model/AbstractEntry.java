package net.snortum.homefinance.model;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Implements most of the {@link Entry} interface. 
 *  
 * @author Knute Snortum
 * @version 2016-09-01
 */
public abstract class AbstractEntry implements Entry {
	
	protected int id;
	protected SimpleStringProperty description = new SimpleStringProperty();
	protected SimpleBooleanProperty recurring = new SimpleBooleanProperty();
	protected SimpleObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();
	protected SimpleStringProperty comment = new SimpleStringProperty();
	protected SimpleObjectProperty<Optional<URL>> url = new SimpleObjectProperty<>();
	protected SimpleBooleanProperty paid = new SimpleBooleanProperty();
	protected SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	protected SimpleBooleanProperty reconciled = new SimpleBooleanProperty();
	protected Optional<Category> category;
	protected SimpleStringProperty categoryDesc = new SimpleStringProperty(); 
	protected EntryType type;

	/**
	 * Used to create {@link Entry} objects
	 * 
	 * @author Knute Snortum
	 * @version 2016-09-17
	 */
	public static abstract class Builder {
		
		// Mandatory
		EntryType type;
		
		// Optional, set default values
		int id = -1;
		String description = "";
		boolean recurring = false;
		BigDecimal amount = BigDecimal.ZERO;
		String comment = "";
		Optional<URL> url = Optional.empty();
		boolean paid = false;
		LocalDate date = LocalDate.now();
		boolean reconciled = false;
		Optional<Category> category = Optional.empty();
		
		/**
		 * Create a builder object with the default values.  Type is mandatory.
		 * 
		 * @param type the type of this entry
		 */
		public Builder(EntryType type) {
			this.type = type;
		}
		
		/**
		 * Create a builder from another Entry object
		 * 
		 * @param entry the Entry object to build from
		 */
		public Builder(Entry entry) {
			this.type = entry.getType();
			this.id = entry.getId();
			this.description = entry.getDescription();
			this.recurring = entry.isRecurring();
			this.amount = entry.getAmount();
			this.comment = entry.getComment();
			this.url = entry.getUrl();
			this.paid = entry.isPaid();
			this.date = entry.getDate();
			this.reconciled = entry.isReconciled();
			this.category = entry.getCategory();
		}
		
		/**
		 * @param id The ID of the entry.  May be -1 until the system knows
		 *        the auto increment number.  
		 */
		public Builder id(int id){
			this.id = id;
			return this;
		}
		
		/**
		 * @param description The description of the entry
		 * @return this object
		 */
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		/**
		 * @param recurring Is this entry recurring?  For instance, a gas bill.
		 * @return this object
		 */
		public Builder recurring(boolean recurring) {
			this.recurring = recurring;
			return this;
		}
		
		/**
		 * @param amount The amount of the entry
		 * @return this object
		 */
		public Builder amount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}
		
		/**
		 * @param comment Any text that does not belong in the description
		 * @return this object
		 */
		public Builder comment(String comment) {
			this.comment = comment;
			return this;
		}
		
		/**
		 * @param url A URL to a website where you can pay your bill, for instance
		 * @return this object
		 */
		public Builder url(Optional<URL> url) {
			this.url = url;
			return this;
		}
		
		/**
		 * @param paid Has this entry been paid yet, or has a deposit hit the bank
		 *        yet?  This field is used to calculate the available amount.
		 * @return this object
		 */
		public Builder paid(boolean paid) {
			this.paid = paid;
			return this;
		}
		
		/**
		 * @param date The date on which the entry has or is expected to occur.
		 * @return this object
		 */
		public Builder date(LocalDate date) {
			this.date = date;
			return this;
		}
		
		/**
		 * @param recociled Is this entry reconciled to the bank?
		 * @return this object
		 */
		public Builder reconciled(boolean reconciled) {
			this.reconciled = reconciled;
			return this;
		}
		
		/**
		 * @param category A {@link Category} object which is the category of 
		 *        this entry
		 * @return this object
		 */
		public Builder category(Optional<Category> category) {
			this.category = category;
			return this;
		}
		
		/**
		 * Build an {@link Entry} object from the Builder's data.  Enter optional
		 * data into the Builder object, then call this method, for instance:
		 * 
		 * <pre>
		 * Entry entry = new EntryBalance.Builder(type)
		 *     .amount(12.34)
		 *     .description("Gas Bill")
		 *     .build();
		 * </pre>
		 * 
		 * @return an Entry object
		 */
		public abstract AbstractEntry build();

	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String getDescription() {
		return description.get();
	}
	
	@Override
	public SimpleStringProperty descriptionProperty() {
		return description;
	}

	@Override
	public boolean isRecurring() {
		return recurring.get();
	}
	
	@Override
	public SimpleBooleanProperty recurringProperty() {
		return recurring;
	}

	@Override
	public BigDecimal getAmount() {
		return amount.get();
	}
	
	@Override
	public SimpleObjectProperty<BigDecimal> amountProperty() {
		return amount;
	}

	@Override
	public String getComment() {
		return comment.get();
	}
	
	@Override 
	public SimpleStringProperty commentProperty() {
		return comment;
	}
	
	@Override
	public Optional<URL> getUrl() {
		return url.get();
	}
	
	@Override
	public SimpleObjectProperty<Optional<URL>> urlProperty() {
		return url;
	}
	
	@Override
	public boolean isPaid() {
		return paid.get();
	}
	
	@Override
	public SimpleBooleanProperty paidProperty() {
		return paid;
	}

	@Override
	public LocalDate getDate() {
		return date.get();
	}
	
	@Override
	public SimpleObjectProperty<LocalDate> dateProperty() {
		return date;
	}
	
	@Override
	public boolean isReconciled() {
		return reconciled.get();
	}

	@Override
	public SimpleBooleanProperty recociledProperty() {
		return reconciled;
	}

	@Override
	public Optional<Category> getCategory() {
		return category;
	}
	
	@Override
	public String getCategoryDesc() {
		return category.isPresent() ? category.get().getDescription() : "";
	}
	
	@Override
	public SimpleStringProperty categoryDescProperty() {
		categoryDesc.set(getCategoryDesc());
		return categoryDesc;
	}

	@Override
	public EntryType getType() {
		return type;
	}

}
