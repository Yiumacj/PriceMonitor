package model;

import interfaces.model.IDataBaseObject;

public class DataBaseModel {
    private final DataBaseConnectorModel source;

    public DataBaseModel(String server, String user, String password) {
        source = new DataBaseConnectorModel(server, user, password);
    }

    public boolean updateByItem(IDataBaseObject updatedItem) {
        return source.update(updatedItem);
    }

    public boolean deleteByItem(IDataBaseObject item) {
        return source.delete(item);
    }

    public boolean addByItem(IDataBaseObject item) {
        return source.insert(item);
    }
    public <T extends IDataBaseObject> T getById(Class<T> clazz, int id) {
        return source.get(clazz, id);
    }
}