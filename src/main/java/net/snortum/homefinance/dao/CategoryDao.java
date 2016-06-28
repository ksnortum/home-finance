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

import net.snortum.homefinance.model.Category;
import net.snortum.homefinance.util.DbConnection;

public class CategoryDao implements GenericDao<Category, Integer> {
	
	private static final Logger LOG = Logger.getLogger(CategoryDao.class);

	private static final String INSERT_SQL = 
			"INSERT INTO category ("
			+ "description " 
			+ ") VALUES (?)";
	
	private static final String SELECT_SQL = 
			"SELECT "
			+ "description "
			+ "FROM category "
			+ "WHERE id = ?";
	
	private static final String SELECT_BY_DESC_SQL = 
			"SELECT "
			+ "id "
			+ "FROM category "
			+ "WHERE description = ?";
	
	private static final String UPDATE_SQL =
			"UPDATE category "
			+ "SET description = ? "
			+ "WHERE id = ?";
	
	private static final String DELETE_SQL =
			"DELETE FROM category "
			+ "WHERE id = ?";
	
	private static final String LIST_SQL =
			"SELECT id, "     // 1
			+ "description "  // 2
			+ "FROM category;";
	
	private final Optional<Connection> connection;

	/**
	 * Create an CategoryDao with the default connection.
	 * 
	 * @see DbConnection
	 */
	public CategoryDao() {
		connection = DbConnection.getConnection();
	}
	
	/**
	 * Create an CategoryDao with the passed-in connection.
	 * 
	 * @param con the connection object to use
	 */
	public CategoryDao(Connection con) {
		connection = Optional.of(con);
	}

	@Override
	public Optional<Category> create(Category record) {
		if (!connection.isPresent()) {
			return Optional.empty();
		}

		Connection con = connection.get();
		try {
			con.setAutoCommit(false);
			PreparedStatement stmnt = con.prepareStatement(INSERT_SQL);
			stmnt.setString(1, record.getDescription());
			stmnt.executeUpdate();
			int id = stmnt.getGeneratedKeys().getInt(1);
			record.setId(id);
			con.commit();
		} catch (SQLException e) {
			
			// A duplicate does not log an error
			if ( !e.getMessage().contains("UNIQUE constraint failed") ) {
				LOG.error("Error inserting a Category record", e);
			}

			try {
				con.rollback();
			} catch (SQLException e1) {
				LOG.error(e1.getMessage());
			}
		}

		return Optional.of(record);
	}

	@Override
	public Optional<Category> read(Integer key) {
		if (!connection.isPresent()) {
			return Optional.empty();
		}

		Connection con = connection.get();
		String desc;
		
		try {
			PreparedStatement stmnt = con.prepareStatement(SELECT_SQL);
			stmnt.setInt(1, key);
			ResultSet rs = stmnt.executeQuery();
			if (!rs.next()) {
				LOG.warn("Category record, ID = " + key + ": ResultSet is empty");
				return Optional.empty();
			}
			desc = rs.getString(1);
		} catch (SQLException e) {
			LOG.error("Error reading Category record", e);
			return Optional.empty();
		}
		
		Category record = new Category(key, desc);
			
		return Optional.of(record);
	}
	
	public Optional<Category> readByDescription(String desc) {
		if (!connection.isPresent()) {
			return Optional.empty();
		}

		Connection con = connection.get();
		int key;
		
		try {
			PreparedStatement stmnt = con.prepareStatement(SELECT_BY_DESC_SQL);
			stmnt.setString(1, desc);
			ResultSet rs = stmnt.executeQuery();
			if (!rs.next()) {
				LOG.warn("Category record, DESCRIPTION = " + desc + ": ResultSet is empty");
				return Optional.empty();
			}
			key = rs.getInt(1);
		} catch (SQLException e) {
			LOG.error("Error reading Category record", e);
			return Optional.empty();
		}
		
		Category record = new Category(key, desc);
			
		return Optional.of(record);
	}

	@Override
	public boolean update(Category record) {
		if (!connection.isPresent()) {
			return false;
		}
		
		Connection con = connection.get();
		int recordsAffected = 0;
		
		try {
			con.setAutoCommit(false);
			PreparedStatement stmnt = con.prepareStatement(UPDATE_SQL);
			stmnt.setString(1, record.getDescription());
			stmnt.setInt(2, record.getId());
			recordsAffected = stmnt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			LOG.error("Error updating Category record", e);
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
			LOG.error("Error deleting Category record", e);
			return false;
		}
		
		return recordsAffected == 1;
	}

	@Override
	public List<Category> list() {
		List<Category> list = new ArrayList<>();
		
		if (!connection.isPresent()) {
			return list;
		}
		
		Connection con = connection.get();
		
		try {
			Statement stmnt = con.createStatement();
			ResultSet rs = stmnt.executeQuery(LIST_SQL);
			
			while (rs.next()) {
				int id = rs.getInt(1);
				String description = rs.getString(2);
				list.add(new Category(id, description)); 
			}
		} catch (SQLException e) {
			LOG.error("Error retrieving Category list", e);
			return list;
		}
		
		return list;
	}
}
