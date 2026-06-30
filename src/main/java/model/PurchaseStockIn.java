package model;

import java.time.LocalDateTime;

public class PurchaseStockIn {
    private int stockInId;
    private int purchaseId;
    private int productId;
    private int warehouseId;
    private int stockInQuantity;
    private LocalDateTime stockInTime;
    private String remark;

    public PurchaseStockIn() {
    }

    public PurchaseStockIn(int stockInId, int purchaseId, int productId, int warehouseId,
                           int stockInQuantity, LocalDateTime stockInTime, String remark) {
        this.stockInId = stockInId;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.stockInQuantity = stockInQuantity;
        this.stockInTime = stockInTime;
        this.remark = remark;
    }

    public int getStockInId() {
        return stockInId;
    }

    public void setStockInId(int stockInId) {
        this.stockInId = stockInId;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getStockInQuantity() {
        return stockInQuantity;
    }

    public void setStockInQuantity(int stockInQuantity) {
        this.stockInQuantity = stockInQuantity;
    }

    public LocalDateTime getStockInTime() {
        return stockInTime;
    }

    public void setStockInTime(LocalDateTime stockInTime) {
        this.stockInTime = stockInTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}