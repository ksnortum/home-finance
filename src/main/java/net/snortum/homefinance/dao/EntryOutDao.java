package net.snortum.homefinance.dao;

import java.sql.Connection;

import net.snortum.homefinance.model.EntryType;

public class EntryOutDao extends AbstractEntryDao {

	public EntryOutDao() {
		super(EntryType.OUT);
	}

	public EntryOutDao(Connection connection) {
		super(EntryType.OUT, connection);
	}

}
