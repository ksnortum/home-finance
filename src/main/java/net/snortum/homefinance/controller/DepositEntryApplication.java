package net.snortum.homefinance.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.snortum.homefinance.dao.EntryInDao;
import net.snortum.homefinance.model.Entry;

/**
 * Display the Deposit Entry form and provide a place for the primary stage of the menu
 * and the deposit data to exist.  This allows the controller to access this data.
 * 
 * TODO: I don't think we need the root form
 *   
 * @author Knute Snortum
 * @version 2016-09-19
 */
public class DepositEntryApplication {
	private static final Logger LOG = LogManager.getLogger();
	private static final String ROOT_FXML_FILE = "DepositEntryRoot.fxml";
	private static final String OVERVIEW_FXML_FILE = "DepositEntryOverview.fxml";
	
	private AnchorPane rootLayout;
	private Stage entryRootStage;
	private ObservableList<Entry> depositData = FXCollections.observableArrayList();
	
	public DepositEntryApplication(Stage primaryStage) {
		initRootLayout(primaryStage);
		showDepositOverview();
	}

    public final void initRootLayout(Stage primaryStage) {
        try {
            // Load root layout from FXML file.
        	URL url = getClass().getResource(ROOT_FXML_FILE);
            FXMLLoader loader = new FXMLLoader(url);
            rootLayout = (AnchorPane) loader.load();

            // Create the root Stage.
            entryRootStage = new Stage();
            entryRootStage.setTitle("Deposit Entry");
            entryRootStage.initModality(Modality.WINDOW_MODAL);
            entryRootStage.initOwner(primaryStage);
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            entryRootStage.setScene(scene);

            // Give the controller access to the root app.
            DepositEntryRootController controller = loader.getController();
            controller.setApplication(this);

            entryRootStage.show();
        } catch (IOException e) {
            LOG.error("Error trying to load " + ROOT_FXML_FILE, e);
        }
        
        EntryInDao dao = new EntryInDao();
        List<Entry> list = dao.list();
        depositData.addAll(list);
    }
	
    public final void showDepositOverview() {
        try {
            // Load person overview.
        	URL url = getClass().getResource(OVERVIEW_FXML_FILE);
            FXMLLoader loader = new FXMLLoader(url);
            LOG.debug("Deposit Entry Overview location: " + loader.getLocation());
            AnchorPane depositEntryOverview = (AnchorPane) loader.load();

            // Set person overview into root layout
            // TODO: is it necessary to have a root layout?
            AnchorPane.setTopAnchor(depositEntryOverview, 0.0);
            AnchorPane.setLeftAnchor(depositEntryOverview, 0.0);
            AnchorPane.setRightAnchor(depositEntryOverview, 0.0);
            AnchorPane.setBottomAnchor(depositEntryOverview, 0.0);
            rootLayout.getChildren().add(depositEntryOverview);
            
            // Give the controller access to the root app.
            DepositEntryOverviewController controller = loader.getController();
            //loader.setController(controller); // TODO: remove?
            controller.setApplication(this);
            
        } catch (IOException e) {
            LOG.error("Error trying to load " + OVERVIEW_FXML_FILE, e);
        }
    }
    
    public Stage getEntryRootStage() {
    	return entryRootStage;
    }
    
    public ObservableList<Entry> getDepositData() {
        return depositData;
    }
}
