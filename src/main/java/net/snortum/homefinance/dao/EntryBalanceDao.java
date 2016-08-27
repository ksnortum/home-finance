package net.snortum.homefinance.dao;

import java.sql.Connection;

import net.snortum.homefinance.model.EntryType;

public class EntryBalanceDao extends AbstractEntryDao {

	public EntryBalanceDao() {
		super(EntryType.BAL);
	}

	public EntryBalanceDao(Connection connection) {
		super(EntryType.BAL, connection);
	}

}
