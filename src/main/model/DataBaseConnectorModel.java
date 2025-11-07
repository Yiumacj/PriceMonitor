package model;

import interfaces.model.IDataBaseObject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseConnectorModel {

    private Session session;
    Transaction tx = null;

    public DataBaseConnectorModel(String url, String username, String password) {
        session = HibernateUtil.getSessionFactory(url, username, password).openSession();
    }

    public boolean closeSession() {
        if (session != null && session.isOpen()) {
            session.close();
            return true;
        }
        return false;
    }
    public boolean openTransaction() {
        try {
            tx = session.beginTransaction();
        } catch (Exception e) {
            tx = null;
            return false;
        }
        return true;
    }
    // Insert
    public boolean insert(IDataBaseObject data) {
        return insert(data, true);
    }

    public boolean insert(IDataBaseObject data, boolean autoCommit) {
        try {
            if (autoCommit) tx = session.beginTransaction();
            session.persist(data);
            if (autoCommit) tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    // Update
    public boolean update(IDataBaseObject data) {
        return update(data, true);
    }
    public boolean update(IDataBaseObject data, boolean autoCommit) {
        Transaction tx = null;
        try {
            if (autoCommit) tx = session.beginTransaction();
            session.merge(data);
            if (autoCommit) tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    // Delete
    public boolean delete(IDataBaseObject data) {
        return delete(data, true);
    }
    public boolean delete(IDataBaseObject data, boolean autoCommit) {
        Transaction tx = null;
        try {
            if (autoCommit) tx = session.beginTransaction();
            session.delete(data);
            if (autoCommit) tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    // Get
    public <T extends IDataBaseObject> T get(Class<T> clazz, int id) {
        return session.get(clazz, id);
    }
}
