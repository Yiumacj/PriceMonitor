package model;

import model.dataClasses.appInfo;

import java.util.ArrayList;

public class dataBaseModel {
    // Плейсхолдер TODO заменить на нормальную бдшку

    private final ArrayList<appInfo> monitoredGames = new ArrayList<>();

    private appInfo findItemById(int id) {
        for (appInfo game: monitoredGames) {
            if (game.getGameId() == id) {
                return game;
            }
        }
        return null;
    }

    public appInfo getById(int id) {
        return this.findItemById(id);
    }

    public boolean update(appInfo newAppInfo) {
        appInfo game = this.findItemById(newAppInfo.getGameId());
        if (game == null) {
            return false;
        }
        monitoredGames.remove(game);
        monitoredGames.add(newAppInfo);
        return true;
    }

    public boolean deleteById(int id) {
        appInfo game = this.findItemById(id);
        if (game == null) {
            return false;
        }
        monitoredGames.remove(game);
        return true;
    }

    public boolean add(appInfo game) {
        if (this.findItemById(game.getGameId()) != null) {
            return false;
        }
        this.monitoredGames.add(game);
        return true;
    }
}