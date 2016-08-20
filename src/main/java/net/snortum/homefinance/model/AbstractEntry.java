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
 * @version 2016-08-19
 */
public abstract class AbstractEntry implements Entry {
	
	protected int id;
	protected SimpleStringProperty description;
	protected SimpleBooleanProperty recurring;
	protected SimpleObjectProperty<BigDecimal> amount;
	protected SimpleStringProperty comment;
	protected SimpleObjectProperty<Optional<URL>> url;
	protected SimpleBooleanProperty paid;
	protected SimpleObjectProperty<LocalDate> date;
	protected SimpleObjectProperty<Optional<Category>> category;
	protected SimpleObjectProperty<EntryType> type;

	/**
	 * Used to create {@link Entry} objects
	 * 
	 * @author Knute Snortum
	 * @version 2015-12-13
	 */
	public static abstract class Builder {
		
		// Mandatory
		EntryType type;
		
		// Optional, set default values
		int id = 0;
		String description = "";
		boolean recurring = false;
		BigDecimal amount = BigDecimal.ZERO;
		String comment = "";
		Optional<URL> url = Optional.empty();
		boolean paid = false;
		LocalDate date = LocalDate.now();
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
		 * @param id The ID of the entry.  May be zero until the system knows
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
		 * @param category A {@link Category} object which is the category of 
		 *        this entry
		 * @return this object
		 */
		public Builder category(Category category) {
			this.category = Optional.of(category);
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
	public String getDescription() {
		return description.get();
	}

	@Override
	public boolean isRecurring() {
		return recurring.get();
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
	public Optional<URL> getUrl() {
		return url.get();
	}
	
	@Override
	public boolean isPaid() {
		return paid.get();
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
	public Optional<Category> getCategory() {
		return category.get();
	}
	
	@Override
	public String getCategoryDesc() {
		return category.get().isPresent() ? category.get().get().getDescription() : "";
	}
	
	@Override
	public int getCategoryId() {
		return category.get().isPresent() ? category.get().get().getId() : 0;
	}

}
