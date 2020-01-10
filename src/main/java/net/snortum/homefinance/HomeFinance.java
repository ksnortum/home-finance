package net.snortum.homefinance;
	
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.snortum.homefinance.controller.BudgetMaintenance;
import net.snortum.homefinance.controller.CategoryMaintenance;
import net.snortum.homefinance.util.DbConnection;
import net.snortum.homefinance.util.DbInitialization;

/**
 * Launch the Home Finance main menu and provide a place to hold the primary stage.
 * 
 * @author Knute Snortum
 * @version 2016-09-20
 */
public class HomeFinance extends Application {
	private static final Logger LOG = LogManager.getLogger();

	private static final String HOME_FINANCE_FXML = "controller/HomeFinance.fxml";
	private static final String CATEGORY_MAINTENANCE_FXML = "controller/CategoryMaintenance.fxml";
	private static final String BUDGET_MAINTENANCE_FXML = "controller/BudgetMaintenance.fxml";
	private static final boolean TESTING = true;
	
	private Stage primaryStage;
	
	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		File dbFile = new File(DbConnection.DB);
		
		// Setup the DB if this is a new install
		if ( ! dbFile.exists() ) {
 			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText("Confirm New Installation");
			alert.setContentText("Do you want to setup a new installation?");
			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get() == ButtonType.CANCEL) {
				LOG.error("Could not find DB file " + DbConnection.DB);
				Platform.exit();
			}
			
			if ( DbInitialization.createTables() ) {
				DbInitialization.populateTables();
			} else {
				Alert warning = new Alert(AlertType.WARNING);
				warning.setHeaderText("Error Initializing");
				warning.setContentText("Could not initialize the DB. Check log file");
				warning.showAndWait();
				Platform.exit();
			}
		}
		
		if (TESTING) {
			DbInitialization.addDepositsForTesting();
		}
		
		displayMainPane();
	}

	private void displayMainPane() {
		try {
			// Load FXML
			URL url = getClass().getResource(HOME_FINANCE_FXML);
			FXMLLoader loader = new FXMLLoader(url);
			BorderPane root = (BorderPane) loader.load();
			
			// Display
			Scene scene = new Scene(root);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Home Finance");
			primaryStage.show();
			
			// Set reference to this instance in controller and stage
			HomeFinanceController controller = loader.getController();
			controller.setRootApplication(this);
			
		} catch(IOException e) {
			LOG.error("Could not find " + HOME_FINANCE_FXML, e);
		}
		
		if (LOG.isInfoEnabled()) {
			LOG.info("Main menu launched");
		}
	}
	
	/**
	 * Display Category Maintenance when button is clicked
	 */
	public void showCategoryMaintenance() {
		try {
			// Load FXML
			URL url = getClass().getResource(CATEGORY_MAINTENANCE_FXML);
			FXMLLoader loader = new FXMLLoader(url);
			VBox pane = loader.<VBox>load();
			
			// Create a new window and make it modal
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Category Maintenance");
			dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            
            // Load controller to set dialog stage so that the controller
            // knows how to close the window
            CategoryMaintenance controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            // Show the dialog and wait until the user closes it
            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
		} catch (IOException e) {
			LOG.error("Could not find " + CATEGORY_MAINTENANCE_FXML, e);
		}
	}
	
	/**
	 * Display Budget Maintenance when button is clicked
	 */
	public void showBudgetMaintenance() {
		try {
			// Load FXML
			URL url = getClass().getResource(BUDGET_MAINTENANCE_FXML);
			FXMLLoader loader = new FXMLLoader(url);
			AnchorPane pane = (AnchorPane) loader.load();
			
			// Create a new window and make it modal
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Budget Maintenance");
			dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            
            // Load controller to set dialog stage so that the controller
            // knows how to close the window
            BudgetMaintenance controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            // Show the dialog and wait until the user closes it
            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
		} catch (IOException e) {
			LOG.error("Could not find " + BUDGET_MAINTENANCE_FXML, e);
		}
	}
	
	/**
	 * @return the primary stage of the menu (main pane)
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
