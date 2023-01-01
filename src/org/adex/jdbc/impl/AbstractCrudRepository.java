package org.adex.jdbc.impl;

import org.adex.jdbc.CrudRepository;
import org.adex.jdbc.annotations.Id;
import org.adex.jdbc.annotations.NotMapped;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AbstractCrudRepository<T, R> implements CrudRepository<T, R> {

    private Connection connection;
    private Class<T> targe;
    private String tableName;

    private Set<Field> columns;

    private Field idColumn;

    public AbstractCrudRepository(Connection connection, Class<T> targe) {
        this.connection = connection;
        this.targe = targe;
        init();
    }

    private void init() {
        this.tableName = targe.getSimpleName().toLowerCase();
        this.columns = new HashSet<>();
        for (Field field : targe.getDeclaredFields()) {
            if (!field.isAnnotationPresent(NotMapped.class))
                columns.add(field);
            if (field.isAnnotationPresent(Id.class))
                idColumn = field;
        }
    }

    @Override
    public List<T> findAll() {
        final List<T> res = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(Template.SELECT_ALL.getScript(tableName));
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                T newObject = targe.newInstance();
                mapperFields(newObject, resultSet);
                res.add(newObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void save(T object) {
        final List<String> columnsToSave = new ArrayList<>();
        final List<String> valuesToSave = new ArrayList<>();
        initColumnsAndValuesToSave(object, columnsToSave, valuesToSave);
        final String saveScript = Template.INSERT.getScript(tableName, String.join(",", columnsToSave), String.join(",", valuesToSave));
        try (PreparedStatement preparedStatement = connection.prepareStatement(saveScript)) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public T findById(R value) {
        String findByIdSql = Template.FIND_BY_ID.getScript(tableName, idColumn.getName().toLowerCase(), value.toString());
        T result = null;
        try (PreparedStatement statement = connection.prepareStatement(findByIdSql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                result = targe.newInstance();
                mapperFields(result, resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void deleteById(R id) {
        final String deleteScript = Template.DELETE.getScript(tableName, idColumn.getName().toLowerCase(), id.toString());
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteScript)) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(T object) {
        try {
            boolean accessible = idColumn.isAccessible();
            idColumn.setAccessible(true);
            deleteById((R) idColumn.get(object));
            idColumn.setAccessible(accessible);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(T object) {
        final String valuesToSet = getStringValuesToSetForUpdate(object);
        final String whereId = String.format("%s=%s", idColumn.getName().toLowerCase(), getFieldValue(idColumn, object));
        final String updateSql = Template.UPDATE.getScript(tableName, valuesToSet, whereId);
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStringValuesToSetForUpdate(T object) {
        return columns
                .stream()
                .map(column -> String.format("%s='%s'", column.getName().toLowerCase(), getFieldValue(column, object)))
                .collect(Collectors.joining(","));
    }

    private void initColumnsAndValuesToSave(T object, List<String> columnsToSave, List<String> valuesToSave) {
        for (Field field : columns) {
            columnsToSave.add(field.getName().toLowerCase());
            valuesToSave.add(String.format("'%s'", getFieldValue(field, object).toString()));
        }
    }

    private Object getFieldValue(Field field, T object) {
        Object value = null;
        try {
            boolean accessibilite = field.isAccessible();
            field.setAccessible(true);
            value = field.get(object);
            field.setAccessible(accessibilite);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private void mapperFields(T newObject, ResultSet resultSet) throws NoSuchFieldException, IllegalAccessException, SQLException {
        for (Field column : columns) {
            Field field = newObject.getClass().getDeclaredField(column.getName());
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(newObject, resultSet.getObject(column.getName()));
            field.setAccessible(accessible);
        }
    }


    private enum Template {
        SELECT_ALL("SELECT * from %s"),
        INSERT("INSERT INTO %s(%s) VALUES(%s)"),
        FIND_BY_ID("SELECT * from %s where %s=%s"),
        DELETE("DELETE FROM %s WHERE %s=%s"),
        UPDATE("UPDATE %s SET %s WHERE %s");

        private String script;

        Template(String script) {
            this.script = script;
        }

        public String getScript(String... params) {
            return String.format(script, params);
        }
    }

}
