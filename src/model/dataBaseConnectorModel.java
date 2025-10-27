package model;

import interfaces.model.IDataBaseItem;

import java.lang.reflect.Constructor;
import java.sql.*;
import java.lang.reflect.Field;


public class dataBaseConnectorModel {
    Connection connection;
    public dataBaseConnectorModel(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            this.connection = null;
        }
    }

    public boolean insert(IDataBaseItem data, String table) {
        Class<?> clazz = data.getClass();
        String tableName = clazz.getSimpleName().toLowerCase();

        Field[] fields = clazz.getDeclaredFields();

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            columns.append(fields[i].getName().toLowerCase());
            placeholders.append("?");

            if (i < fields.length - 1) {
                columns.append(", ");
                placeholders.append(", ");
            }
        }

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

        try {
            PreparedStatement  stmt = connection.prepareStatement(sql);

            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object value = fields[i].get(data);

                if (value instanceof IDataBaseItem) {
                    stmt.setObject(i + 1, ((IDataBaseItem) value).getId());
                    this.insert((IDataBaseItem) value, table);
                } else {
                    stmt.setObject(i + 1, value);
                }
            }
            stmt.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            return false;
        }
        return true;
    }
    public boolean delete(IDataBaseItem data, String table) {

        try {
            Class<?> clazz = data.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(data);

                if (value instanceof IDataBaseItem nestedItem) {
                    String nestedTable = nestedItem.getClass().getSimpleName().toLowerCase();
                    delete(nestedItem, nestedTable);
                }
            }

            String sql = "DELETE FROM " + table + " WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, data.getId());
                stmt.executeUpdate();
            }

            return true;

        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean update(IDataBaseItem data, String table) {
        boolean result = this.delete(data, table);
        result &= this.insert(data, table);
        return result;
    }

    public <T extends IDataBaseItem> T get(Class<T> clazz, String table, int id) {

        String sql = "SELECT * FROM " + table.toLowerCase() + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            Constructor<T> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            T instance = ctor.newInstance();

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = rs.getObject(name);

                if (value == null) continue;

                if (IDataBaseItem.class.isAssignableFrom(field.getType())) {
                    int refId = ((Number) value).intValue();
                    Object ref = get((Class<? extends IDataBaseItem>) field.getType(), field.getType().getSimpleName(), refId);
                    field.set(instance, ref);
                } else {
                    field.set(instance, value);
                }
            }
            return instance;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
