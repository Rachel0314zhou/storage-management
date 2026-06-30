package model;

import java.time.LocalDateTime;

public class SaleOrder {
    private int saleId;
    private String customerName;
    private String status;
    private LocalDateTime saleDate;
    private String remark;

    public SaleOrder() {
    }

    public SaleOrder(int saleId, String customerName, String status, LocalDateTime saleDate, String remark) {
        this.saleId = saleId;
        this.customerName = customerName;
        this.status = status;
        this.saleDate = saleDate;
        this.remark = remark;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}