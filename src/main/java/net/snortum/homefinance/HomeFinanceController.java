package net.snortum.homefinance;

import javafx.fxml.FXML;

public class HomeFinanceController {
	
	private HomeFinance root;
	
	@FXML
	private void handleCategoryMaintenance() {
		root.showCategoryMaintenance();
	}
	
	@FXML
	private void handleBudgetMaintenance() {
		root.showBudgetMaintenance();
	}
	
	public void setRootApplication(HomeFinance root) {
		this.root = root;
	}
}
