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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.Callback;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.NumberStringConverter;
import net.snortum.homefinance.dao.EntryInDao;
import net.snortum.homefinance.model.Entry;
import net.snortum.homefinance.model.EntryIn;
import net.snortum.homefinance.model.EntryInputData;

/**
 * Controls the Deposit Entry form.
 * 
 * TODO: Category as a dropdown
 * 
 * @author Knute Snortum
 * @version 2016-11-09
 */
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

		// Initialize the deposit table with the two columns.
		dateColumn.setCellValueFactory(
				cellData -> cellData.getValue().dateProperty());
		amountColumn.setCellValueFactory(
				cellData -> cellData.getValue().amountProperty());

		// Amount needs custom formatting
		amountColumn.setCellFactory(getCustomCellFactory());

		clearDepositDetails();

		// Listen for selection changes and show the deposit details when
		// changed.
		depositTable.getSelectionModel()
				.selectedItemProperty()
				.addListener((observable, oldValue,
						newValue) -> showDepositDetails(newValue));

		// Format amount when a new amount is entered
		amountField.setTextFormatter(new TextFormatter<Number>(
				new NumberStringConverter(EntryValidator.AMOUNT_PATTERN)));

		// Format date when new date is entered
		dateField.setTextFormatter(new TextFormatter<LocalDate>(
				new LocalDateStringConverter(EntryValidator.DATE_FORMATTER,
						EntryValidator.DATE_FORMATTER)));
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
		} else {
			warnNothingSelected();
		}
	}

	/**
	 * Called when user clicks the Save button
	 */
	@FXML
	private void handleSaveDeposit() {
		Optional<Integer> selectedIndex = getSelectedIndex();

		if (selectedIndex.isPresent()) {
			if (validateInputData()) {
				// update
				Entry deposit = depositTable.getItems()
						.get(selectedIndex.get());
				Optional<Entry> entryOption = updateDeposit(deposit);

				if (entryOption.isPresent()) {
					depositTable.getItems().remove(deposit);
					depositTable.getItems().add(entryOption.get());
					clearDepositDetails();
				}
			}
		} else {
			if (anythingEntered() && validateInputData()) {
				// create
				Optional<Entry> entryOption = updateDeposit(
						new EntryIn.Builder().build());

				if (entryOption.isPresent()) {
					depositTable.getItems().add(entryOption.get());
					clearDepositDetails();
				}
			}
		}

	}

	/**
	 * Called when the user clicks "OK"
	 */
	@FXML
	private void handleOkButton() {
		handleSaveDeposit();
		depositEntryApplication.getEntryRootStage().close();
	}

	/**
	 * Called when the user clicks "Cancel"
	 */
	@FXML
	private void handleCancelButton() {
		depositEntryApplication.getEntryRootStage().close();
	}

	/**
	 * Called when the user clicks "New"
	 */
	@FXML
	private void handleNewButton() {
		clearDepositDetails();

		// No selection and ID absent trigger create mode
		depositTable.getSelectionModel().clearSelection();
	}

	/**
	 * Fills all text fields to show details about the deposit
	 * 
	 * @param deposit
	 *            the deposit entry
	 */
	private void showDepositDetails(Entry deposit) {
		if (deposit == null) {
			clearDepositDetails();
		} else {
			dateField.setText(
					deposit.getDate().format(EntryValidator.DATE_FORMATTER));
			amountField.setText(String.valueOf(deposit.getAmount()));
			descriptionField.setText(deposit.getDescription());
			commentField.setText(deposit.getComment());
			urlField.setText(deposit.getUrl().isPresent()
					? deposit.getUrl().get().toString() : "");
			categoryField.setText(deposit.getCategoryDesc());
			recurringChk.setSelected(deposit.isRecurring());
		}
	}

	private void clearDepositDetails() {
		dateField.clear();
		amountField.clear();
		descriptionField.clear();
		commentField.clear();
		urlField.clear();
		categoryField.clear();
		recurringChk.setSelected(false);
	}

	// All this garbage is needed just to format a table cell. Yuck!
	private Callback<TableColumn<Entry, BigDecimal>, TableCell<Entry, BigDecimal>> getCustomCellFactory() {
		return new Callback<TableColumn<Entry, BigDecimal>, TableCell<Entry, BigDecimal>>() {

			@Override
			public TableCell<Entry, BigDecimal> call(
					TableColumn<Entry, BigDecimal> param) {
				TableCell<Entry, BigDecimal> cell = new TableCell<Entry, BigDecimal>() {

					// updateItem() is very brittle and can easily break if not
					// implemented properly. The second setText() is the only
					// code that should be changed.
					@Override
					protected void updateItem(BigDecimal amount,
							boolean empty) {
						super.updateItem(amount, empty);

						if (empty || amount == null) {
							setText(null);
						} else {
							setText(EntryValidator.AMOUNT_FORMATTER
									.format(amount.doubleValue()));
						}
					}
				};
				return cell;
			}
		};
	}

	private Optional<Integer> getSelectedIndex() {
		int selectedIndex = depositTable.getSelectionModel().getSelectedIndex();

		if (selectedIndex < 0) {
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

	private boolean anythingEntered() {
		return !dateField.getText().isEmpty()
				|| !amountField.getText().isEmpty()
				|| !urlField.getText().isEmpty();
	}

	private Optional<Entry> updateDeposit(Entry deposit) {
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

		if (deposit.isIdAbsent()) {
			Optional<Entry> entryOption = depositEntryDao.create(newDeposit);
			if (entryOption.isPresent()) {
				newDeposit = entryOption.get();
			} else {
				LOG.error("Deposit was not created");
				newDeposit = null;
			}
		} else {
			if (!depositEntryDao.update(newDeposit)) {
				LOG.error("Deposit did not update");
				newDeposit = null;
			}
		}

		return newDeposit == null ? Optional.empty() : Optional.of(newDeposit);
	}
}
