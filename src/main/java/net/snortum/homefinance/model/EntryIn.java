package net.snortum.homefinance.model;

/**
 * Increase Assets, mostly deposits
 * @author Knute Snortum
 * @version 2016-08-19
 */
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
		this.description.set(builder.description);
		this.recurring.set(builder.recurring);
		this.amount.set(builder.amount);
		this.comment.set(builder.comment);
		this.url.set(builder.url);
		this.paid.set(builder.paid);
		this.date.set(builder.date);
		this.category.set(builder.category);
		this.type.set(EntryType.IN);
	}
	
	/**
	 * Return a new EntryIn object with a new ID
	 * 
	 * @param id the new ID
	 * @param entry all the other values
	 */
	public EntryIn(int id, EntryIn entry) {
		this.id = id;
		this.description.set(entry.getDescription());
		this.recurring.set(entry.isRecurring());
		this.amount.set(entry.getAmount());
		this.comment.set(entry.getComment());
		this.url.set(entry.getUrl());
		this.paid.set(entry.isPaid());
		this.date.set(entry.getDate());
		this.category.set(entry.getCategory());
		this.type.set(entry.getType());
	}

	@Override
	public EntryType getType() {
		return type.get();
	}

}
