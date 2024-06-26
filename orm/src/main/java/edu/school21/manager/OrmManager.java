package edu.school21.manager;

import edu.school21.annotations.OrmColumn;
import edu.school21.annotations.OrmColumnId;
import edu.school21.annotations.OrmEntity;
import edu.school21.manager.exception.OrmManagerException;
import edu.school21.utils.DatabaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public final class OrmManager implements AutoCloseable {
    private static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS ";

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS ";

    private static final String INSERT_QUERY = "INSERT INTO ";
    private final Connection connection;
    private final List<Class<?>> ormAnnotatedEntities;

    public OrmManager(DataSource dataSource, String entityPackageName) throws OrmManagerException {
        try {
            this.connection = dataSource.getConnection();
            this.ormAnnotatedEntities = getOrmEntities(entityPackageName);
            begin();
            dropEntityTables();
            createEntityTables();
            commit();
        } catch (Exception e) {
            rollback();
            throw new OrmManagerException("The ORM manager can't be created: " + e.getMessage(), e);
        }
    }

    private List<Class<?>> getOrmEntities(String entityPackageName) {
        Reflections reflections = new Reflections(entityPackageName, Scanners.TypesAnnotated);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(OrmEntity.class);
        return new ArrayList<>(annotatedClasses);
    }

    private void dropEntityTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (Class<?> entity : ormAnnotatedEntities) {
                OrmEntity ormEntity = entity.getAnnotation(OrmEntity.class);
                if (ormEntity != null) {
                    String sql = DROP_TABLE_QUERY + ormEntity.table() + " CASCADE;";
                    log.info(sql);
                    statement.addBatch(sql);
                }
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error("Failed to drop tables: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void createEntityTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (Class<?> entity : ormAnnotatedEntities) {
                OrmEntity ormEntity = entity.getAnnotation(OrmEntity.class);
                if (ormEntity != null) {
                    StringBuilder sql = new StringBuilder(CREATE_TABLE_QUERY + ormEntity.table() + " (");
                    for (Field field : entity.getDeclaredFields()) {
                        OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                        OrmColumnId ormColumnId = field.getAnnotation(OrmColumnId.class);
                        if (ormColumnId != null) {
                            sql.append(field.getName()).append(" SERIAL PRIMARY KEY, ");
                        } else if (ormColumn != null) {
                            sql.append(ormColumn.name()).append(" ").append(getSqlType(field, ormColumn)).append(", ");
                        }
                    }
                    sql.delete(sql.length() - 2, sql.length()).append(");");
                    log.info(sql.toString());
                    statement.addBatch(sql.toString());
                }
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error("Failed to create tables: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String getSqlType(Field field, OrmColumn ormColumn) {
        Class<?> type = field.getType();
        if (type == String.class) {
            return "VARCHAR(" + ormColumn.length() + ")";
        } else if (type == int.class || type == Integer.class) {
            return "INTEGER";
        } else if (type == double.class || type == Double.class) {
            return "DOUBLE";
        } else if (type == boolean.class || type == Boolean.class) {
            return "BOOLEAN";
        } else if (type == long.class || type == Long.class) {
            return "BIGINT";
        }
        throw new IllegalArgumentException("Unsupported field type: " + type);
    }

    public void save(Object entity) throws SQLException, OrmManagerException {
        Class<?> clazz = entity.getClass();
        OrmEntity ormEntity = clazz.getAnnotation(OrmEntity.class);

        if (ormEntity != null) {
            StringBuilder sql = new StringBuilder(INSERT_QUERY + ormEntity.table() + " (");
            StringBuilder values = new StringBuilder(" VALUES (");
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                if (ormColumn != null) {
                    sql.append(ormColumn.name()).append(", ");
                    try {
                        values.append("'").append(field.get(entity)).append("', ");
                    } catch (IllegalAccessException e) {
                        throw new OrmManagerException("Failed to access field value: " + field.getName(), e);
                    }
                }
            }

            sql.delete(sql.length() - 2, sql.length()).append(")");
            values.delete(values.length() - 2, values.length()).append(");");
            sql.append(values);
            log.info(sql.toString());

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
                preparedStatement.executeUpdate();
            }
        }
    }

    public void update(Object entity) throws SQLException, OrmManagerException {
        Class<?> clazz = entity.getClass();
        OrmEntity ormEntity = clazz.getAnnotation(OrmEntity.class);

        if (ormEntity != null) {
            StringBuilder sql = new StringBuilder("UPDATE " + ormEntity.table() + " SET ");
            String idColumn = null;
            Object idValue = null;
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                OrmColumnId ormColumnId = field.getAnnotation(OrmColumnId.class);
                try {
                    if (ormColumn != null) {
                        sql.append(ormColumn.name()).append("='").append(field.get(entity)).append("', ");
                    } else if (ormColumnId != null) {
                        idColumn = field.getName();
                        idValue = field.get(entity);
                    }
                } catch (IllegalAccessException e) {
                    throw new OrmManagerException("Failed to access field value: " + field.getName(), e);
                }
            }

            if (idColumn == null || idValue == null) {
                throw new OrmManagerException("Entity must have an ID field annotated with @OrmColumnId");
            }

            sql.delete(sql.length() - 2, sql.length());
            sql.append(" WHERE ").append(idColumn).append(" = ").append(idValue).append(";");
            log.info(sql.toString());

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
                preparedStatement.executeUpdate();
            }
        }
    }

    public <T> T findById(Long id, Class<T> clazz) throws SQLException, OrmManagerException {
        OrmEntity ormEntity = clazz.getAnnotation(OrmEntity.class);

        if (ormEntity != null) {
            String sql = "SELECT * FROM " + ormEntity.table() + " WHERE id = ?;";
            log.info(sql);
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return createEntityFromResultSet(resultSet, clazz);
                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new OrmManagerException("Failed to create entity instance", e);
            }
        }

        return null;
    }

    private <T> T createEntityFromResultSet(ResultSet resultSet, Class<T> clazz) throws ReflectiveOperationException, SQLException {
        T entity = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
            OrmColumnId ormColumnId = field.getAnnotation(OrmColumnId.class);
            if (ormColumn != null || ormColumnId != null) {
                String columnName = ormColumn != null ? ormColumn.name() : field.getName();
                Object value = getFieldValueFromResultSet(resultSet, field, columnName);
                field.set(entity, value);
            }
        }

        return entity;
    }

    private Object getFieldValueFromResultSet(ResultSet resultSet, Field field, String columnName) throws SQLException {
        Class<?> fieldType = field.getType();
        Object value = null;

        if (fieldType == Long.class || fieldType == long.class) {
            value = resultSet.getLong(columnName);
        } else if (fieldType == Integer.class || fieldType == int.class) {
            value = resultSet.getInt(columnName);
        } else if (fieldType == String.class) {
            value = resultSet.getString(columnName);
        } else if (fieldType == Double.class || fieldType == double.class) {
            value = resultSet.getDouble(columnName);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            value = resultSet.getBoolean(columnName);
        }

        return value;
    }

    public void begin() throws OrmManagerException {
        try {
            if (DatabaseUtils.isConnectionAvailable(connection)) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new OrmManagerException("The transaction cant be start!", e);
        }
    }

    public void commit() throws OrmManagerException {
        try {
            if (DatabaseUtils.isConnectionAvailable(connection)) {
                connection.commit();
            }
        } catch (SQLException e) {
            throw new OrmManagerException("The commit of transaction cant be complete!", e);
        }
    }

    public void rollback() throws OrmManagerException {
        try {
            if (DatabaseUtils.isConnectionAvailable(connection)) {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new OrmManagerException("The rollback of transaction can't be complete!", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (DatabaseUtils.isConnectionAvailable(connection)) {
            connection.close();
        }
    }

}
