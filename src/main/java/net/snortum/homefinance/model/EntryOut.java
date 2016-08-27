package net.snortum.homefinance.model;

public class EntryOut extends AbstractEntry {

	public static class Builder extends AbstractEntry.Builder {

		public Builder() {
			super(EntryType.OUT);
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
		this.category = builder.category;
		this.categoryDesc.set(this.category.get().getDescription());
	}

}
