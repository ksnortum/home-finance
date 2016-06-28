package net.snortum.homefinance.model;

public class EntryBalance extends AbstractEntry {

	public static class Builder extends AbstractEntry.Builder {

		public Builder(EntryType type) {
			super(type);
		}

		@Override
		public AbstractEntry build() {
			return new EntryBalance(this);
		}
		
	}
	
	private EntryBalance(Builder builder) {
		this.id = builder.id;
		this.description = builder.description;
		this.recurring = builder.recurring;
		this.amount = builder.amount;
		this.comment = builder.comment;
		this.url = builder.url;
		this.paid = builder.paid;
		this.date = builder.date;
		this.category = builder.category;
		this.type = EntryType.BAL;
	}
	
	/**
	 * Return a new EntryBalance object with a new ID
	 * 
	 * @param id the new ID
	 * @param entry all the other values
	 */
	public EntryBalance(int id, EntryBalance entry) {
		this.id = id;
		this.description = entry.getDescription();
		this.recurring = entry.isRecurring();
		this.amount = entry.getAmount();
		this.comment = entry.getComment();
		this.url = entry.getUrl();
		this.paid = entry.isPaid();
		this.date = entry.getDate();
		this.category = entry.getCategory();
		this.type = entry.getType();
	}

	public EntryType getType() {
		return type;
	}

}
