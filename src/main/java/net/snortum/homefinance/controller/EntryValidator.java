package net.snortum.homefinance.controller;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import net.snortum.homefinance.model.EntryInputData;

/**
 * Contains all the code necessary to validate input data from Entry forms.
 *  
 * @author Knute Snortum
 * @version 2016-09-20
 * @see EntryInputData
 */
public class EntryValidator {

	static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
	static final NumberFormat AMOUNT_FORMATTER = NumberFormat.getCurrencyInstance();
	
	private static final String DATE_ERROR_MESSAGE = "Invalid date, enter in the form: "
			+ DATE_FORMATTER.toString();
	private static final String AMOUNT_ERROR_MESSAGE = "Invalid amount";
	private static final String URL_ERROR_MESSAGE = "Invalid URL";
	
	private final EntryInputData input;
	
	/**
	 * Create a new EntryValidator
	 * 
	 * @param input an {@link EntryInputData} object containing all the input data to validate 
	 */
	public EntryValidator(EntryInputData input) {
		this.input = input;
	}
	
	/**
	 * Validate input data
	 * 
	 * @return a list of validation error messages, if any
	 */
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
		
		if ( ! isValidDate(input.getDateText()) ) {
			errors.add(DATE_ERROR_MESSAGE);
		}
		
		if ( ! isValidAmount(input.getAmountText()) ) {
			errors.add(AMOUNT_ERROR_MESSAGE);
		}
		
		if ( ! isValidUrl(input.getUrlText()) ) {
			errors.add(URL_ERROR_MESSAGE);
		}
		
		return errors;
	}

	/**
	 * Convert a string to a URL
	 * 
	 * @param url the string to convert to a URL
	 * @return the URL from the string, or {@code null} if invalid or blank
	 */
	public static URL getUrl(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	/**
	 * Parse a BigDecimal from an amount text string.
	 * 
	 * @param amountText the string containing the formatted number
	 * @return a BigDecimal parsed from the string.  Amount will be 0.0 if there is a formatting error.
	 */
	public static BigDecimal parseAmountText(String amountText) {
		Number number;
		
		try {
			number = AMOUNT_FORMATTER.parse(amountText);
		} catch (ParseException e) {
			number = 0.0;
		}
		
		return BigDecimal.valueOf((double) number);
	}
	
	private boolean isValidDate(String dateText) {
		try {
			LocalDate.parse(dateText, DATE_FORMATTER);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
	
	private boolean isValidAmount(String amountText) {
		try {
			AMOUNT_FORMATTER.parse(amountText);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	private boolean isValidUrl(String urlText) {
		if (urlText == null || urlText.isEmpty()) {
			return true;
		}
		
		try {
			new URL(urlText);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
