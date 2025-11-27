package com.pricemonitorbot.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pricemonitorbot.interfaces.model.IDataBaseObject;
import com.pricemonitorbot.model.DataBaseConnectorModel;


@ExtendWith(MockitoExtension.class)
public class DataBaseConnectorModelTest {

    private DataBaseConnectorModel newConnector(SessionFactory sf, Session session) {
        // Create the connector by injecting the mocked Session
        return new DataBaseConnectorModel(session);
    }

    @Test
    void constructor_acceptsSession() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);

        DataBaseConnectorModel dbc = newConnector(sf, session);
        assertNotNull(dbc);
    }

    @Test
    void closeSession_returnsTrue_whenOpen() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);
        when(session.isOpen()).thenReturn(true);

        DataBaseConnectorModel dbc = newConnector(sf, session);
        assertTrue(dbc.closeSession());
        verify(session).close();
    }

    @Test
    void closeSession_returnsFalse_whenClosed() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);
        when(session.isOpen()).thenReturn(false);

        DataBaseConnectorModel dbc = newConnector(sf, session);
        assertFalse(dbc.closeSession());
        verify(session, never()).close();
    }

    @Test
    void openTransaction_success() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);
        when(session.beginTransaction()).thenReturn(mock(Transaction.class));

        DataBaseConnectorModel dbc = newConnector(sf, session);
        assertTrue(dbc.openTransaction());
        verify(session).beginTransaction();
    }

    @Test
    void openTransaction_handlesException() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);
        when(session.beginTransaction()).thenThrow(new RuntimeException("boom"));

        DataBaseConnectorModel dbc = newConnector(sf, session);
        assertFalse(dbc.openTransaction());
        verify(session).beginTransaction();
    }

    @Test
    void insert_autoCommitTrue_success() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction tx = mock(Transaction.class);
        when(session.beginTransaction()).thenReturn(tx);

        DataBaseConnectorModel dbc = newConnector(sf, session);
        IDataBaseObject item = mock(IDataBaseObject.class);

        assertTrue(dbc.insert(item)); // autoCommit=true
        verify(session).beginTransaction();
        verify(session).persist(item);
        verify(tx).commit();
    }

    @Test
    void insert_autoCommitTrue_rollbackOnException() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction tx = mock(Transaction.class);
        when(session.beginTransaction()).thenReturn(tx);

        DataBaseConnectorModel dbc = newConnector(sf, session);
        IDataBaseObject item = mock(IDataBaseObject.class);
        doThrow(new RuntimeException("persist failed")).when(session).persist(item);

        assertFalse(dbc.insert(item));
        verify(tx).rollback();
    }

    @Test
    void insert_autoCommitFalse_success() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);

        DataBaseConnectorModel dbc = newConnector(sf, session);
        IDataBaseObject item = mock(IDataBaseObject.class);

        assertTrue(dbc.insert(item, false));
        verify(session, never()).beginTransaction();
        verify(session).persist(item);
    }

    @Test
    void update_delete_get_work() {
        SessionFactory sf = mock(SessionFactory.class);
        Session session = mock(Session.class);
        Transaction tx = mock(Transaction.class);
        when(session.beginTransaction()).thenReturn(tx);

        DataBaseConnectorModel dbc = newConnector(sf, session);
        IDataBaseObject item = mock(IDataBaseObject.class);

        // update
        when(session.merge(item)).thenReturn(item);
        assertTrue(dbc.update(item));
        verify(session).merge(item);
        verify(tx).commit();

        // delete
        assertTrue(dbc.delete(item));
        verify(session).remove(item);

        // get
        class Dummy implements IDataBaseObject { public int getId(){ return 1; } }
        Dummy expected = new Dummy();
        when(session.get(Dummy.class, 1)).thenReturn(expected);
        assertSame(expected, dbc.get(Dummy.class, 1));
    }
}
