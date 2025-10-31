package test;

import model.DataClasses.AppInfo;
import model.DataBaseModel;
import org.junit.jupiter.api.Test;
import service.SteamApi;

import static org.junit.jupiter.api.Assertions.*;

public class essentialTesting {

    @Test
    public void dbTest(){
        IO.println("Database testing");
        DataBaseModel  db = new DataBaseModel();

        AppInfo dataTest = SteamApi.getGameInfo(1190000, "RU");
        assertNotNull(dataTest);
        assertTrue(db.add(dataTest));
        assertFalse(db.add(dataTest));
        assertNotNull(db.getById(1190000));
        assertTrue(db.deleteById(1190000));
        assertNull(db.getById(1190000));
        assertTrue(db.add(dataTest));

        IO.println("Database testing complete!");
    }
    @Test
    public void run(){
        IO.println("Essential Testing");
        dbTest();
        IO.println("Essential Testing Complete!");

    }
}
