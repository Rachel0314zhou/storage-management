package model;

import java.time.LocalDateTime;

public class PurchaseOrder {
    private int purchaseId;
    private int supplierId;
    private String status;
    private LocalDateTime purchaseDate;
    private String remark;

    public PurchaseOrder() {
    }

    public PurchaseOrder(int purchaseId, int supplierId, String status, LocalDateTime purchaseDate, String remark) {
        this.purchaseId = purchaseId;
        this.supplierId = supplierId;
        this.status = status;
        this.purchaseDate = purchaseDate;
        this.remark = remark;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}