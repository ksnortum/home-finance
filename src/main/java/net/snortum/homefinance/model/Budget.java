package net.snortum.homefinance.model;

import java.text.DecimalFormat;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Budget is used to build budget versus actual reports.  Budgets are grouped by category.
 * 
 * @author Knute Snortum
 * @version 2016-06-09
 */
public class Budget implements ValueSettable<Double>{
	
	/** SQL statement to create table */
	public static final String SQL_TO_CREATE =
			"CREATE TABLE budget ("
    		+ "id          INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ "category_id INTEGER NOT NULL,"
    		+ "budget_amt  REAL    CHECK(budget_amt >= 0),"
    		+ "actual_amt  REAL," // TODO should this only be calculated?
    		+ "FOREIGN KEY (category_id) REFERENCES category(id)"
    		+ ")";
	
	private static final String AMOUNT_FORMAT = "###,##0.00";
	
	// Fields
	private IntegerProperty id;
	private ObjectProperty<Category> category;
	private StringProperty categoryDescription;
	private DoubleProperty budgetAmt;
	private DoubleProperty actualAmt;
	private DecimalFormat format = new DecimalFormat(AMOUNT_FORMAT);
	
	/**
	 * Creates a Budget object with all information.
	 * 
	 * @param id The ID of this object.  May be zero initially. 
	 * @param category The {@link Category} of this budget.
	 * @param budgetAmt The budget amount, cannot be negative.
	 * @param actualAmt The actual amount from the {@link Entry} objects.
	 */
	public Budget(int id, Category category, double budgetAmt, double actualAmt) {
		if (budgetAmt < 0) {
			throw new IllegalArgumentException("Budget amount cannot be negative");
		}
		if (category == null) {
			throw new IllegalArgumentException("Category cannot be null");
		}
		if (id < 0) {
			throw new IllegalArgumentException("ID cannot be negative");
		}
		this.id = new SimpleIntegerProperty(id);
		this.category = new SimpleObjectProperty<Category>(category);
		this.categoryDescription = new SimpleStringProperty(category.getDescription());
		this.budgetAmt = new SimpleDoubleProperty(budgetAmt);
		this.actualAmt = new SimpleDoubleProperty(actualAmt);
	}
	
	/**
	 * Create a Budget Object with actual amount set to zero.
	 * 
	 * @param id The ID of this object.  May be zero initially.
	 * @param category The {@link Category} of this budget.
	 * @param budgetAmt The budget amount, cannot be negative.
	 */
	public Budget(int id, Category category, int budgetAmt) {
		this(id, category, budgetAmt, 0);
	}
	
	/**
	 * Create a Budget Object with only the {@link Category}.
	 * 
	 * @param category The Category of this budget.
	 */
	public Budget(Category category) {
		this(0, category, 0, 0);
	}
	
	/**
	 * Create a Budget object from an ID and an existing Budget
	 * 
	 * @param id The new ID.  Should not be zero.
	 * @param budget The existing budget object.
	 */
	public Budget(int id, Budget budget) {
		this(id, budget.getCategory(), budget.getBudgetAmount(), budget.getActualAmount());
	}
	
	// Getters and Setters
	
	public int getId() {
		return id.get();
	}
	
	public void setId(int id) {
		this.id.set(id);
	}
	
	public IntegerProperty idProperty() {
		return id;
	}
	
	public Category getCategory() {
		return category.get();
	}
	
	/**
	 * The category description is updated when category is set.
	 * 
	 * @param category The category to set
	 */
	public void setCategory(Category category) {
		this.category = new SimpleObjectProperty<Category>(category);
		this.categoryDescription = new SimpleStringProperty(category.getDescription());
	}
	
	public ObjectProperty<Category> categoryProperty() {
		return category;
	}
	
	public int getCategoryId() {
		return getCategory().getId();
	}
	
	public String getCategoryDesc() {
		return categoryDescription.get();
	}
	
	public StringProperty categoryDescProperty() {
		return categoryDescription;
	}
	
	public double getBudgetAmount() {
		return budgetAmt.get();
	}
	
	public String getBudgetAmountFormatted() {
		return format.format(getBudgetAmount());
	}
	
	public void setBudgetAmount(double amount) {
		budgetAmt.set(amount);
	}
	
	public DoubleProperty budgetAmountProperty() {
		return budgetAmt;
	}
	
	public double getActualAmount() {
		return actualAmt.get();
	}
	
	public void addToActual(double amount) {
		actualAmt.set(getActualAmount() + amount);
	}

	@Override
	public void setValue(Double value) {
		setBudgetAmount(value);
	}
	
	@Override
	public String toString() {
		return "Budget for: " + getCategoryDesc();
	}
}
