package org.adex.jdbc;

import org.adex.jdbc.exceptions.DataSourceException;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, R> {

    List<T> findAll() throws DataSourceException;

    void save(T object) throws DataSourceException;

    Optional<T> findById(R id) throws DataSourceException;

    void deleteById(R id) throws DataSourceException;

    void delete(T object) throws DataSourceException;

    void update(T object) throws DataSourceException;
}
