package com.pricemonitorbot.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pricemonitorbot.interfaces.model.IDataBaseObject;
import com.pricemonitorbot.model.DataBaseConnectorModel;
import com.pricemonitorbot.model.DataBaseModel;
import com.pricemonitorbot.model.dataclasses.AppInfo;

@ExtendWith(MockitoExtension.class)
public class DataBaseModelTest {

    @Test
    void delegates_to_connector() {
        IDataBaseObject item = mock(IDataBaseObject.class);

        DataBaseConnectorModel mocked = mock(DataBaseConnectorModel.class);
        AppInfo appInfoResult = new AppInfo();
        
        when(mocked.insert(item)).thenReturn(true);
        when(mocked.update(item)).thenReturn(true);
        when(mocked.delete(item)).thenReturn(true);
        when(mocked.get(AppInfo.class, 42)).thenReturn(appInfoResult);

        DataBaseModel model = new DataBaseModel(mocked);
        // add
        assertTrue(model.addByItem(item));
        verify(mocked).insert(item);
        
        // update
        assertTrue(model.updateByItem(item));
        verify(mocked).update(item);
        
        // delete
        assertTrue(model.deleteByItem(item));
        verify(mocked).delete(item);
        
        // get
        Object res = model.getById(AppInfo.class, 42);
        assertNotNull(res);
        verify(mocked).get(AppInfo.class, 42);
        assertSame(appInfoResult, res);
    }
}
