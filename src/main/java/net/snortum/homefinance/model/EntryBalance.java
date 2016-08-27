package net.snortum.homefinance.model;

public class EntryBalance extends AbstractEntry {

	public static class Builder extends AbstractEntry.Builder {

		public Builder() {
			super(EntryType.BAL);
		}

		@Override
		public AbstractEntry build() {
			return new EntryBalance(this);
		}
		
	}
	
	private EntryBalance(Builder builder) {
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
