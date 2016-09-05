package net.snortum.homefinance.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.snortum.homefinance.dao.EntryInDao;
import net.snortum.homefinance.model.Entry;

public class DepositEntryApplication {
	private static final Logger LOG = Logger.getLogger(DepositEntryApplication.class);
	private static final String ROOT_FXML_FILE = "/fxml/DepositEntryRoot.fxml";
	private static final String OVERVIEW_FXML_FILE = "/fxml/DepositEntryOverview.fxml";
	
	private BorderPane rootLayout;
	private Stage primaryStage;
	private ObservableList<Entry> depositData = FXCollections.observableArrayList();
	
	public DepositEntryApplication(Stage primaryStage) {
		this.primaryStage = primaryStage;
		initRootLayout();
		showDepositOverview();
	}

    public final void initRootLayout() {
        try {
            // Load root layout from FXML file.
        	URL url = getClass().getResource(ROOT_FXML_FILE);
            FXMLLoader loader = new FXMLLoader(url);
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the root app.
            DepositEntryRootController controller = loader.getController();
            controller.setApplication(this);

            primaryStage.show();
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
            LOG.info("Deposit Entry Overview location: " + loader.getLocation());
            AnchorPane depositEntryOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(depositEntryOverview);
            
            // Give the controller access to the root app.
            DepositEntryOverviewController controller = loader.getController();
            loader.setController(controller);
            controller.setApplication(this);
            
        } catch (IOException e) {
            LOG.error("Error trying to load " + OVERVIEW_FXML_FILE, e);
        }
    }
    
    public Stage getPrimaryStage() {
    	return primaryStage;
    }
    
    public ObservableList<Entry> getDepositData() {
        return depositData;
    }
}
