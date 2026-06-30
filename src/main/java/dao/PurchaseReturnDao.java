package dao;

import db.DB;
import model.PurchaseReturnDetail;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 采购退货 DAO
 *
 * 主要功能：
 * 1. 新增采购退货，调用 sp_purchase_return_tx。
 * 2. 查询采购退货历史记录。
 * 3. 查询可退货采购记录，支持关键字搜索和分页。
 *
 * 说明：
 * 采购退货不让用户手填采购订单ID和商品ID，
 * 而是在页面上从“可退货采购记录”中点击退货按钮，
 * 系统自动带入 purchase_id 和 product_id。
 */
public class PurchaseReturnDao {

    /**
     * 新增采购退货。
     *
     * 调用数据库事务过程：
     * sp_purchase_return_tx(purchase_id, product_id, return_quantity, reason, remark)
     *
     * 库存扣减、库存流水生成、采购订单状态更新由数据库触发器/存储过程完成。
     */
    public int addPurchaseReturn(int purchaseId,
                                 int productId,
                                 int returnQuantity,
                                 String reason,
                                 String remark) throws SQLException {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = DB.getConnection();

            if (conn == null) {
                throw new SQLException("数据库连接失败");
            }

            String sql = "{CALL sp_purchase_return_tx(?, ?, ?, ?, ?)}";
            cs = conn.prepareCall(sql);
            cs.setInt(1, purchaseId);
            cs.setInt(2, productId);
            cs.setInt(3, returnQuantity);
            cs.setString(4, reason);
            cs.setString(5, remark);
            cs.execute();

            // 当前存储过程没有返回 return_id，返回 1 表示执行成功。
            return 1;

        } finally {
            DB.close(null, cs, conn);
        }
    }

    /**
     * 查询全部采购退货记录。
     * 数据来源：v_purchase_return_detail。
     */
    public ArrayList<PurchaseReturnDetail> findAllPurchaseReturnDetail() {
        ArrayList<PurchaseReturnDetail> list = new ArrayList<>();

        String sql =
                "SELECT * " +
                        "FROM v_purchase_return_detail " +
                        "ORDER BY return_id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapPurchaseReturnDetail(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 采购退货历史 LIKE 模糊搜索。
     */
    public ArrayList<PurchaseReturnDetail> searchPurchaseReturnDetail(String keyword) {
        ArrayList<PurchaseReturnDetail> list = new ArrayList<>();

        String sql = "{CALL sp_like_purchase_return(?)}";

        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            if (keyword == null) {
                keyword = "";
            }

            conn = DB.getConnection();
            cs = conn.prepareCall(sql);
            cs.setString(1, keyword.trim());
            rs = cs.executeQuery();

            while (rs.next()) {
                list.add(mapPurchaseReturnDetail(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, cs, conn);
        }

        return list;
    }

    /**
     * 采购退货 FULLTEXT 全文搜索。
     * 用于展示高阶数据库技术。
     */
    public ArrayList<PurchaseReturnDetail> searchPurchaseReturnDetailFulltext(String keyword) {
        ArrayList<PurchaseReturnDetail> list = new ArrayList<>();

        String sql = "{CALL sp_fulltext_purchase_return(?)}";

        Connection conn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            if (keyword == null) {
                keyword = "";
            }

            conn = DB.getConnection();
            cs = conn.prepareCall(sql);
            cs.setString(1, keyword.trim());
            rs = cs.executeQuery();

            while (rs.next()) {
                list.add(mapPurchaseReturnDetail(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, cs, conn);
        }

        return list;
    }

    /**
     * 查询可退货采购记录，支持关键字搜索和分页。
     *
     * 数据来源：v_purchase_returnable_detail
     *
     * 只显示：
     * 1. 已入库 / 部分入库 / 部分退货的采购记录。
     * 2. 可退货数量 > 0 的采购记录。
     */
    public ArrayList<Map<String, Object>> findReturnablePurchaseList(String keyword,
                                                                     int page,
                                                                     int pageSize) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();

        if (page < 1) {
            page = 1;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;
        String likeKeyword = buildLikeKeyword(keyword);

        String sql =
                "SELECT * " +
                        "FROM v_purchase_returnable_detail " +
                        "WHERE supplier_name LIKE ? " +
                        "   OR product_name LIKE ? " +
                        "   OR product_code LIKE ? " +
                        "   OR CAST(purchase_id AS CHAR) LIKE ? " +
                        "ORDER BY purchase_date DESC, purchase_id DESC, product_id ASC " +
                        "LIMIT ? OFFSET ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setString(3, likeKeyword);
            ps.setString(4, likeKeyword);
            ps.setInt(5, pageSize);
            ps.setInt(6, offset);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapReturnablePurchase(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 统计可退货采购记录总数，用于分页。
     */
    public int countReturnablePurchaseList(String keyword) {
        int count = 0;

        String likeKeyword = buildLikeKeyword(keyword);

        String sql =
                "SELECT COUNT(*) AS total_count " +
                        "FROM v_purchase_returnable_detail " +
                        "WHERE supplier_name LIKE ? " +
                        "   OR product_name LIKE ? " +
                        "   OR product_code LIKE ? " +
                        "   OR CAST(purchase_id AS CHAR) LIKE ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setString(3, likeKeyword);
            ps.setString(4, likeKeyword);

            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total_count");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, ps, conn);
        }

        return count;
    }

    /**
     * 把采购退货历史 ResultSet 转换为 PurchaseReturnDetail。
     */
    private PurchaseReturnDetail mapPurchaseReturnDetail(ResultSet rs) throws SQLException {
        PurchaseReturnDetail detail = new PurchaseReturnDetail();

        detail.setReturnId(rs.getInt("return_id"));
        detail.setPurchaseId(rs.getInt("purchase_id"));

        if (rs.getTimestamp("purchase_date") != null) {
            detail.setPurchaseDate(rs.getTimestamp("purchase_date").toLocalDateTime());
        }

        detail.setPurchaseStatus(rs.getString("purchase_status"));
        detail.setSupplierName(rs.getString("supplier_name"));
        detail.setProductId(rs.getInt("product_id"));
        detail.setProductName(rs.getString("product_name"));
        detail.setReturnQuantity(rs.getInt("return_quantity"));

        if (rs.getTimestamp("return_time") != null) {
            detail.setReturnTime(rs.getTimestamp("return_time").toLocalDateTime());
        }

        detail.setReason(rs.getString("reason"));
        detail.setRemark(rs.getString("remark"));

        return detail;
    }

    /**
     * 把可退货采购记录 ResultSet 转换为 Map。
     */
    private Map<String, Object> mapReturnablePurchase(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();

        map.put("purchaseId", rs.getInt("purchase_id"));
        map.put("purchaseDate", rs.getTimestamp("purchase_date"));
        map.put("status", rs.getString("status"));
        map.put("supplierId", rs.getInt("supplier_id"));
        map.put("supplierName", rs.getString("supplier_name"));
        map.put("productId", rs.getInt("product_id"));
        map.put("productCode", rs.getString("product_code"));
        map.put("productName", rs.getString("product_name"));
        map.put("unitPrice", rs.getBigDecimal("unit_price"));
        map.put("stockedInQuantity", rs.getInt("stocked_in_quantity"));
        map.put("returnedQuantity", rs.getInt("returned_quantity"));
        map.put("returnableQuantity", rs.getInt("returnable_quantity"));
        map.put("remark", rs.getString("remark"));

        return map;
    }

    private String buildLikeKeyword(String keyword) {
        if (keyword == null) {
            keyword = "";
        }

        return "%" + keyword.trim() + "%";
    }
}