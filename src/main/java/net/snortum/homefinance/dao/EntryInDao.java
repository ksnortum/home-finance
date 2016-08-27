package net.snortum.homefinance.dao;

import java.sql.Connection;

import net.snortum.homefinance.model.EntryType;

public class EntryInDao extends AbstractEntryDao {

	public EntryInDao() {
		super(EntryType.IN);
	}

	public EntryInDao(Connection connection) {
		super(EntryType.IN, connection);
	}

}
