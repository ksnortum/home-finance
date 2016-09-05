package net.snortum.homefinance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import net.snortum.homefinance.model.Budget;
import net.snortum.homefinance.model.Category;
import net.snortum.homefinance.util.DbConnection;

public class BudgetDao implements GenericDao<Budget, Integer> {
	
	private static final Logger LOG = Logger.getLogger(BudgetDao.class);

	private static final String INSERT_SQL = 
			"INSERT INTO budget ("
			+ "category_id, " // 1
			+ "budget_amt, "  // 2
			+ "actual_amt "   // 3
			+ ") VALUES (?, ?, ?)";
	
	private static final String SELECT_SQL = 
			"SELECT "
			+ "category_id, "
			+ "budget_amt, "
			+ "actual_amt "
			+ "FROM budget "
			+ "WHERE id = ?";
	
	private static final String SELECT_BY_CATEGORY_SQL = 
			"SELECT "
			+ "id, "
			+ "budget_amt, "
			+ "actual_amt "
			+ "FROM budget "
			+ "WHERE category_id = ?";
	
	private static final String UPDATE_SQL =
			"UPDATE budget "
			+ "SET category_id = ?, "
			+ "budget_amt = ?, "
			+ "actual_amt = ? "
			+ "WHERE id = ?";
	
	private static final String DELETE_SQL =
			"DELETE FROM budget "
			+ "WHERE id = ?";
	
	private static final String LIST_SQL =
			"SELECT "
			+ "id, "          // 1
			+ "category_id, " // 2
			+ "budget_amt, "  // 3
			+ "actual_amt "   // 4
			+ "FROM budget ";

	
	private final Optional<Connection> connection;

	/**
	 * Create an BudgetDao with the default connection.
	 * 
	 * @see DbConnection
	 */
	public BudgetDao() {
		connection = DbConnection.getConnection();
	}
	
	/**
	 * Create an BudgetDao with the passed-in connection.
	 * 
	 * @param con the connection object to use
	 */
	public BudgetDao(Connection con) {
		connection = Optional.of(con);
	}

	@Override
	public Optional<Budget> create(Budget record) {
		if (!connection.isPresent()) {
			return Optional.empty();
		}

		Connection con = connection.get();
		try {
			con.setAutoCommit(false);
			PreparedStatement stmnt = con.prepareStatement(INSERT_SQL);
			stmnt.setInt(1, record.getCategoryId());
			stmnt.setDouble(2, record.getBudgetAmount());
			stmnt.setDouble(3, record.getActualAmount());
			stmnt.executeUpdate();
			int id = stmnt.getGeneratedKeys().getInt(1);
			record.setId(id);
			con.commit();
		} catch (SQLException e) {
			LOG.error("Error inserting a Budget record", e);

			try {
				con.rollback();
			} catch (SQLException e1) {
				LOG.error(e1.getMessage());
			}
		}

		return Optional.of(record);
	}

	@Override
	public Optional<Budget> read(Integer key) {
		if (!connection.isPresent()) {
			return Optional.empty();
		}

		Connection con = connection.get();
		int categoryId;
		double budgetAmt;
		double actualAmt;
		
		try {
			PreparedStatement stmnt = con.prepareStatement(SELECT_SQL);
			stmnt.setInt(1, key);
			ResultSet rs = stmnt.executeQuery();
			if (!rs.next()) {
				LOG.warn("Budget record, ID = " + key + ": ResultSet is empty");
				return Optional.empty();
			}
			categoryId = rs.getInt(1);
			budgetAmt = rs.getDouble(2);
			actualAmt = rs.getDouble(3);
		} catch (SQLException e) {
			LOG.error("Error reading Budget record", e);
			return Optional.empty();
		}
		
		CategoryDao categoryDao = new CategoryDao(con);
		Optional<Category> categoryOpt = categoryDao.read(categoryId);
		if (!categoryOpt.isPresent()) {
			LOG.error("Budget record error: Category could not be found");
			return Optional.empty();
		}
		Budget record = new Budget(key, categoryOpt.get(), budgetAmt, actualAmt);
			
		return Optional.of(record);
	}

	public Optional<Budget> readByCategory(Category category) {
		if (!connection.isPresent()) {
			return Optional.empty();
		}

		Connection con = connection.get();
		int id;
		double budgetAmt;
		double actualAmt;
		
		try {
			PreparedStatement stmnt = con.prepareStatement(SELECT_BY_CATEGORY_SQL);
			stmnt.setInt(1, category.getId());
			ResultSet rs = stmnt.executeQuery();
			if (!rs.next()) {
				LOG.info("Budget record, CATEGORY_ID = " + category.getId() + ": ResultSet is empty");
				return Optional.empty();
			}
			id = rs.getInt(1);
			budgetAmt = rs.getDouble(2);
			actualAmt = rs.getDouble(3);
		} catch (SQLException e) {
			LOG.error("Error reading Budget record", e);
			return Optional.empty();
		}
		
		Budget record = new Budget(id, category, budgetAmt, actualAmt);
			
		return Optional.of(record);
	}
	
	@Override
	public boolean update(Budget record) {
		if (!connection.isPresent()) {
			return false;
		}
		
		Connection con = connection.get();
		int recordsAffected = 0;
		
		try {
			con.setAutoCommit(false);
			PreparedStatement stmnt = con.prepareStatement(UPDATE_SQL);
			stmnt.setInt(1, record.getCategoryId());
			stmnt.setDouble(2, record.getBudgetAmount());
			stmnt.setDouble(3, record.getActualAmount());
			stmnt.setInt(4, record.getId());
			recordsAffected = stmnt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			LOG.error("Error updating Budget record", e);
			return false;
		}
		
		return recordsAffected == 1;
	}

	@Override
	public boolean delete(Integer key) {
		if (!connection.isPresent()) {
			return false;
		}
		
		Connection con = connection.get();
		int recordsAffected = 0;
		
		try {
			con.setAutoCommit(false);
			PreparedStatement stmnt = con.prepareStatement(DELETE_SQL);
			stmnt.setInt(1, key);
			recordsAffected = stmnt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			LOG.error("Error deleting Budget record", e);
			return false;
		}
		
		return recordsAffected == 1;
	}
	
	@Override
	public List<Budget> list() {
		List<Budget> list = new ArrayList<>();
		
		if (!connection.isPresent()) {
			return list;
		}
		
		Connection con = connection.get();
		ResultSet rs;
		
		try {
			Statement stmnt = con.createStatement();
			rs = stmnt.executeQuery(LIST_SQL);
			
			while (rs.next()) {
				int key = rs.getInt(1);
				int categoryId = rs.getInt(2);
				double budgetAmt = rs.getDouble(3);
				double actualAmt = rs.getDouble(4);
				CategoryDao categoryDao = new CategoryDao(con);
				Optional<Category> categoryOpt = categoryDao.read(categoryId);
				if (!categoryOpt.isPresent()) {
					LOG.error("Budget record error: Category could not be found");
					return list;
				}
				list.add(new Budget(key, categoryOpt.get(), budgetAmt, actualAmt));
			}
		} catch (SQLException e) {
			LOG.error("Error getting Budget list", e);
			return list;
		}
		
		return list;
	}
}
