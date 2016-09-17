package net.snortum.homefinance.controller;

import java.util.stream.Collectors;

import java.util.Optional;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import net.snortum.homefinance.dao.BudgetDao;
import net.snortum.homefinance.dao.CategoryDao;
import net.snortum.homefinance.model.Budget;
import net.snortum.homefinance.model.Category;

public class BudgetMaintenance {
	
	private static final Logger LOG = Logger.getLogger(BudgetMaintenance.class);
	
	// Fields tied to FXML
	
	@FXML
	private ComboBox<String> categoryDescription;
	
	@FXML 
	private TextField budgetAmount;
	
	@FXML
	private Button okBtn;
	
	@FXML
	private Button deleteBtn;
	
	// Other fields
	private ObservableList<String> categories;
	private CategoryDao categoryDao = new CategoryDao();
	private BudgetDao budgetDao = new BudgetDao();
	private Optional<Budget> budget = Optional.empty();
	private Stage dialogStage;
	
	/**
	 * This method is called automatically after loading the FXML document.
	 */
	@FXML
	public void initialize() {

		// Get the categories from the DB
		categories = FXCollections.observableArrayList(categoryDao.list().stream()
				.map(Category::getDescription)
				.collect(Collectors.toList()));
		categoryDescription.setItems(categories);

		// Get budget amount and display when category selected
		categoryDescription.setOnAction(event -> {
			String description = categoryDescription.getSelectionModel().getSelectedItem();
			
			if (description == null) {
				return;
			}
			
			Optional<Category> category = categoryDao.readByDescription(description);
			
			if (!category.isPresent()) {
				LOG.warn("Category for description \"" + description + "\" not found" );
				return;
			}
			
			budget = budgetDao.readByCategory(category.get());
			
			if (!budget.isPresent()) { 
				budget = budgetDao.create(new Budget(category.get()));
			}
			
			budgetAmount.setText(budget.get().getBudgetAmountFormatted()); // TODO format doesn't work?
			budgetAmount.requestFocus();
		});
		
		// Restrict budget amount text field to doubles
		final TextFormatter<Double> numericFormat = new TextFormatter<>( new DoubleStringConverter() );
		budgetAmount.setTextFormatter(numericFormat);
		
		// Update budget record when amount looses focus
		budgetAmount.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue) {
				updateBudgetRecord(); 
			}
		});
		
		// Update budget record when <return> is pressed
		budgetAmount.setOnAction(event -> {
			categoryDescription.requestFocus();
		});
	
	}
	
	// Actions tied to FXML controls

	@FXML
	private void handleOkButtonAction(ActionEvent event) {
		dialogStage.close();
	}
	
	@FXML
	private void handleDeleteButtonAction(ActionEvent event) {
		if (budget.isPresent()) {
			if ( !budgetDao.delete(budget.get().getId()) ) {
				LOG.warn("Delete failed " + budget.get().toString());
			} 
		}
		
		budgetAmount.clear();
		categoryDescription.getSelectionModel().clearSelection();
		categoryDescription.requestFocus();
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	private void updateBudgetRecord() {
		if (budget.isPresent()) {
			budget.get().setBudgetAmount(Double.parseDouble(budgetAmount.getText()));
			if (!budgetDao.update(budget.get())) {
				LOG.warn("Could not update " + budget.get().toString());
			} else {
				LOG.info("Record updated " + budget.get().toString());
			}
		}
	}
}
