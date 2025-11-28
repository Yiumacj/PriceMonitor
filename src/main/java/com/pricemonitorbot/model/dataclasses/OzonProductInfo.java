package com.pricemonitorbot.model.dataclasses;

import com.pricemonitorbot.interfaces.model.IDataBaseObject;
import jakarta.persistence.*;

@Entity
@Table(name = "ozonproduct")
public class OzonProductInfo implements IDataBaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Ссылка на товар на Ozon */
    @Column(nullable = false, length = 2000)
    private String url;

    /** Название товара */
    @Column(nullable = false)
    private String name;

    /** SKU или какой-то идентификатор товара на Ozon (если будешь использовать) */
    @Column
    private String sku;

    /** Текущая ценовая информация по товару */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "priceinfo_id")
    private PriceInfo priceInfo;

    public OzonProductInfo() { }

    public OzonProductInfo(String url, String name, String sku, PriceInfo priceInfo) {
        this.url = url;
        this.name = name;
        this.sku = sku;
        this.priceInfo = priceInfo;
    }

    @Override
    public int getId() {
        // интерфейс, судя по PriceInfo, ожидает int
        return id;
    }

    public Integer getIdObject() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public PriceInfo getPriceInfo() {
        return priceInfo;
    }
}
