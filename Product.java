package com.example.phase4_1220813_122856_1210475;

public class Product {
    private int productId;
    private String productName;
    private String category;
    private int reorderLevel;
    private int quantity;
    private double unitPrice;
    private String description;
    private int stockId;

    public Product(int productId, String productName, String category, int reorderLevel, int quantity, double unitPrice, String description, int stockId) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.reorderLevel = reorderLevel;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.description = description;
        this.stockId = stockId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }
}