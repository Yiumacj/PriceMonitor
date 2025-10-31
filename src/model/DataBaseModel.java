package model;

import model.dataClasses.AppInfo;

import java.util.ArrayList;

public class DataBaseModel {
    // Плейсхолдер TODO заменить на нормальную бдшку

    private final ArrayList<AppInfo> monitoredGames = new ArrayList<>();

    private AppInfo findItemById(int id) {
        for (AppInfo game: monitoredGames) {
            if (game.getGameId() == id) {
                return game;
            }
        }
        return null;
    }

    public AppInfo getById(int id) {
        return this.findItemById(id);
    }

    public boolean update(AppInfo newAppInfo) {
        AppInfo game = this.findItemById(newAppInfo.getGameId());
        if (game == null) {
            return false;
        }
        monitoredGames.remove(game);
        try {
            monitoredGames.add(newAppInfo.clone());
        } catch (CloneNotSupportedException e) {
            return false;
        }
        return true;
    }

    public boolean deleteById(int id) {
        AppInfo game = this.findItemById(id);
        if (game == null) {
            return false;
        }
        monitoredGames.remove(game);
        return true;
    }

    public boolean add(AppInfo game) {
        if (this.findItemById(game.getGameId()) != null) {
            return false;
        }
        this.monitoredGames.add(game);
        return true;
    }
}