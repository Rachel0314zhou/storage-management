package dao;

import db.DB;
import model.InventoryDetail;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * 库存 DAO
 *
 * 当前数据库设计说明：
 * 1. inventory 表已经按照公共核心字段统一，不再包含 warehouse_id。
 * 2. v_inventory_detail 视图不再包含 warehouse_id、warehouse_name、specification。
 * 3. 库存按照 product_id 汇总管理。
 * 4. 如果页面或 InventoryDetail 模型里暂时还有 warehouseName/specification 字段，
 *    这里做兼容处理，避免页面空指针或字段读取报错。
 */
public class InventoryDao {

    /**
     * 查询全部库存明细
     * 数据来源：视图 v_inventory_detail
     */
    public ArrayList<InventoryDetail> findAllInventory() {
        ArrayList<InventoryDetail> list = new ArrayList<>();

        // 当前库存视图中已经没有 warehouse_id，不能再 ORDER BY warehouse_id。
        String sql = "SELECT * FROM v_inventory_detail ORDER BY product_id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapInventoryDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 按关键字查询库存
     * 数据来源：存储过程 sp_query_inventory
     *
     * 当前支持：
     * 1. 商品编码
     * 2. 商品名称
     * 3. 商品描述
     * 4. 商品分类
     */
    public ArrayList<InventoryDetail> queryInventory(String keyword) {
        ArrayList<InventoryDetail> list = new ArrayList<>();

        String sql = "{CALL sp_query_inventory(?)}";

        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            cs = conn.prepareCall(sql);

            if (keyword == null) {
                keyword = "";
            }

            cs.setString(1, keyword.trim());
            rs = cs.executeQuery();

            while (rs.next()) {
                list.add(mapInventoryDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, cs, conn);
        }

        return list;
    }

    /**
     * 查询低库存商品
     * 数据来源：存储过程 sp_low_stock
     */
    public ArrayList<InventoryDetail> findLowStock(int threshold) {
        ArrayList<InventoryDetail> list = new ArrayList<>();

        String sql = "{CALL sp_low_stock(?)}";

        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            cs = conn.prepareCall(sql);
            cs.setInt(1, threshold);

            rs = cs.executeQuery();

            while (rs.next()) {
                list.add(mapInventoryDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, cs, conn);
        }

        return list;
    }

    /**
     * 查询某个商品的总库存
     * 数据来源：存储过程 sp_product_stock_summary
     */
    public int getProductTotalStock(int productId) {
        int totalQuantity = 0;

        String sql = "{CALL sp_product_stock_summary(?)}";

        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            cs = conn.prepareCall(sql);
            cs.setInt(1, productId);

            rs = cs.executeQuery();

            if (rs.next()) {
                totalQuantity = rs.getInt("total_quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, cs, conn);
        }

        return totalQuantity;
    }

    /**
     * 把 ResultSet 转换为 InventoryDetail 对象
     *
     * 注意：
     * 当前数据库已经没有 warehouse_id、warehouse_name、specification。
     * 为了兼容你原来的 InventoryDetail 类和 JSP 页面：
     * 1. specification 暂时用 description 代替。
     * 2. warehouseId 固定为 0。
     * 3. warehouseName 固定为“无仓库维度”。
     */
    private InventoryDetail mapInventoryDetail(ResultSet rs) throws SQLException {
        InventoryDetail detail = new InventoryDetail();

        detail.setInventoryId(rs.getInt("inventory_id"));
        detail.setProductId(rs.getInt("product_id"));
        detail.setProductName(rs.getString("product_name"));
        detail.setCategoryName(rs.getString("category_name"));

        // 当前公共字段中没有 specification，使用商品 description 兼容原页面。
        detail.setSpecification(getStringSafely(rs, "description"));

        detail.setUnit(rs.getString("unit"));



        detail.setQuantity(rs.getInt("quantity"));
        detail.setPurchasePrice(rs.getBigDecimal("purchase_price"));
        detail.setSalePrice(rs.getBigDecimal("sale_price"));

        // 当前库存表字段为 last_update_time；
        // 如果视图里仍保留 updated_at，也兼容读取。
        Timestamp updateTime = getTimestampSafely(rs, "last_update_time");
        if (updateTime == null) {
            updateTime = getTimestampSafely(rs, "updated_at");
        }

        if (updateTime != null) {
            detail.setUpdatedAt(updateTime.toLocalDateTime());
        }

        return detail;
    }

    /**
     * 安全读取字符串字段。
     * 如果结果集中没有该字段，返回空字符串，避免 Unknown column 异常。
     */
    private String getStringSafely(ResultSet rs, String columnName) {
        try {
            String value = rs.getString(columnName);
            return value == null ? "" : value;
        } catch (SQLException e) {
            return "";
        }
    }

    /**
     * 安全读取时间字段。
     * 如果结果集中没有该字段，返回 null。
     */
    private Timestamp getTimestampSafely(ResultSet rs, String columnName) {
        try {
            return rs.getTimestamp(columnName);
        } catch (SQLException e) {
            return null;
        }
    }
}