package model.DataClasses;

public class ItemInfo {
    public int id;
    public int price;

    public ItemInfo(int newId, int newPrice) {
        this.id = newId;
        this.price = newPrice;
    }

    public ItemInfo(ItemInfo original) {
        id = original.id;
        price = original.price;
    }
}