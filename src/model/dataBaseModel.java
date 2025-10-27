package model;

import interfaces.model.IDataBaseItem;

public class dataBaseModel {
    private final dataBaseConnectorModel source;

    public dataBaseModel(String server, String user, String password) {
        source = new dataBaseConnectorModel(server, user, password);
    }

    public boolean updateByItem(IDataBaseItem updatedItem, String tableName) {
        return source.update(updatedItem, tableName);
    }

    public boolean deleteByItem(IDataBaseItem item, String tableName) {
        return source.delete(item, tableName);
    }

    public boolean addByItem(IDataBaseItem item, String tableName) {
        return source.insert(item, tableName);
    }
    public <T extends IDataBaseItem> T getById(Class<T> clazz,int id, String tableName) {
        return source.get(clazz, tableName, id);
    }
}