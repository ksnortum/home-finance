package net.snortum.homefinance;

import org.apache.log4j.Logger;

import javafx.fxml.FXML;
import net.snortum.homefinance.controller.DepositEntryApplication;

public class HomeFinanceController {
	
	private static final Logger LOG = Logger.getLogger(HomeFinanceController.class);
	
	private HomeFinance root;
	
	@FXML
	private void handleCategoryMaintenance() {
		root.showCategoryMaintenance();
	}
	
	@FXML
	private void handleBudgetMaintenance() {
		root.showBudgetMaintenance();
	}
	
	@FXML
	private void handleDepositEntry() {
		LOG.info("Launching Deposit Entry");
		new DepositEntryApplication(root.getPrimaryStage());
	}
	
	public void setRootApplication(HomeFinance root) {
		this.root = root;
	}
	
}
