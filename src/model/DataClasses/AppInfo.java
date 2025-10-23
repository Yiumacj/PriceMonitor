package model.DataClasses;

public class AppInfo {
    private int gameId;
    private String name;
    private boolean isFree;
    private String description;
    private PriceInfo priceInfo;

    public AppInfo(int gameId, String name, boolean isFree, String description, PriceInfo priceInfo) {
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
    public PriceInfo getPriceInfo() { return priceInfo; }


}