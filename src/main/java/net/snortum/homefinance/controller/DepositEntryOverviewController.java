package net.snortum.homefinance.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import net.snortum.homefinance.model.EntryIn;

public class DepositEntryOverviewController {
	
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final NumberFormat AMOUNT_FORMATTER = NumberFormat.getCurrencyInstance();
	
	@FXML
	private TableView<EntryIn> depositTable; 
	@FXML
	private TableColumn<EntryIn, LocalDate> dateColumn;
	@FXML
	private TableColumn<EntryIn, BigDecimal> amountColumn;
	
	@FXML
	private Label dateLabel;
	@FXML
	private Label amountLabel;
	@FXML
	private Label descriptionLabel;
	@FXML
	private Label commentLabel;
	@FXML
	private Label urlLabel;
	@FXML
	private Label categoryLabel;
	@FXML
	private Label recurringLabel;
	
	private DepositEntryApplication depositEntryApplication;
	
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
	}
	
    /**
     * Fills all text fields to show details about the person.
     * If the specified person is null, all text fields are cleared.
     * 
     * @param person the person or null
     */
    private void showDepositDetails(Optional<EntryIn> depositOption) {
        if (depositOption.isPresent()) {
            // Fill the labels with info from the deposit object.
        	EntryIn deposit = depositOption.get();
            dateLabel.setText(deposit.getDate().format(DATE_FORMATTER));
            amountLabel.setText(AMOUNT_FORMATTER.format(deposit.getAmount()));
            descriptionLabel.setText(deposit.getDescription());
            commentLabel.setText(deposit.getComment());
            urlLabel.setText(deposit.getUrl().isPresent() ? deposit.getUrl().get().toString() : "");
            categoryLabel.setText(deposit.getCategoryDesc());
            recurringLabel.setText(deposit.isRecurring() ? "Yes" : "No");
        } else {
            // clear all the text.
        	dateLabel.setText("");
        	amountLabel.setText("");
        	descriptionLabel.setText("");
        	commentLabel.setText("");
            urlLabel.setText("");
            categoryLabel.setText("");
            recurringLabel.setText("");
        }
    }
    
    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDeletePerson() {
        int selectedIndex = depositTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            depositTable.getItems().remove(selectedIndex);
            // TODO: remove from DB
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(depositEntryApplication.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }
}
