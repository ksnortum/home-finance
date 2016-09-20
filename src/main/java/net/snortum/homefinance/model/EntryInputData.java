package net.snortum.homefinance.model;

import net.snortum.homefinance.controller.EntryValidator;

/**
 * A class to hold all input data from an Entry form so it can be easily validated.  
 * 
 * @author Knute Snortum
 * @version 2016-09-18
 * @see {@link EntryValidator}
 */
public class EntryInputData {

	private final String dateText;
	private final String amountText;
	private final String urlText;
	
	public static class Builder {
		
		private String dateText;
		private String amountText;
		private String urlText;
		
		public Builder() {
		}
		
		public Builder date(String dateText) {
			this.dateText = dateText;
			return this;
		}
		
		public Builder amount(String amountText) {
			this.amountText = amountText;
			return this;
		}
		
		public Builder url(String urlText) {
			this.urlText = urlText;
			return this;
		}
		
		public EntryInputData build() {
			return new EntryInputData(this);
		}
		
	}
	
	private EntryInputData(Builder builder) {
		this.dateText = builder.dateText;
		this.amountText = builder.amountText;
		this.urlText = builder.urlText;
	}
	
	public String getDateText() {
		return dateText;
	}
	
	public String getAmountText() {
		return amountText;
	}
	
	public String getUrlText() {
		return urlText;
	}
}
