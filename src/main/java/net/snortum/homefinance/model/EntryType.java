package net.snortum.homefinance.model;

/**
 * The type of {@link Entry}.  
 * 
 * <ul>
 *     <li><b>IN</b>: Money going into the account, usually deposits</li>
 *     <li><b>OUT</b>: Money going out of an account, usually bills</li>
 *     <li><b>BAL</b>: Balance forward - money from last month</li>
 * </ul>
 * 
 * @author Knute Snortum
 * @version 2015-12-16
 */
public enum EntryType {
	IN ("in"), 
	OUT ("out"), 
	BAL ("balance");
	
	private String description;
	
	private EntryType(String description) {
		this.description = description;
	}
	
	public String getDesc() {
		return description;
	}
}
