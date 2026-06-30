package model;

import java.time.LocalDateTime;

public class SaleStockOut {
    private int stockOutId;
    private int saleId;
    private int productId;
    private int warehouseId;
    private int stockOutQuantity;
    private LocalDateTime stockOutTime;
    private String remark;

    public SaleStockOut() {
    }

    public SaleStockOut(int stockOutId, int saleId, int productId, int warehouseId,
                        int stockOutQuantity, LocalDateTime stockOutTime, String remark) {
        this.stockOutId = stockOutId;
        this.saleId = saleId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.stockOutQuantity = stockOutQuantity;
        this.stockOutTime = stockOutTime;
        this.remark = remark;
    }

    public int getStockOutId() {
        return stockOutId;
    }

    public void setStockOutId(int stockOutId) {
        this.stockOutId = stockOutId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
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

    public int getStockOutQuantity() {
        return stockOutQuantity;
    }

    public void setStockOutQuantity(int stockOutQuantity) {
        this.stockOutQuantity = stockOutQuantity;
    }

    public LocalDateTime getStockOutTime() {
        return stockOutTime;
    }

    public void setStockOutTime(LocalDateTime stockOutTime) {
        this.stockOutTime = stockOutTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}