package net.snortum.homefinance.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.snortum.homefinance.dao.EntryInDao;
import net.snortum.homefinance.model.Entry;
import net.snortum.homefinance.model.EntryIn;
import net.snortum.homefinance.model.EntryInputData;

public class DepositEntryOverviewController {

	private static final Logger LOG = Logger
			.getLogger(DepositEntryOverviewController.class);

	@FXML
	private TableView<Entry> depositTable;
	@FXML
	private TableColumn<Entry, LocalDate> dateColumn;
	@FXML
	private TableColumn<Entry, BigDecimal> amountColumn;

	@FXML
	private TextField dateField;
	@FXML
	private TextField amountField;
	@FXML
	private TextField descriptionField;
	@FXML
	private TextField commentField;
	@FXML
	private TextField urlField;
	@FXML
	private TextField categoryField;
	@FXML
	private CheckBox recurringChk;

	private DepositEntryApplication depositEntryApplication;
	private final EntryInDao depositEntryDao = new EntryInDao();

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the FXML file has been loaded.
	 */
	@FXML
	private void initialize() {

		// Initialize the person table with the two columns.
		dateColumn.setCellValueFactory(
				cellData -> cellData.getValue().dateProperty());
		amountColumn.setCellValueFactory(
				cellData -> cellData.getValue().amountProperty());

		// Clear person details.
		showDepositDetails(Optional.empty());

		// Listen for selection changes and show the person details when
		// changed.
		depositTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue,
						newValue) -> showDepositDetails(Optional.of(newValue)));
	}

	/**
	 * Set the DepositEntryApplication object. This allows a connection with the
	 * deposit data and the primary stage of the menu.
	 * 
	 * @param depositEntryApplication
	 *            the application object to set
	 */
	public void setApplication(
			DepositEntryApplication depositEntryApplication) {
		this.depositEntryApplication = depositEntryApplication;

		// Add observable list data to the table
		depositTable.setItems(depositEntryApplication.getDepositData());
	}

	/**
	 * Fills all text fields to show details about the person. If the specified
	 * person is null, all text fields are cleared.
	 * 
	 * @param person
	 *            the person or null
	 */
	private void showDepositDetails(Optional<Entry> depositOption) {
		if (depositOption.isPresent()) {
			// Fill the labels with info from the deposit object.
			Entry deposit = depositOption.get();
			dateField.setText(
					deposit.getDate().format(EntryValidator.DATE_FORMATTER));
			amountField.setText(EntryValidator.AMOUNT_FORMATTER
					.format(deposit.getAmount()));
			descriptionField.setText(deposit.getDescription());
			commentField.setText(deposit.getComment());
			urlField.setText(deposit.getUrl().isPresent()
					? deposit.getUrl().get().toString() : "");
			categoryField.setText(deposit.getCategoryDesc());
			recurringChk.setSelected(deposit.isRecurring());
		} else {
			// clear all the text.
			dateField.setText("");
			amountField.setText("");
			descriptionField.setText("");
			commentField.setText("");
			urlField.setText("");
			categoryField.setText("");
			recurringChk.setSelected(false);
		}
	}

	/**
	 * Called when the user clicks on the delete button.
	 */
	@FXML
	private void handleDeleteDeposit() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Delete deposit entry button pressed");
		}

		Optional<Integer> selectedIndex = getSelectedIndex();

		if (selectedIndex.isPresent()) {
			// Remove from DB
			Entry deposit = depositTable.getItems().get(selectedIndex.get());
			depositEntryDao.delete(deposit.getId());
			// Remove from observable list
			depositTable.getItems().remove(selectedIndex);
		}
	}

	/**
	 * Called when user clicks the Save button
	 */
	@FXML
	private void handleSaveDeposit() {
		Optional<Integer> selectedIndex = getSelectedIndex();

		if (selectedIndex.isPresent() && validateInputData()) {
			Entry deposit = depositTable.getItems().get(selectedIndex.get());
			Entry newDeposit = updateDB(deposit);
			depositTable.getItems().remove(deposit);
			depositTable.getItems().add(newDeposit);
		}
	}
	
	/**
	 * Called when the user clicks "Ok"
	 */
	@FXML
	private void handleOkButton() {
		// Save before closing
		int selectedIndex = depositTable.getSelectionModel().getSelectedIndex();
		
		if (selectedIndex >= 0) {
			handleSaveDeposit();
		}
		
		depositEntryApplication.getEntryRootStage().close();
	}

	private Optional<Integer> getSelectedIndex() {
		int selectedIndex = depositTable.getSelectionModel().getSelectedIndex();

		if (selectedIndex < 0) {
			warnNothingSelected();
			return Optional.empty();
		} else {
			return Optional.of(selectedIndex);
		}
	}

	private void warnNothingSelected() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(depositEntryApplication.getEntryRootStage());
		alert.setTitle("No Selection");
		alert.setHeaderText("No Deposit Selected");
		alert.setContentText(
				"Please select a deposit in the table on the left");
		alert.showAndWait();
	}

	private boolean validateInputData() {
		EntryValidator validator = new EntryValidator(
				new EntryInputData.Builder()
						.date(dateField.getText())
						.amount(amountField.getText())
						.url(urlField.getText())
						.build());
		List<String> errors = validator.validate();

		if (!errors.isEmpty()) {
			String errorsText = errors.stream()
					.collect(Collectors.joining(System.lineSeparator()));
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(depositEntryApplication.getEntryRootStage());
			alert.setTitle("Input Errors");
			alert.setHeaderText("Please Correct These Input Errors");
			alert.setContentText(errorsText);
			alert.showAndWait();

			return false;
		}

		return true;
	}

	private Entry updateDB(Entry deposit) {
		URL url = EntryValidator.getUrl(urlField.getText());
		Entry newDeposit = new EntryIn.Builder(deposit)
				.description(descriptionField.getText())
				.recurring(recurringChk.isSelected())
				.amount(EntryValidator.parseAmountText(amountField.getText()))
				.comment(commentField.getText())
				.url(url == null ? Optional.empty() : Optional.of(url))
				.paid(true)
				.date(LocalDate.parse(dateField.getText()))
				.reconciled(true)
				.category(Optional.empty())
				.build();
		
		if ( ! depositEntryDao.update(newDeposit) ) {
			LOG.error("Deposit did not update");
		}
		
		return newDeposit;
	}
}
