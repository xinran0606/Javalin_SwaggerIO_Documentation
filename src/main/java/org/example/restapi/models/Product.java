package org.example.restapi.models;

import io.javalin.openapi.JsonSchema;
import io.swagger.v3.oas.annotations.media.Schema;

public class Product {
    @Schema(description = "Eindeutige Produkt-ID", example = "1")
    private Integer id;

    @Schema(description = "Produktname", example = "Smartphone XYZ")
    private String name;

    @Schema(description = "Produktbeschreibung", example = "Das neueste Smartphone-Modell")
    private String description;

    @Schema(description = "Preis in Euro", example = "799.99")
    private Double price;

    @Schema(description = "Verfügbare Menge", example = "50")
    private Integer stock;

    // 构造函数
    public Product() {
    }

    public Product(Integer id, String name, String description, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}