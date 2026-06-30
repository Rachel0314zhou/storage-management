package dao;

import db.DB;
import model.SaleDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 销售 DAO
 * 负责销售订单、销售明细、销售出库操作
 *
 * 注意：
 * 后端这里只插入销售出库记录，不手动扣减库存。
 * 库存是否充足由触发器 trg_sale_stock_out_before_insert 检查。
 * 库存扣减由触发器 trg_sale_stock_out_after_insert 自动完成。
 */
public class SaleDao {

    /**
     * 新增销售出库业务
     *
     * 执行流程：
     * 1. 检查库存是否充足（提前检查，避免消耗自增ID）
     * 2. 插入 sales_order
     * 3. 插入 sales_order_item
     * 4. 插入 sale_stock_out
     * 5. 数据库触发器检查库存是否充足（二次保障）
     * 6. 数据库触发器自动扣减库存
     * 7. 数据库触发器自动生成库存流水
     *
     * @return 新增的销售订单 ID
     */
    public int addSaleStockOut(int customerId,
                               int productId,
                               int quantity,
                               BigDecimal unitPrice,
                               String remark) throws SQLException {

        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        PreparedStatement psStockOut = null;
        PreparedStatement psUpdateTotal = null;
        ResultSet rs = null;

        int saleId = 0;

        try {
            conn = DB.getConnection();

            if (conn == null) {
                throw new SQLException("数据库连接失败");
            }

            conn.setAutoCommit(false);

            // ===== 第1步：提前检查库存是否充足（不消耗ID） =====
            String checkStockSql = "SELECT quantity FROM inventory WHERE product_id = ?";
            psCheck = conn.prepareStatement(checkStockSql);
            psCheck.setInt(1, productId);
            rs = psCheck.executeQuery();

            if (rs.next()) {
                int currentStock = rs.getInt("quantity");
                if (currentStock < quantity) {
                    throw new SQLException("库存不足，当前库存：" + currentStock + "，需要出库：" + quantity);
                }
            } else {
                throw new SQLException("商品不存在或库存记录为空");
            }
            rs.close();
            psCheck.close();

            // ===== 第2步：库存充足，开始插入数据 =====

            // 2.1 插入销售订单
            String orderSql = "INSERT INTO sales_order (customer_id, order_date, status, remark) VALUES (?, NOW(), 1, ?)";

            psOrder = conn.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, customerId);
            psOrder.setString(2, remark);
            psOrder.executeUpdate();

            rs = psOrder.getGeneratedKeys();

            if (rs.next()) {
                saleId = rs.getInt(1);
            } else {
                throw new SQLException("新增销售订单失败，未获取到 sales_order_id");
            }
            rs.close();

            // 2.2 插入销售订单明细
            String itemSql = "INSERT INTO sales_order_item (sales_order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

            psItem = conn.prepareStatement(itemSql);
            psItem.setInt(1, saleId);
            psItem.setInt(2, productId);
            psItem.setInt(3, quantity);
            psItem.setBigDecimal(4, unitPrice);
            psItem.executeUpdate();

            // 2.3 插入销售出库记录（无 warehouse_id）
            String stockOutSql = "INSERT INTO sale_stock_out (sales_order_id, product_id, stock_out_quantity, remark) VALUES (?, ?, ?, ?)";

            psStockOut = conn.prepareStatement(stockOutSql);
            psStockOut.setInt(1, saleId);
            psStockOut.setInt(2, productId);
            psStockOut.setInt(3, quantity);
            psStockOut.setString(4, remark);
            psStockOut.executeUpdate();

            // 2.4 回填订单总金额
            String updateTotalSql = "UPDATE sales_order SET total_amount = (SELECT COALESCE(SUM(quantity * unit_price), 0) FROM sales_order_item WHERE sales_order_id = ?) WHERE sales_order_id = ?";
            psUpdateTotal = conn.prepareStatement(updateTotalSql);
            psUpdateTotal.setInt(1, saleId);
            psUpdateTotal.setInt(2, saleId);
            psUpdateTotal.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            DB.rollback(conn);
            throw e;
        } finally {
            DB.close(rs, psCheck, null);
            DB.close(null, psStockOut, null);
            DB.close(null, psItem, null);
            DB.close(null, psOrder, null);
            DB.close(null, psUpdateTotal, null);
            DB.close(conn);
        }

        return saleId;
    }


    /**
     * 查询销售明细
     * 数据来源：视图 v_sale_detail
     */
    public ArrayList<SaleDetail> findAllSaleDetail() {
        ArrayList<SaleDetail> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "sales_order_id AS sale_id, " +
                        "order_date AS sale_date, " +
                        "status, " +
                        "total_amount, " +
                        "remark, " +
                        "customer_id, " +
                        "customer_name, " +
                        "sales_order_item_id AS sale_item_id, " +
                        "product_id, " +
                        "product_code, " +
                        "product_name, " +
                        "quantity, " +
                        "unit_price, " +
                        "subtotal AS amount " +
                        "FROM v_sale_detail " +
                        "ORDER BY sales_order_id DESC, sales_order_item_id ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapSaleDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 按客户名称或商品名称查询销售明细
     */
    public ArrayList<SaleDetail> searchSaleDetail(String keyword) {
        ArrayList<SaleDetail> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "sales_order_id AS sale_id, " +
                        "order_date AS sale_date, " +
                        "status, " +
                        "total_amount, " +
                        "remark, " +
                        "customer_id, " +
                        "customer_name, " +
                        "sales_order_item_id AS sale_item_id, " +
                        "product_id, " +
                        "product_code, " +
                        "product_name, " +
                        "quantity, " +
                        "unit_price, " +
                        "subtotal AS amount " +
                        "FROM v_sale_detail " +
                        "WHERE customer_name LIKE ? OR product_name LIKE ? " +
                        "ORDER BY sales_order_id DESC, sales_order_item_id ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (keyword == null) {
                keyword = "";
            }

            String key = "%" + keyword.trim() + "%";

            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, key);
            ps.setString(2, key);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapSaleDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 把 ResultSet 转换为 SaleDetail 对象
     */
    private SaleDetail mapSaleDetail(ResultSet rs) throws SQLException {
        SaleDetail detail = new SaleDetail();

        detail.setSaleId(rs.getInt("sale_id"));

        if (rs.getTimestamp("sale_date") != null) {
            detail.setSaleDate(rs.getTimestamp("sale_date").toLocalDateTime());
        }

        detail.setStatus(rs.getString("status"));
        detail.setCustomerName(rs.getString("customer_name"));
        detail.setSaleItemId(rs.getInt("sale_item_id"));
        detail.setProductName(rs.getString("product_name"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setUnitPrice(rs.getBigDecimal("unit_price"));
        detail.setAmount(rs.getBigDecimal("amount"));
        detail.setRemark(rs.getString("remark"));

        return detail;
    }

    // 内部类：用于封装商品信息
    public static class SaleItem {
        private int productId;
        private int quantity;
        private BigDecimal unitPrice;

        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }

    /**
     * 新增多商品销售出库业务
     *
     * @param customerId 客户ID
     * @param items 商品列表
     * @param remark 备注
     * @return 新增的销售订单 ID
     */
    public int addMultiSaleStockOut(int customerId,
                                    java.util.List<SaleItem> items,
                                    String remark) throws SQLException {

        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        PreparedStatement psStockOut = null;
        PreparedStatement psUpdateTotal = null;
        ResultSet rs = null;

        int saleId = 0;

        try {
            conn = DB.getConnection();

            if (conn == null) {
                throw new SQLException("数据库连接失败");
            }

            conn.setAutoCommit(false);

            // ===== 第1步：提前检查所有商品的库存 =====
            for (SaleItem item : items) {
                String checkStockSql = "SELECT quantity FROM inventory WHERE product_id = ?";
                try (PreparedStatement psCheck = conn.prepareStatement(checkStockSql)) {
                    psCheck.setInt(1, item.getProductId());
                    ResultSet rsCheck = psCheck.executeQuery();
                    if (rsCheck.next()) {
                        int currentStock = rsCheck.getInt("quantity");
                        if (currentStock < item.getQuantity()) {
                            throw new SQLException("库存不足，商品ID：" + item.getProductId() +
                                    "，当前库存：" + currentStock + "，需要出库：" + item.getQuantity());
                        }
                    } else {
                        throw new SQLException("商品不存在，商品ID：" + item.getProductId());
                    }
                    rsCheck.close();
                }
            }

            // ===== 第2步：插入销售订单 =====
            String orderSql = "INSERT INTO sales_order (customer_id, order_date, status, remark) VALUES (?, NOW(), 1, ?)";

            psOrder = conn.prepareStatement(orderSql, PreparedStatement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, customerId);
            psOrder.setString(2, remark);
            psOrder.executeUpdate();

            rs = psOrder.getGeneratedKeys();

            if (rs.next()) {
                saleId = rs.getInt(1);
            } else {
                throw new SQLException("新增销售订单失败，未获取到 sales_order_id");
            }
            rs.close();

            // ===== 第3步：循环插入销售订单明细和出库记录 =====
            for (SaleItem item : items) {
                // 3.1 插入明细
                String itemSql = "INSERT INTO sales_order_item (sales_order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
                psItem = conn.prepareStatement(itemSql);
                psItem.setInt(1, saleId);
                psItem.setInt(2, item.getProductId());
                psItem.setInt(3, item.getQuantity());
                psItem.setBigDecimal(4, item.getUnitPrice());
                psItem.executeUpdate();
                psItem.close();

                // 3.2 插入出库记录
                String stockOutSql = "INSERT INTO sale_stock_out (sales_order_id, product_id, stock_out_quantity, remark) VALUES (?, ?, ?, ?)";
                psStockOut = conn.prepareStatement(stockOutSql);
                psStockOut.setInt(1, saleId);
                psStockOut.setInt(2, item.getProductId());
                psStockOut.setInt(3, item.getQuantity());
                psStockOut.setString(4, remark);
                psStockOut.executeUpdate();
                psStockOut.close();
            }

            // ===== 第4步：回填订单总金额 =====
            String updateTotalSql = "UPDATE sales_order SET total_amount = (SELECT COALESCE(SUM(quantity * unit_price), 0) FROM sales_order_item WHERE sales_order_id = ?) WHERE sales_order_id = ?";
            psUpdateTotal = conn.prepareStatement(updateTotalSql);
            psUpdateTotal.setInt(1, saleId);
            psUpdateTotal.setInt(2, saleId);
            psUpdateTotal.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            DB.rollback(conn);
            throw e;
        } finally {
            DB.close(rs, null, null);
            DB.close(null, psStockOut, null);
            DB.close(null, psItem, null);
            DB.close(null, psOrder, null);
            DB.close(null, psUpdateTotal, null);
            DB.close(conn);
        }

        return saleId;
    }
}