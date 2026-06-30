package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryDetail {
    private int inventoryId;
    private int productId;
    private String productName;
    private String categoryName;
    private String specification;
    private String unit;
    private int quantity;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private LocalDateTime updatedAt;

    public InventoryDetail() {
    }

    public InventoryDetail(int inventoryId,
                           int productId,
                           String productName,
                           String categoryName,
                           String specification,
                           String unit,
                           int quantity,
                           BigDecimal purchasePrice,
                           BigDecimal salePrice,
                           LocalDateTime updatedAt) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.specification = specification;
        this.unit = unit;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.updatedAt = updatedAt;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}