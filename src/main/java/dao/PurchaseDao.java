package dao;

import db.DB;
import model.PurchaseDetail;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 采购 DAO
 * 负责采购订单、待入库订单、采购入库明细和采购历史查询。
 *
 * 修改后的采购流程：
 * 1. createPurchaseOrder()：只创建采购订单和采购明细，不增加库存。
 * 2. stockInPurchaseOrder()：根据采购订单办理入库，插入 purchase_stock_in。
 * 3. 库存增加、库存流水生成、采购订单状态更新由数据库触发器自动完成。
 */
public class PurchaseDao {

    /**
     * 第一步：创建采购订单。
     * 只插入 purchase_order 和 purchase_order_item。
     * 不插入 purchase_stock_in，不改变 inventory。
     */
    public void createPurchaseOrder(int supplierId,
                                    int productId,
                                    int quantity,
                                    BigDecimal unitPrice,
                                    String remark) throws SQLException {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = DB.getConnection();

            if (conn == null) {
                throw new SQLException("数据库连接失败");
            }

            String sql = "{CALL sp_create_purchase_order_tx(?, ?, ?, ?, ?)}";
            cs = conn.prepareCall(sql);
            cs.setInt(1, supplierId);
            cs.setInt(2, productId);
            cs.setInt(3, quantity);
            cs.setBigDecimal(4, unitPrice);
            cs.setString(5, remark);
            cs.execute();

        } finally {
            DB.close(null, cs, conn);
        }
    }

    /**
     * 第二步：办理采购入库。
     * 只插入 purchase_stock_in。
     * 插入后由数据库触发器自动增加库存、生成库存流水、更新订单状态。
     */
    public void stockInPurchaseOrder(int purchaseId,
                                     int productId,
                                     int stockInQuantity,
                                     String remark) throws SQLException {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = DB.getConnection();

            if (conn == null) {
                throw new SQLException("数据库连接失败");
            }

            String sql = "{CALL sp_purchase_stock_in_tx(?, ?, ?, ?)}";
            cs = conn.prepareCall(sql);
            cs.setInt(1, purchaseId);
            cs.setInt(2, productId);
            cs.setInt(3, stockInQuantity);
            cs.setString(4, remark);
            cs.execute();

        } finally {
            DB.close(null, cs, conn);
        }
    }

    /**
     * 兼容旧代码：如果其他地方仍然调用 addPurchaseStockIn，
     * 这里仍然保留“一步下单并入库”的旧逻辑。
     * 新的采购页面不再调用这个方法。
     */
    public void addPurchaseStockIn(int supplierId,
                                   int productId,
                                   int quantity,
                                   BigDecimal unitPrice,
                                   String remark) throws SQLException {
        Connection conn = null;
        CallableStatement cs = null;

        try {
            conn = DB.getConnection();

            if (conn == null) {
                throw new SQLException("数据库连接失败");
            }

            String sql = "{CALL sp_create_purchase_order_stock_in_tx(?, ?, ?, ?, ?)}";
            cs = conn.prepareCall(sql);
            cs.setInt(1, supplierId);
            cs.setInt(2, productId);
            cs.setInt(3, quantity);
            cs.setBigDecimal(4, unitPrice);
            cs.setString(5, remark);
            cs.execute();

        } finally {
            DB.close(null, cs, conn);
        }
    }

    /**
     * 查询全部采购订单明细。
     * 数据来源：v_purchase_detail。
     */
    public ArrayList<PurchaseDetail> findAllPurchaseDetail() {
        ArrayList<PurchaseDetail> list = new ArrayList<>();

        String sql =
                "SELECT * " +
                        "FROM v_purchase_detail " +
                        "ORDER BY purchase_id DESC, purchase_item_id ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapPurchaseDetail(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 采购订单历史查询。
     */
    public ArrayList<PurchaseDetail> searchPurchaseDetail(String keyword) {
        ArrayList<PurchaseDetail> list = new ArrayList<>();

        String sql = "{CALL sp_like_purchase_search(?)}";

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
                list.add(mapPurchaseDetail(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, cs, conn);
        }

        return list;
    }

    /**
     * 查询待入库采购订单。
     * 数据来源：v_purchase_pending_stock_in。
     */
    public ArrayList<Map<String, Object>> findPendingPurchaseOrders() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();

        String sql =
                "SELECT * " +
                        "FROM v_purchase_pending_stock_in " +
                        "ORDER BY purchase_id DESC, purchase_item_id ASC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapPendingPurchaseOrder(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 查询全部采购入库明细，也就是进货单明细。
     * 数据来源：v_purchase_stock_in_detail。
     */
    public ArrayList<Map<String, Object>> findAllStockInDetail() {
        ArrayList<Map<String, Object>> list = new ArrayList<>();

        String sql =
                "SELECT * " +
                        "FROM v_purchase_stock_in_detail " +
                        "ORDER BY stock_in_id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapStockInDetail(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 查询采购入库明细。
     */
    public ArrayList<Map<String, Object>> searchStockInDetail(String keyword) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();

        String sql =
                "SELECT * " +
                        "FROM v_purchase_stock_in_detail " +
                        "WHERE supplier_name LIKE ? " +
                        "   OR product_name LIKE ? " +
                        "   OR remark LIKE ? " +
                        "ORDER BY stock_in_id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (keyword == null) {
                keyword = "";
            }

            String likeKeyword = "%" + keyword.trim() + "%";

            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setString(3, likeKeyword);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapStockInDetail(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 把 ResultSet 转换为 PurchaseDetail 对象。
     */
    private PurchaseDetail mapPurchaseDetail(ResultSet rs) throws SQLException {
        PurchaseDetail detail = new PurchaseDetail();

        detail.setPurchaseId(rs.getInt("purchase_id"));

        if (rs.getTimestamp("purchase_date") != null) {
            detail.setPurchaseDate(rs.getTimestamp("purchase_date").toLocalDateTime());
        }

        detail.setStatus(rs.getString("status"));
        detail.setSupplierName(rs.getString("supplier_name"));
        detail.setPurchaseItemId(rs.getInt("purchase_item_id"));
        detail.setProductName(rs.getString("product_name"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setUnitPrice(rs.getBigDecimal("unit_price"));
        detail.setAmount(rs.getBigDecimal("amount"));
        detail.setRemark(rs.getString("remark"));

        return detail;
    }

    /**
     * 把待入库视图结果转换成 Map。
     */
    private Map<String, Object> mapPendingPurchaseOrder(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();

        map.put("purchaseId", rs.getInt("purchase_id"));
        map.put("purchaseDate", rs.getTimestamp("purchase_date"));
        map.put("status", rs.getString("status"));
        map.put("remark", rs.getString("remark"));
        map.put("supplierId", rs.getInt("supplier_id"));
        map.put("supplierName", rs.getString("supplier_name"));
        map.put("purchaseItemId", rs.getInt("purchase_item_id"));
        map.put("productId", rs.getInt("product_id"));
        map.put("productCode", rs.getString("product_code"));
        map.put("productName", rs.getString("product_name"));
        map.put("orderQuantity", rs.getInt("order_quantity"));
        map.put("stockedInQuantity", rs.getInt("stocked_in_quantity"));
        map.put("remainingQuantity", rs.getInt("remaining_quantity"));
        map.put("unitPrice", rs.getBigDecimal("unit_price"));
        map.put("amount", rs.getBigDecimal("amount"));

        return map;
    }

    /**
     * 把采购入库明细视图结果转换成 Map。
     */
    private Map<String, Object> mapStockInDetail(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();

        map.put("stockInId", rs.getInt("stock_in_id"));
        map.put("purchaseId", rs.getInt("purchase_id"));
        map.put("purchaseDate", rs.getTimestamp("purchase_date"));
        map.put("stockInTime", rs.getTimestamp("stock_in_time"));
        map.put("purchaseStatus", rs.getString("purchase_status"));
        map.put("supplierId", rs.getInt("supplier_id"));
        map.put("supplierName", rs.getString("supplier_name"));
        map.put("productId", rs.getInt("product_id"));
        map.put("productCode", rs.getString("product_code"));
        map.put("productName", rs.getString("product_name"));
//        map.put("description", rs.getString("description"));
        map.put("stockInQuantity", rs.getInt("stock_in_quantity"));
        map.put("unitPrice", rs.getBigDecimal("unit_price"));
        map.put("stockInAmount", rs.getBigDecimal("stock_in_amount"));
        map.put("remark", rs.getString("remark"));

        return map;
    }
}