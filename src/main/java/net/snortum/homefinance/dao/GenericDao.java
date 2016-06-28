package net.snortum.homefinance.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GenericDao<T, PK extends Serializable> {
	Optional<T> create(T record);
	Optional<T> read(PK key);
	boolean update(T record);
	boolean delete(PK key);
	List<T> list();
}
