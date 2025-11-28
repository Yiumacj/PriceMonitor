package com.pricemonitorbot.model.dataclasses;

import com.pricemonitorbot.interfaces.model.IDataBaseObject;
import jakarta.persistence.*;

@Entity
@Table(name = "priceinfo")
public class PriceInfo implements IDataBaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id = 0;
    private double finalPrice = 0;
    private double initialPrice = 0;
    private int discountPercent = 0;
    private String currency = "";

    public PriceInfo(double finalPrice, double initialPrice, int discountPercent, String currency) {
        this.finalPrice = finalPrice;
        this.initialPrice = initialPrice;
        this.discountPercent = discountPercent;
        this.currency = currency;
    }

    public PriceInfo() {}

    public int getId() { return id; }
    public double getFinalPrice() { return finalPrice; }
    public double getInitialPrice() { return initialPrice; }
    public int getDiscountPercent() { return discountPercent; }
    public String getCurrency() { return currency; }
}
