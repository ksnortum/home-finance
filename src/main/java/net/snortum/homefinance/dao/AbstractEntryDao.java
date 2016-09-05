package net.snortum.homefinance.dao;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import net.snortum.homefinance.model.Category;
import net.snortum.homefinance.model.Entry;
import net.snortum.homefinance.model.EntryBalance;
import net.snortum.homefinance.model.EntryIn;
import net.snortum.homefinance.model.EntryOut;
import net.snortum.homefinance.model.EntryType;
import net.snortum.homefinance.util.DbConnection;

public class AbstractEntryDao implements GenericDao<Entry, Integer> {

	private static final Logger LOG = Logger.getLogger(AbstractEntryDao.class);
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	private static final String INSERT_SQL = 
			"INSERT INTO entry ("
			+ "description, " // 1
			+ "type, "        // 2
			+ "recurring, "   // 3
			+ "amount, "      // 4
			+ "comment, "     // 5
			+ "url, "         // 6
			+ "paid, "        // 7
			+ "date, "        // 8
			+ "reconciled, "  // 9
			+ "category_id"   // 10
			+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SELECT_SQL = 
			"SELECT "
			+ "id, "          
			+ "description, " 
			+ "type, "        
			+ "recurring, "   
			+ "amount, "      
			+ "comment, "     
			+ "url, "         
			+ "paid, "        
			+ "date, "        
			+ "reconciled, "  
			+ "category_id"   
			+ "FROM entry "
			+ "WHERE id = ?";
	
	private static final String UPDATE_SQL =
			"UPDATE entry "
			+ "SET "
			+ "description = ?, " // 1
			+ "type = ?,"         // 2
			+ "recurring = ?,"    // 3
			+ "amount = ?,"       // 4
			+ "comment = ?,"      // 5
			+ "url = ?,"          // 6
			+ "paid = ?,"         // 7
			+ "date = ?,"         // 8
			+ "reconciled = ?,"   // 9
			+ "category_id = ? "  // 10
			+ "WHERE id = ?";     // 11
	
	private static final String DELETE_SQL =
			"DELETE FROM entry "
			+ "WHERE id = ?";
	
	private static final String LIST_SQL =
			"SELECT "
			+ "id, "          
			+ "description, " 
			+ "type, "        
			+ "recurring, "   
			+ "amount, "      
			+ "comment, "     
			+ "url, "         
			+ "paid, "        
			+ "date, "        
			+ "reconciled, "  
			+ "category_id "  
			+ "FROM entry ";
	
	private static final String ID_COLUMN = "id";
	private static final int ID_PARAM = 11;
	private static final String DESCRIPTION_COLUMN = "description";
	private static final int DESCRIPTION_PARAM = 1;
	private static final String TYPE_COLUMN = "type";
	private static final int TYPE_PARAM = 2;
	private static final String RECURRING_COLUMN = "recurring";
	private static final int RECURRING_PARAM = 3;
	private static final String AMOUNT_COLUMN = "amount";
	private static final int AMOUNT_PARAM = 4;
	private static final String COMMENT_COLUMN = "comment";
	private static final int COMMENT_PARAM = 5;
	private static final String URL_COLUMN = "url";
	private static final int URL_PARAM = 6;
	private static final String PAID_COLUMN = "paid";
	private static final int PAID_PARAM = 7;
	private static final String DATE_COLUMN = "date";
	private static final int DATE_PARAM = 8;
	private static final String RECONCILED_COLUMN = "reconciled";
	private static final int RECONCILED_PARAM = 9;
	private static final String CATEGORY_ID_COLUMN = "category_id";
	private static final int CATEGORY_ID_PARAM = 10;
	private static final int DELETE_ID_PARAM = 1;

	private final EntryType entryType;
	private final Optional<Connection> connection;

	/**
	 * Create an AbstractEntryDao object
	 * 
	 * @param entryType
	 *            The {@link EntryType} for this DAO session
	 */
	protected AbstractEntryDao(EntryType entryType) {
		this.entryType = entryType;
		connection = DbConnection.getConnection();
	}

	/**
	 * Create an AbstractEntryDao object
	 * 
	 * @param entryType
	 *            The {@link EntryType} for this DAO session
	 * @param connection
	 *            The {@link Connection} to use
	 */
	protected AbstractEntryDao(EntryType entryType, Connection connection) {
		this.entryType = entryType;
		this.connection = Optional.of(connection);
	}

	/**
	 * Create a new {@link Entry} row in the database
	 * 
	 * @param record
	 *            The Entry record to insert into the database
	 * @return An {@link Optional} of the Entry record with the new ID
	 */
	@Override
	public Optional<Entry> create(Entry record) {
		if (!connection.isPresent()) {
			return Optional.empty();
		}

		Connection con = connection.get();
		try {
			con.setAutoCommit(false);
			PreparedStatement stmnt = con.prepareStatement(INSERT_SQL);
			stmnt.setString(DESCRIPTION_PARAM, record.getDescription());
			stmnt.setString(TYPE_PARAM, entryType.getDesc());
			stmnt.setBoolean(RECURRING_PARAM, record.isRecurring());
			stmnt.setBigDecimal(AMOUNT_PARAM, record.getAmount());
			stmnt.setString(COMMENT_PARAM, record.getComment());
			stmnt.setString(URL_PARAM, record.getUrl().isPresent()
					? record.getUrl().get().toString()
					: "");
			stmnt.setBoolean(PAID_PARAM, record.isPaid());
			stmnt.setDate(DATE_PARAM, Date.valueOf(record.getDate()));
			stmnt.setBoolean(RECONCILED_PARAM, record.isRecurring());
			stmnt.setInt(CATEGORY_ID_PARAM, record.getCategory().isPresent()
					? record.getCategory().get().getId()
					: -1);
			stmnt.executeUpdate();
			
 			// Get auto-generated ID and set it into the record
			ResultSet rs = stmnt.getGeneratedKeys();
			if (rs.next()) {
				record.setId(rs.getInt(1));
			} else {
				LOG.error("Could not retrieve the generated key");
			}
			
			con.commit();
		} catch (SQLException e) {
			LOG.error("Error inserting a Entry record", e);

			try {
				con.rollback();
			} catch (SQLException e1) {
				LOG.error(e1.getMessage());
			}
		}

		return Optional.of(record);
	}

	@Override
	public Optional<Entry> read(Integer key) {
		if (!connection.isPresent()) {
			return Optional.empty();
		}

		Connection con = connection.get();
		String description;
		EntryType type = EntryType.IN;
		boolean recurring;
		BigDecimal amount;
		String comment;
		URL url;
		boolean paid;
		LocalDate date;
		boolean reconciled;
		int categoryId;

		try {
			PreparedStatement stmnt = con.prepareStatement(SELECT_SQL);
			stmnt.setInt(ID_PARAM, key);
			ResultSet rs = stmnt.executeQuery();
			
			if (!rs.next()) {
				LOG.warn("Entry record, ID = " + key + ": ResultSet is empty");
				return Optional.empty();
			}
			
			description = rs.getString(DESCRIPTION_COLUMN);
			type = EntryType.valueOf(rs.getString(TYPE_COLUMN).toUpperCase());
			recurring = rs.getBoolean(RECURRING_COLUMN);
			amount = BigDecimal.valueOf(rs.getDouble(AMOUNT_COLUMN));
			comment = rs.getString(COMMENT_COLUMN);
			url = getUrl(rs.getString(URL_COLUMN));
			paid = rs.getBoolean(PAID_COLUMN);
			String formattedDate = FORMATTER.format(rs.getDate(DATE_COLUMN));
			date = LocalDate.parse(formattedDate);
			reconciled = rs.getBoolean(RECONCILED_COLUMN);
			categoryId = rs.getInt(CATEGORY_ID_COLUMN);
		} catch (SQLException e) {
			LOG.error("Error reading Entry record", e);
			return Optional.empty();
		}

		if (type != entryType) {
			LOG.error("Entry types don't match, " + type + " != " + entryType);
			return Optional.empty();
		}
		
		CategoryDao categoryDao = new CategoryDao(con);
		Optional<Category> category = categoryDao.read(Integer.valueOf(categoryId));
		
		Entry record = null;
		switch(type) {
		case IN:
			record = new EntryIn.Builder()
					.description(description)
					.recurring(recurring)
					.amount(amount)
					.comment(comment)
					.url(url == null ? Optional.empty() : Optional.of(url))
					.paid(paid)
					.date(date)
					.reconciled(reconciled)
					.category(category)
					.build();
			break;
		case OUT:
			record = new EntryOut.Builder() 
					.description(description)
					.recurring(recurring)
					.amount(amount)
					.comment(comment)
					.url(url == null ? Optional.empty() : Optional.of(url))
					.paid(paid)
					.date(date)
					.reconciled(reconciled)
					.category(category)
					.build();
			break;
		case BAL:
			record = new EntryBalance.Builder() 
					.description(description)
					.recurring(recurring)
					.amount(amount)
					.comment(comment)
					.url(url == null ? Optional.empty() : Optional.of(url))
					.paid(paid)
					.date(date)
					.reconciled(reconciled)
					.category(category)
					.build();
			break;
		default:
			LOG.error("Type (" + type + ") unknown");
		}

		return record == null ? Optional.empty() : Optional.of(record);
	}

	@Override
	public boolean update(Entry record) {
		if (!connection.isPresent()) {
			return false;
		}
		
		if (record.getType() != entryType) {
			LOG.error("Entry types don't match, " + record.getType() + " != " + entryType);
			return false;
		}

		Connection con = connection.get();
		int recordsAffected = 0;

		try {
			con.setAutoCommit(false);
			PreparedStatement stmnt = con.prepareStatement(UPDATE_SQL);
			stmnt.setString(DESCRIPTION_PARAM, record.getDescription());
			stmnt.setString(TYPE_PARAM, entryType.getDesc());
			stmnt.setBoolean(RECURRING_PARAM, record.isRecurring());
			stmnt.setBigDecimal(AMOUNT_PARAM, record.getAmount());
			stmnt.setString(COMMENT_PARAM, record.getComment());
			stmnt.setString(URL_PARAM, record.getUrl().isPresent()
					? record.getUrl().get().toString()
					: "");
			stmnt.setBoolean(PAID_PARAM, record.isPaid());
			stmnt.setDate(DATE_PARAM, Date.valueOf(record.getDate()));
			stmnt.setBoolean(RECONCILED_PARAM, record.isRecurring());
			stmnt.setInt(CATEGORY_ID_PARAM, record.getCategory().isPresent()
					? record.getCategory().get().getId()
					: -1);
			stmnt.setInt(ID_PARAM, record.getId());
			recordsAffected = stmnt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			LOG.error("Error updating Entry record", e);
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
			stmnt.setInt(DELETE_ID_PARAM, key);
			recordsAffected = stmnt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			LOG.error("Error deleting Entry record", e);
			return false;
		}
		
		return recordsAffected == 1;
	}

	@Override
	public List<Entry> list() {
		List<Entry> list = new ArrayList<>();
		
		if (!connection.isPresent()) {
			return list;
		}
		
		Connection con = connection.get();
		ResultSet rs;
		
		try {
			Statement stmnt = con.createStatement();
			rs = stmnt.executeQuery(LIST_SQL);
			
			while (rs.next()) {
				int key = rs.getInt(ID_COLUMN);
				String description = rs.getString(DESCRIPTION_COLUMN);
				String entryTypeValue = rs.getString(TYPE_COLUMN);
				EntryType type = EntryType.valueOf(entryTypeValue.toUpperCase());
				boolean recurring = rs.getBoolean(RECURRING_COLUMN);
				BigDecimal amount = BigDecimal.valueOf(rs.getDouble(AMOUNT_COLUMN));
				String comment = rs.getString(COMMENT_COLUMN);
				URL url = getUrl(rs.getString(URL_COLUMN));
				boolean paid = rs.getBoolean(PAID_COLUMN);
				String formattedDate = FORMATTER.format(rs.getDate(DATE_COLUMN));
				LocalDate date = LocalDate.parse(formattedDate);
				boolean reconciled = rs.getBoolean(RECONCILED_COLUMN);
				int categoryId = rs.getInt(CATEGORY_ID_COLUMN);
				CategoryDao categoryDao = new CategoryDao(con);
				Optional<Category> category = categoryDao.read(categoryId);
				
				Entry record = null;
				switch(type) {
				case IN:
					record = new EntryIn.Builder()
							.id(key)
							.description(description)
							.recurring(recurring)
							.amount(amount)
							.comment(comment)
							.url(url == null ? Optional.empty() : Optional.of(url))
							.paid(paid)
							.date(date)
							.reconciled(reconciled)
							.category(category)
							.build();
					break;
				case OUT:
					record = new EntryOut.Builder() 
							.id(key)
							.description(description)
							.recurring(recurring)
							.amount(amount)
							.comment(comment)
							.url(url == null ? Optional.empty() : Optional.of(url))
							.paid(paid)
							.date(date)
							.reconciled(reconciled)
							.category(category)
							.build();
					break;
				case BAL:
					record = new EntryBalance.Builder()
							.id(key)
							.description(description)
							.recurring(recurring)
							.amount(amount)
							.comment(comment)
							.url(url == null ? Optional.empty() : Optional.of(url))
							.paid(paid)
							.date(date)
							.reconciled(reconciled)
							.category(category)
							.build();
					break;
				default:
					LOG.error("Type (" + type + ") unknown");
				}
				
				list.add(record);
			} // End while next rs
		} catch (SQLException e) {
			LOG.error("Error getting Entry list", e);
			return list;
		}
		
		return list;
	}

	private URL getUrl(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}

}
