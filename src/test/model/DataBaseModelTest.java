package model;

import interfaces.model.IDataBaseObject;
import model.DataClasses.AppInfo;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DataBaseModelTest {

    @Test
    void delegates_to_connector() {
        IDataBaseObject item = mock(IDataBaseObject.class);

        try (MockedConstruction<DataBaseConnectorModel> construction =
                     mockConstruction(DataBaseConnectorModel.class, (mock, context) -> {
                         when(mock.insert(item)).thenReturn(true);
                         when(mock.update(item)).thenReturn(true);
                         when(mock.delete(item)).thenReturn(true);
                         when(mock.get(IDataBaseObject.class, 42)).thenReturn(new AppInfo());
                     })) {

            DataBaseModel model = new DataBaseModel("jdbc:mysql://localhost:3306/test", "", "");
            // add
            assertTrue(model.addByItem(item));
            // update
            assertTrue(model.updateByItem(item));
            // delete
            assertTrue(model.deleteByItem(item));
            // get
            Object res = model.getById(AppInfo.class, 42);
            assertNotNull(res);

            DataBaseConnectorModel mocked = construction.constructed().get(0);
            verify(mocked).insert(item);
            verify(mocked).update(item);
            verify(mocked).delete(item);
            verify(mocked).get(AppInfo.class, 42);
        }
    }
}
