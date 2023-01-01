package org.adex.jdbc;

import java.util.List;

public interface CrudRepository <T, R> {

    List<T> findAll();

    void save(T object) throws IllegalAccessException;

    T findById(R id);

    void deleteById(R id);

    void delete(T object);

    void update(T object);
}
