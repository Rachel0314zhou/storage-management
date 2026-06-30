package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseDetail {
    private int purchaseId;
    private LocalDateTime purchaseDate;
    private String status;
    private String supplierName;
    private int purchaseItemId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String remark;

    public PurchaseDetail() {
    }

    public PurchaseDetail(int purchaseId, LocalDateTime purchaseDate, String status,
                          String supplierName, int purchaseItemId, String productName,
                          int quantity, BigDecimal unitPrice, BigDecimal amount, String remark) {
        this.purchaseId = purchaseId;
        this.purchaseDate = purchaseDate;
        this.status = status;
        this.supplierName = supplierName;
        this.purchaseItemId = purchaseItemId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.remark = remark;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public int getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(int purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}