package model.DataClasses;

public class PriceInfo {
    private double finalPrice;
    private double initialPrice;
    private int discountPercent;
    private String currency;

    public PriceInfo(double finalPrice, double initialPrice, int discountPercent, String currency) {
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