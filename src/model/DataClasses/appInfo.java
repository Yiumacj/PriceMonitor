package model.DataClasses;

import interfaces.model.IDataBaseItem;

public class appInfo implements IDataBaseItem {
    private int id = 0;
    private String name = "";
    private boolean isFree = false;
    private String description = "";
    private priceInfo priceInfo = null;

    public appInfo(int id, String name, boolean isFree, String description, priceInfo priceInfo) {
        this.id = id;
        this.name = name;
        this.isFree = isFree;
        this.description = description;
        this.priceInfo = priceInfo;
    }

    public appInfo(){}

    // Геттеры
    @Override
    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isFree() { return isFree; }
    public String getDescription() { return description; }
    public priceInfo getPriceInfo() { return priceInfo; }


}