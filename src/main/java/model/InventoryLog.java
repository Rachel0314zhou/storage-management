package model;

import java.time.LocalDateTime;

public class InventoryLog {
    private int logId;
    private int productId;
    private int warehouseId;
    private String changeType;
    private int changeQuantity;
    private int beforeQuantity;
    private int afterQuantity;
    private String sourceType;
    private int sourceId;
    private String remark;
    private LocalDateTime createdAt;

    public InventoryLog() {
    }

    public InventoryLog(int logId, int productId, int warehouseId, String changeType,
                        int changeQuantity, int beforeQuantity, int afterQuantity,
                        String sourceType, int sourceId, String remark, LocalDateTime createdAt) {
        this.logId = logId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.changeType = changeType;
        this.changeQuantity = changeQuantity;
        this.beforeQuantity = beforeQuantity;
        this.afterQuantity = afterQuantity;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.remark = remark;
        this.createdAt = createdAt;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
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

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public int getChangeQuantity() {
        return changeQuantity;
    }

    public void setChangeQuantity(int changeQuantity) {
        this.changeQuantity = changeQuantity;
    }

    public int getBeforeQuantity() {
        return beforeQuantity;
    }

    public void setBeforeQuantity(int beforeQuantity) {
        this.beforeQuantity = beforeQuantity;
    }

    public int getAfterQuantity() {
        return afterQuantity;
    }

    public void setAfterQuantity(int afterQuantity) {
        this.afterQuantity = afterQuantity;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}