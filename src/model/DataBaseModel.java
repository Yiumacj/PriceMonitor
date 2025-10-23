package model;

import model.DataClasses.ItemInfo;

import java.util.ArrayList;

public class DataBaseModel {
    // Плейсхолдер TODO заменить на нормальную бдшку

    private ArrayList<ItemInfo> monitoredGames = new ArrayList<>();

    private ItemInfo findItemById(int id) {
        for (ItemInfo game: monitoredGames) {
            if (game.id == id) {
                return game;
            }
        }
        return null;
    }

    public ItemInfo getById(int id) {
        ItemInfo game = this.findItemById(id);
        if (game == null) {
            return null;
        }
        return new ItemInfo(game);
    }

    public void updateById(int id, int newPrice) {
        ItemInfo game = this.findItemById(id);
        if (game == null) {
            return;
        }
        game.price = newPrice;
    }

    public void deleteById(int id, int newPrice) {
        ItemInfo game = this.findItemById(id);
        if (game == null) {
            return;
        }
        monitoredGames.remove(game);
    }

    public void add(ItemInfo game) {
        if (this.findItemById(game.id) != null) {
            throw new IllegalArgumentException("Запись с таким id уже существует"); // кал какой-то, но это все равно плейсхолдер
        }
        this.monitoredGames.add(game);
    }
}