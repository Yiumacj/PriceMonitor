package test;

import model.dataClasses.appInfo;
import model.dataBaseModel;
import org.junit.jupiter.api.Test;
import service.steamApi;

import static org.junit.jupiter.api.Assertions.*;

public class essentialTesting {

    @Test
    public void dbTest(){
        IO.println("Database testing");
        dataBaseModel db = new dataBaseModel();

        appInfo dataTest = steamApi.getGameInfo(1190000, "RU");
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
