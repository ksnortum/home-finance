package net.snortum.homefinance.model;

public class EntryIn extends AbstractEntry {

	public static class Builder extends AbstractEntry.Builder {

		public Builder(EntryType type) {
			super(type);
		}

		@Override
		public AbstractEntry build() {
			return new EntryIn(this);
		}
		
	}
	
	private EntryIn(Builder builder) {
		this.id = builder.id;
		this.description = builder.description;
		this.recurring = builder.recurring;
		this.amount = builder.amount;
		this.comment = builder.comment;
		this.url = builder.url;
		this.paid = builder.paid;
		this.date = builder.date;
		this.category = builder.category;
		this.type = EntryType.IN;
	}
	
	/**
	 * Return a new EntryIn object with a new ID
	 * 
	 * @param id the new ID
	 * @param entry all the other values
	 */
	public EntryIn(int id, EntryIn entry) {
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
