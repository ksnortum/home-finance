package net.snortum.homefinance.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Get a connection to the SQLite database file
 * 
 * @author Knute Snortum
 * @version 2015-12-09
 */
public class DbConnection {
	private static final Logger LOG = LogManager.getLogger();
	
	/** SQLite database file name */
	public static final String DB = "finance.db";
	private static Connection con;
	
	/**
	 * Get a JDBC connection with the SQLite database.  Error log will
	 * contain error if connection cannot be made.
	 *  
	 * @return Optional containing connection if connected without error,
	 *         otherwise, Optional empty.
	 */
	public static Optional<Connection> getConnection() {
		if (con == null) {
    		try {
    			con = DriverManager.getConnection("jdbc:sqlite:" + DB);
    		} catch (SQLException e) {
    			LOG.error("Could not connect with " + DB, e);
    			return Optional.empty();
    		}
		}

		return Optional.of(con);
	}
}
