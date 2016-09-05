package net.snortum.homefinance.model;

import java.util.regex.Pattern;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public abstract class NumberEditingCell<T extends ValueSettable<U>, U extends Number>
		extends TableCell<T, U> {
	
	/**
	 * Regex that matches a integer.  If the passed-in regex does not equal
	 * this regex, it's considered a double pattern. 
	 */
	public static final String INTEGER_REGEX = "-?\\d+";
	
	/** Regex that matches a double with any number of decimal places */
	public static final String DOUBLE_REGEX = 
			"-?"            // optional negative sign
			+ "(?:"         // non-grouping parens
			+   "\\d+"      // if we start with a digit...
			+   "(?:"
			+     "\\.\\d*" // ...digits need not follow the decimal point
			+   ")?"        // and decimal place is optional
			+ ")"
			+ "||"          // --or--
			+ "(?:"
			+   "\\.\\d+"   // digits must follow if pattern starts with a decimal point
			+ ")";
	
	/** Regex that matches a double with a maximum of two decimal places */
	public static final String MONEY_REGEX = 
			"-?"            // optional negative sign
			+ "(?:"         // non-grouping parens
			+   "\\d+"      // if we start with a digit...
			+   "(?:"
			+     "\\.\\d{0,2}" // ...digits need not follow the decimal point (max 2)
			+   ")?"        // and decimal place is optional
			+ ")"
			+ "||"          // --or--
			+ "(?:"
			+   "\\.\\d{1,2}" // max of 2 digits must follow if pattern starts with a decimal point
			+ ")";

	private final TextField textField = new TextField();
	private final String numberRegex;
	private final Pattern numberPattern;

	/**
	 * Create an editing cell in a {@link TableView} for a number.  The 
	 * intension is that {@code numberRegex} will be one of the constants
	 * in this class, but any compilable regex will work.  If the regex
	 * equals {@code INTEGER_REGEX}, it is assumed to be an integer, 
	 * otherwise it's assumed to be a double.
	 * 
	 * Set {@code textField} to process editing.
	 * 
	 * @param numberRegex A regex that will match the correct amount format
	 */
	public NumberEditingCell(String numberRegex) {
		this.numberRegex = numberRegex;
		this.numberPattern = Pattern.compile(numberRegex);
		textField.focusedProperty().addListener(
				(obs, wasFocused, isNowFocused) -> {
					if (!isNowFocused) {
						processEdit();
					}
				});
		textField.setOnAction(event -> processEdit());
	}
	
	/**
	 * Default to the {@code MONEY_REGEX} format.  
	 */
	public NumberEditingCell() {
		this(MONEY_REGEX);
	}

	@SuppressWarnings("unchecked")
	private void processEdit() {
		String text = textField.getText();
		if (numberPattern.matcher(text).matches()) {
			if (numberRegex.equals(INTEGER_REGEX)) {
				commitEdit((U)Integer.valueOf(text));
			} else {
				commitEdit((U)Double.valueOf(text));
			}
		} else {
			cancelEdit();
		}
	}

	@Override
	public void updateItem(U value, boolean empty) {
		super.updateItem(value, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else if (isEditing()) {
			setText(null);
			textField.setText(value.toString());
			setGraphic(textField);
		} else {
			setText(value.toString());
			setGraphic(null);
		}
	}

	@Override
	public void startEdit() {
		super.startEdit();
		Number value = getItem();
		if (value != null) {
			textField.setText(value.toString());
			setGraphic(textField);
			setText(null);
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem().toString());
		setGraphic(null);
	}

	/**
	 * Override this method to set the correct amount field in {@code T}.
	 */
	@SuppressWarnings("unchecked") // getItem() returns Object but we need T
	@Override
	public void commitEdit(U value) {
		super.commitEdit(value);
		((T)this.getTableRow().getItem()).setValue(value);
	}
}
