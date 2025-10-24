package model;

import model.DataClasses.AppInfo;
import model.DataClasses.PriceInfo;

import java.util.ArrayList;

public class dataBaseModel {
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

    public boolean updateById(int id, int newPrice) {
        AppInfo game = this.findItemById(id);
        if (game == null) {
            return false;
        }
        monitoredGames.remove(game);
        monitoredGames.add(
                new AppInfo(game.getGameId(), game.getName(), game.isFree(), game.getDescription(),
                new PriceInfo(
                        newPrice,
                        game.getPriceInfo().getInitialPrice(),
                        game.getPriceInfo().getDiscountPercent(),
                        game.getPriceInfo().getCurrency()
                ))
        );
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
            return false; // кал какой-то, но это все равно плейсхолдер
        }
        this.monitoredGames.add(game);
        return true;
    }
}