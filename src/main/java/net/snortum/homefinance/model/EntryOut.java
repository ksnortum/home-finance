package net.snortum.homefinance.model;

public class EntryOut extends AbstractEntry {

	public static class Builder extends AbstractEntry.Builder {

		public Builder(EntryType type) {
			super(type);
		}

		@Override
		public AbstractEntry build() {
			return new EntryOut(this);
		}
		
	}
	
	private EntryOut(Builder builder) {
		this.id = builder.id;
		this.description.set(builder.description);
		this.recurring.set(builder.recurring);
		this.amount.set(builder.amount);
		this.comment.set(builder.comment);
		this.url.set(builder.url);
		this.paid.set(builder.paid);
		this.date.set(builder.date);
		this.category.set(builder.category);
		this.type.set(EntryType.OUT);
	}
	
	/**
	 * Return a new EntryOut object with a new ID
	 * 
	 * @param id the new ID
	 * @param entry all the other values
	 */
	public EntryOut(int id, EntryOut entry) {
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

	public EntryType getType() {
		return type.get();
	}

}
