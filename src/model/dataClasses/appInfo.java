package model.dataClasses;

public class appInfo {
    private int gameId;
    private String name;
    private boolean isFree;
    private String description;
    private ariceInfo priceInfo;

    public appInfo(int gameId, String name, boolean isFree, String description, ariceInfo priceInfo) {
        this.gameId = gameId;
        this.name = name;
        this.isFree = isFree;
        this.description = description;
        this.priceInfo = priceInfo;
    }

    // Геттеры
    public int getGameId() { return gameId; }
    public String getName() { return name; }
    public boolean isFree() { return isFree; }
    public String getDescription() { return description; }
    public ariceInfo getPriceInfo() { return priceInfo; }


}