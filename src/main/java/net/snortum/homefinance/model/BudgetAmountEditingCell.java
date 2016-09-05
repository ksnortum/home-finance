package net.snortum.homefinance.model;

public class BudgetAmountEditingCell extends NumberEditingCell<Budget, Double> {

	@Override
	public void commitEdit(Double value) {
		super.commitEdit(value);
		((Budget) this.getTableRow().getItem()).setBudgetAmount(value);
	}

}
