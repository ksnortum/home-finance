package net.snortum.homefinance.model;

/**
 * Increase Assets, mostly deposits
 * @author Knute Snortum
 * @version 2016-09-20
 */
public class EntryIn extends AbstractEntry {

	public static class Builder extends AbstractEntry.Builder {

		public Builder() {
			super(EntryType.IN);
		}
		
		public Builder(Entry entry) {
			super(entry);
		}

		@Override
		public AbstractEntry build() {
			return new EntryIn(this);
		}
		
	}
	
	private EntryIn(Builder builder) {
		this.id = builder.id;
		this.type = builder.type;
		this.description.set(builder.description);
		this.recurring.set(builder.recurring);
		this.amount.set(builder.amount);
		this.comment.set(builder.comment);
		this.url.set(builder.url);
		this.paid.set(builder.paid);
		this.date.set(builder.date);
		this.category = builder.category;
		this.categoryDesc.set(this.category.isPresent() ? this.category.get().getDescription() : "");
	}

}
