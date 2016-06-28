package net.snortum.homefinance.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import net.snortum.homefinance.dao.BudgetDao;
import net.snortum.homefinance.dao.CategoryDao;
import net.snortum.homefinance.model.Budget;
import net.snortum.homefinance.model.BudgetAmountEditingCell;
import net.snortum.homefinance.model.Category;
import net.snortum.homefinance.model.NumberEditingCell;

public class BudgetMaintenanceFirstPass {

	// Fields tied to the FXML
	
	@FXML
	private TableView<Budget> budgetListTable;
	
	@FXML
	private TableColumn<Budget, Integer> idCol;
	
	@FXML
	private TableColumn<Budget, String> categoryCol;
	
	@FXML
	private TableColumn<Budget, Double> budgetAmtCol;
	
	@FXML
	private Button okBtn;
	
	// Other fields
	private ObservableList<Budget> data = FXCollections.observableArrayList();
	private BudgetDao dao = new BudgetDao();
	private Alert deleteConfirm;
	private Alert nothingSelected;
	
	/**
	 * This method will be called automatically after loading the FXML document
	 */
	@FXML
	public void initialize() {
		
		// Get the data from the DB
		List<Budget> list = dao.list();
		
		// Add to table
		data.addAll(list);
		budgetListTable.setItems(data);
		
		// Connect cell values to Budget fields
		idCol.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
		categoryCol.setCellValueFactory(cell -> cell.getValue().categoryDescProperty());
		budgetAmtCol.setCellValueFactory(cell -> cell.getValue().budgetAmountProperty().asObject());
		
		// Make cell editable on double-click
		budgetAmtCol.setCellFactory(col -> new BudgetAmountEditingCell());
		
		// Editing budget amount
		budgetAmtCol.setOnEditCommit(
			new EventHandler<CellEditEvent<Budget, Double>>() {

				@Override
				public void handle(CellEditEvent<Budget, Double> event) {
					double oldValue = event.getOldValue();
					double newValue = event.getNewValue();
					
					if (newValue != oldValue) {
						Budget budget = event.getTableView()
								.getItems()
								.get(event.getTablePosition().getRow());
						budget.setBudgetAmount(newValue);
						dao.update(budget);
					}
				}
			}
		);
		
		// Setup delete alert dialog
		deleteConfirm = new Alert(AlertType.CONFIRMATION);
		deleteConfirm.setTitle("Delete Confirmation");
		deleteConfirm.setContentText("Are you sure you want to delete this budget?");
		
		// Setup "nothing selected" alert
		nothingSelected = new Alert(AlertType.INFORMATION);
		nothingSelected.setTitle("Nothing Selected");
		nothingSelected.setHeaderText("No row was selected in the grid");
		nothingSelected.setContentText("Please select a row first");
	}
	
	// Actions tied to FXML controls
	
    @FXML
    private void handleOkButtonAction(ActionEvent event) {
    	System.out.println("OK button was pressed"); // DEBUG
    }
    
    /*
     * Delete and creating are tricky.  Budgets are tied to categories, you can't 
     * make a new one w/o a new category.  Does deleting just mean there is no
     * budget?
     */
    
    @FXML
    private void handleDeleteButtonAction(ActionEvent event) {
    	
    	// Get Category from current line
    	Budget budget = budgetListTable.getSelectionModel().getSelectedItem();
    	
    	if (budget == null) {
    		nothingSelected.showAndWait();
    		return;
    	}
    	
		deleteConfirm.setHeaderText(budget.getCategoryDesc());

    	// Alert, Are you sure?
    	Optional<ButtonType> pressed = deleteConfirm.showAndWait();
    	if (pressed.isPresent() && pressed.get() == ButtonType.OK) {
    	    if ( dao.delete(budget.getId()) ) {
    	    	data.remove(budget);
    	    }
    	}
    }
    
//    @FXML
//    private void handleAddButtonAction(ActionEvent event) {
//    	String newDesc = descriptionAdd.getText();
//    	descriptionAdd.clear();
//    	
//    	// Nothing to add
//    	if ( newDesc == null || newDesc.trim().isEmpty() ) {
//    		return;
//    	}
//    	
//    	Category category = new Category(newDesc);
//    	int id = dao.create(category);
//    	
//    	// Error creating category
//    	if (id == -1) {
//    		return;
//    	}
//    	
//    	Category newCat = new Category(id, category);
//    	dao.update(newCat);
//    	data.add(newCat);
//    }
}
