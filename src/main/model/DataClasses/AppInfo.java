package model.DataClasses;

import interfaces.model.IDataBaseObject;
import jakarta.persistence.*;

@Entity
@Table(name = "appinfo")
public class AppInfo implements IDataBaseObject {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id = 0;

    private String name = "";
    private boolean isFree = false;
    private String description = "";

    @OneToOne(cascade = CascadeType.ALL)  // <-- связь с ценой
    @JoinColumn(name = "priceinfo")
    private PriceInfo priceInfo = null;


    public AppInfo(int id, String name, boolean isFree, String description, PriceInfo priceInfo) {
        this.id = id;
        this.name = name;
        this.isFree = isFree;
        this.description = description;
        this.priceInfo = priceInfo;
    }

    public AppInfo(){}

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isFree() { return isFree; }
    public String getDescription() { return description; }
    public PriceInfo getPriceInfo() { return priceInfo; }


}