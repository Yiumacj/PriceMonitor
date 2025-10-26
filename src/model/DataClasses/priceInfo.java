package model.DataClasses;

import interfaces.model.IDataBaseItem;

public class priceInfo implements IDataBaseItem {
    private int id = 0;
    private double finalPrice = 0;
    private double initialPrice = 0;
    private int discountPercent = 0;
    private String currency = "";

    public priceInfo(int id, double finalPrice, double initialPrice, int discountPercent, String currency) {
        this.id = id;
        this.finalPrice = finalPrice;
        this.initialPrice = initialPrice;
        this.discountPercent = discountPercent;
        this.currency = currency;
    }

    public priceInfo() {}

    // Геттеры
    @Override
    public int getId() { return id; }
    public double getFinalPrice() { return finalPrice; }
    public double getInitialPrice() { return initialPrice; }
    public int getDiscountPercent() { return discountPercent; }
    public String getCurrency() { return currency; }
}