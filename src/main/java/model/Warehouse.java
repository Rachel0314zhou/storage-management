package model;

import java.time.LocalDateTime;

public class Warehouse {
    private int warehouseId;
    private String warehouseName;
    private String address;
    private String managerName;
    private LocalDateTime createdAt;

    public Warehouse() {
    }

    public Warehouse(int warehouseId, String warehouseName, String address, String managerName, LocalDateTime createdAt) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.address = address;
        this.managerName = managerName;
        this.createdAt = createdAt;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}