package net.snortum.homefinance.model;

/**
 * Increase Assets, mostly deposits
 * @author Knute Snortum
 * @version 2016-08-21
 */
public class EntryIn extends AbstractEntry {

	public static class Builder extends AbstractEntry.Builder {

		public Builder() {
			super(EntryType.IN);
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
		this.category = builder.category;
		this.categoryDesc.set(this.category.get().getDescription());
	}

}
