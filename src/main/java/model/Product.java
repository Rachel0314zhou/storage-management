package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private int productId;
    private int categoryId;
    private int supplierId;

    private String productCode;
    private String productName;

    /**
     * 旧字段：原来页面可能叫“规格”。
     * 当前数据库没有 specification 字段，可以用 description 兼容赋值。
     */
    private String specification;

    /**
     * 当前数据库正式字段：商品描述。
     */
    private String description;

    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;

    private int stockLowerLimit;
    private int stockUpperLimit;

    /**
     * 为了兼容旧代码，status 继续使用 String。
     * 数据库中 status 是 TINYINT，DAO 可以用 rs.getString("status") 或 setStatus(int)。
     */
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {
    }

    /**
     * 保留旧构造方法，避免旧代码报错。
     */
    public Product(int productId, String productName, int categoryId, String specification,
                   String unit, BigDecimal purchasePrice, BigDecimal salePrice,
                   String status, LocalDateTime createdAt) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.specification = specification;
        this.description = specification;
        this.unit = unit;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * 新构造方法，对应当前 product 表公共字段。
     */
    public Product(int productId, int categoryId, int supplierId, String productCode,
                   String productName, String description, String unit,
                   BigDecimal purchasePrice, BigDecimal salePrice,
                   int stockLowerLimit, int stockUpperLimit,
                   String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.productCode = productCode;
        this.productName = productName;
        this.description = description;
        this.specification = description;
        this.unit = unit;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.stockLowerLimit = stockLowerLimit;
        this.stockUpperLimit = stockUpperLimit;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

        // 兼容旧页面：如果 specification 为空，就用 description 填充。
        if (this.specification == null || this.specification.length() == 0) {
            this.specification = description;
        }
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public int getStockLowerLimit() {
        return stockLowerLimit;
    }

    public void setStockLowerLimit(int stockLowerLimit) {
        this.stockLowerLimit = stockLowerLimit;
    }

    public int getStockUpperLimit() {
        return stockUpperLimit;
    }

    public void setStockUpperLimit(int stockUpperLimit) {
        this.stockUpperLimit = stockUpperLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 兼容 DAO 中 product.setStatus(rs.getInt("status")) 的写法。
     */
    public void setStatus(int status) {
        this.status = String.valueOf(status);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}