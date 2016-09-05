package net.snortum.homefinance.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import net.snortum.homefinance.dao.BudgetDao;
import net.snortum.homefinance.dao.CategoryDao;
import net.snortum.homefinance.dao.EntryInDao;
import net.snortum.homefinance.model.Budget;
import net.snortum.homefinance.model.Category;
import net.snortum.homefinance.model.Entry;
import net.snortum.homefinance.model.EntryIn;

/**
 * Initialize a database for its first use and add some suggested categories.
 * 
 * @author Knute Snortum
 * @version 2016-08-29
 */
public class DbInitialization {
	private static final Logger LOG = Logger.getLogger(DbInitialization.class);
	
	/**
	 * Create tables using the default database connection.
	 * 
	 * @return true if database and tables were created, otherwise false.
	 */
	public static boolean createTables() {
		Optional<Connection> result = DbConnection.getConnection();
		if ( ! result.isPresent() ) {
			return false;
		}
		Connection con = result.get();
		return executeSqlStatements(con);
	}
	
	/**
	 * There needs to be at least one {@link Category} on file, so populate
	 * the table with some common categories.  And since {@link Budget}s are
	 * tied to Categories, create a zero budget record.
	 */
	public static void populateTables() {
		CategoryDao categoyDao = new CategoryDao();
		BudgetDao budgetDao = new BudgetDao();
		List<String> descriptions = Arrays.asList(
				"Miscellaneous",
				"Housing",
				"Utilities",
				"Food",
				"Automotive",
				"Medical",
				"Entertainment");
		
		for (String description : descriptions) {
			Optional<Category> category = categoyDao.create(new Category(description));
			
			if (category.isPresent()) {
				budgetDao.create(new Budget(category.get()));
			} else {
				LOG.warn("Could not create category \"" + description +"\"");
			}
		}
	}
	
	/**
	 * Create tables using a passed-in database connection.
	 * 
	 * @param con The database connection.
	 * @return true if tables were created, otherwise false.
	 */
	public static boolean executeSqlStatements(Connection con) {
		try {
			con.setAutoCommit(false);
			Statement stmnt = con.createStatement();
    		stmnt.executeUpdate(Category.SQL_TO_CREATE);
    		stmnt.executeUpdate(Budget.SQL_TO_CREATE);
    		stmnt.executeUpdate(Entry.SQL_TO_CREATE);
    		con.commit();
		} catch (SQLException e) {
			LOG.error("Error creating tables", e);
			
			try {
				con.rollback();
			} catch (SQLException e1) {
				LOG.error("Error trying to rollback", e1);
			}
			
			return false;
		}
		
		return true;
	}
	
	public static void addDepositsForTesting() {
		EntryInDao dao = new EntryInDao();
		Entry deposit = new EntryIn.Builder()
				.date(LocalDate.of(2016, 8, 1))
				.amount(BigDecimal.valueOf(2134.67))
				.build();
		dao.create(deposit);
		deposit = new EntryIn.Builder()
				.date(LocalDate.of(2016, 8, 15))
				.amount(BigDecimal.valueOf(2145.83))
				.build();
		dao.create(deposit);
		deposit = new EntryIn.Builder()
				.date(LocalDate.of(2016, 9, 1))
				.amount(BigDecimal.valueOf(2135.09))
				.build();
		dao.create(deposit);
	}
}
