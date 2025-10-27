package test;

import model.DataClasses.appInfo;
import model.dataBaseModel;
import org.junit.jupiter.api.Test;
import service.steamApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class essentialTesting {

    ArrayList<appInfo>steamApps = new ArrayList<>();


    @Test
    public void dbTest(){
        IO.println("\t* Database testing");
        String[] dbcfg = null;
        try {
            dbcfg = Files.readAllLines(Paths.get("C:\\CFG_OOP\\configDB.txt")).getFirst().split(";");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataBaseModel db = new dataBaseModel(dbcfg[0], dbcfg[1], dbcfg[2]);

        for (appInfo app : steamApps) {
            assertTrue(db.deleteByItem(app,"appinfo"));
        }
        for (appInfo app : steamApps) {
            assertNull(db.getById(appInfo.class, app.getId(),"appinfo"));
        }
        for (appInfo app : steamApps) {
            assertTrue(db.addByItem(app,"appinfo"));
        }
        for (appInfo app : steamApps) {
            assertFalse(db.addByItem(app,"appinfo"));
        }
        for (appInfo app : steamApps) {
            appInfo appGet = db.getById(appInfo.class, app.getId(),"appinfo");
            assertNotNull(appGet);
            assertEquals(app.getId(), appGet.getId());
            assertEquals(app.getPriceInfo().getId(), appGet.getPriceInfo().getId());
        }
        for (appInfo app : steamApps) {
            assertTrue(db.deleteByItem(app,"appinfo"));
        }
        for (appInfo app : steamApps) {
            assertNull(db.getById(appInfo.class, app.getId(),"appinfo"));
        }
        for (appInfo app : steamApps) {
            assertTrue(db.deleteByItem(app,"appinfo"));
        }
        IO.println("\t+ Database testing complete!");
    }

    @Test
    public void steamApiTest(){
        IO.println("\t* SteamApi testing");
        ArrayList<Integer> steamIds = new ArrayList<>();
        steamIds.add(805550);
        steamIds.add(3167020);
        steamIds.add(2592160);
        steamIds.add(270880);
        steamIds.add(594650);
        steamIds.add(2183900);
        steamIds.add(2835570);
        steamIds.add(2300120);
        steamIds.add(3527290);




        for (int id : steamIds){
            appInfo app = steamApi.getGameInfo(id, "RU");
            assertNotNull(app);
            steamApps.add(app);
        }
        IO.println("\t+ SteamApi testing complete!");
    }

    @Test
    public void run(){
        IO.println("Essential Testing");
        steamApiTest();
        dbTest();
        IO.println("Essential Testing Complete!");

    }
}
