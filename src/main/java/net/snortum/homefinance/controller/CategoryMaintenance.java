package net.snortum.homefinance.controller;

import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import net.snortum.homefinance.dao.CategoryDao;
import net.snortum.homefinance.model.Category;

/**
 * Controller for Category Maintenance
 * 
 * @author Knute Snortum
 * @version 2016-06-09
 */
public class CategoryMaintenance {

	// Fields tied to the FXML

	@FXML
	private TableView<Category> categoryListTable;

	@FXML
	private TableColumn<Category, Integer> idCol;

	@FXML
	private TableColumn<Category, String> descriptionCol;

	@FXML
	private Button okBtn;

	@FXML
	private Button deleteBtn;

	@FXML
	private Button addBtn;

	@FXML
	private TextField descriptionAdd;

	// Other fields
	private ObservableList<Category> data = FXCollections.observableArrayList();
	private Alert deleteConfirm;
	private Alert nothingSelected;
	private CategoryDao dao = new CategoryDao();
	private Stage dialogStage;

	/**
	 * This method is called automatically after loading the FXML document.
	 */
	@FXML
	public void initialize() {

		// Get the data from the DB
		List<Category> list = dao.list();

		// Add to table
		data.addAll(list);
		categoryListTable.setItems(data);

		// Connect cell values to Category fields
		idCol.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
		descriptionCol.setCellValueFactory(cell -> cell.getValue().descriptionProperty());

		// Make cell editable on double-click
		descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());

		// Editing Description
		descriptionCol.setOnEditCommit(event -> {
			String oldValue = event.getOldValue();
			String newValue = event.getNewValue();

			if (!newValue.equals(oldValue)) {
				Category category = event.getTableView()
						.getItems()
						.get(event.getTablePosition().getRow());
				category.setDescription(newValue);
				dao.update(category);
			}
		});

		// Setup delete alert dialog
		deleteConfirm = new Alert(AlertType.CONFIRMATION);
		deleteConfirm.setTitle("Delete Confirmation");
		deleteConfirm.setContentText(
				"Are you sure you want to delete this category?");

		// Setup "nothing selected" alert
		nothingSelected = new Alert(AlertType.INFORMATION);
		nothingSelected.setTitle("Nothing Selected");
		nothingSelected.setHeaderText("No row was selected in the grid");
		nothingSelected.setContentText("Please select a row first");
	}

	// Actions tied to FXML controls

	@FXML
	private void handleOkButtonAction(ActionEvent event) {
		dialogStage.close();
	}

	@FXML
	private void handleDeleteButtonAction(ActionEvent event) {

		// Get Category from current line
		Category category = categoryListTable.getSelectionModel()
				.getSelectedItem();

		if (category == null) {
			nothingSelected.showAndWait();
			return;
		}

		deleteConfirm.setHeaderText(category.getDescription());

		// Alert, Are you sure?
		Optional<ButtonType> pressed = deleteConfirm.showAndWait();
		if (pressed.isPresent() && pressed.get() == ButtonType.OK) {
			if (dao.delete(category.getId())) {
				data.remove(category);
			}
		}
	}

	@FXML
	private void handleAddButtonAction(ActionEvent event) {
		String newDesc = descriptionAdd.getText();
		descriptionAdd.clear();

		// Nothing to add
		if (newDesc == null || newDesc.trim().isEmpty()) {
			return;
		}

		Optional<Category> category = dao.create(new Category(newDesc));

		// Error creating category
		if (!category.isPresent()) {
			return;
		}

		dao.update(category.get());
		data.add(category.get());
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
}
