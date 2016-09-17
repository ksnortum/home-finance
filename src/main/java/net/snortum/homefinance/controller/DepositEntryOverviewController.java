package net.snortum.homefinance.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

public class DepositEntryOverviewController {
	
	private static final Logger LOG = Logger.getLogger(DepositEntryOverviewController.class);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final NumberFormat AMOUNT_FORMATTER = NumberFormat.getCurrencyInstance();
	
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

        // Listen for selection changes and show the person details when changed.
        depositTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDepositDetails(Optional.of(newValue)));
    }	

	public void setApplication(
			DepositEntryApplication depositEntryApplication) {
		this.depositEntryApplication = depositEntryApplication;
		
		// Add observable list data to the table
        depositTable.setItems(depositEntryApplication.getDepositData()); 
	}
	
    /**
     * Fills all text fields to show details about the person.
     * If the specified person is null, all text fields are cleared.
     * 
     * @param person the person or null
     */
    private void showDepositDetails(Optional<Entry> depositOption) {
        if (depositOption.isPresent()) {
            // Fill the labels with info from the deposit object.
        	Entry deposit = depositOption.get();
            dateField.setText(deposit.getDate().format(DATE_FORMATTER));
            amountField.setText(AMOUNT_FORMATTER.format(deposit.getAmount()));
            descriptionField.setText(deposit.getDescription());
            commentField.setText(deposit.getComment());
            urlField.setText(deposit.getUrl().isPresent() ? deposit.getUrl().get().toString() : "");
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
    	if (LOG.isInfoEnabled()) {
    		LOG.info("Delete deposit entry button pressed");
    	}
        int selectedIndex = depositTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
        	// Remove from DB
        	Entry deposit = depositTable.getItems().get(selectedIndex);
        	depositEntryDao.delete(deposit.getId());
        	// Remove from observable list
            depositTable.getItems().remove(selectedIndex);
        } else {
            // Nothing selected
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(depositEntryApplication.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Deposit Selected");
            alert.setContentText("Please select a deposit in the table.");
            alert.showAndWait();
        }
    }
}
