package model.dataClasses;

public class ariceInfo {
    private double finalPrice;
    private double initialPrice;
    private int discountPercent;
    private String currency;

    public ariceInfo(double finalPrice, double initialPrice, int discountPercent, String currency) {
        this.finalPrice = finalPrice;
        this.initialPrice = initialPrice;
        this.discountPercent = discountPercent;
        this.currency = currency;
    }

    // Геттеры
    public double getFinalPrice() { return finalPrice; }
    public double getInitialPrice() { return initialPrice; }
    public int getDiscountPercent() { return discountPercent; }
    public String getCurrency() { return currency; }
}