package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleDetail {
    private int saleId;
    private LocalDateTime saleDate;
    private String status;
    private String customerName;
    private int saleItemId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String remark;

    public SaleDetail() {
    }

    public SaleDetail(int saleId, LocalDateTime saleDate, String status,
                      String customerName, int saleItemId, String productName,
                      int quantity, BigDecimal unitPrice, BigDecimal amount, String remark) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.status = status;
        this.customerName = customerName;
        this.saleItemId = saleItemId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.remark = remark;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getSaleItemId() {
        return saleItemId;
    }

    public void setSaleItemId(int saleItemId) {
        this.saleItemId = saleItemId;
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