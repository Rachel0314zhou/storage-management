package dao;

import db.DB;
import model.InventoryLogDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 库存流水 DAO
 * 当前数据库已经取消仓库维度。
 */
public class InventoryLogDao {

    /**
     * 查询全部库存流水
     */
    public ArrayList<InventoryLogDetail> findAllLog() {
        ArrayList<InventoryLogDetail> list = new ArrayList<InventoryLogDetail>();

        String sql =
                "SELECT " +
                        "inventory_change_id AS log_id, " +
                        "product_name, " +
                        "change_type, " +
                        "change_type_name, " +
                        "change_quantity, " +
                        "before_quantity, " +
                        "after_quantity, " +
                        "change_type_name AS source_type, " +
                        "source_id, " +
                        "remark, " +
                        "change_time AS created_at " +
                        "FROM v_inventory_change_detail " +
                        "ORDER BY inventory_change_id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapInventoryLogDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 按变动类型查询库存流水。
     * 页面传入：入库 / 出库。
     *
     * 这里不用 change_type = ?，而是用 LIKE，
     * 因为数据库中可能是“采购入库”“销售出库”“采购退货”等更具体的名称。
     */
    public ArrayList<InventoryLogDetail> findLogByChangeType(String changeType) {
        ArrayList<InventoryLogDetail> list = new ArrayList<InventoryLogDetail>();

        String sql =
                "SELECT " +
                        "inventory_change_id AS log_id, " +
                        "product_name, " +
                        "change_type, " +
                        "change_type_name, " +
                        "change_quantity, " +
                        "before_quantity, " +
                        "after_quantity, " +
                        "change_type_name AS source_type, " +
                        "source_id, " +
                        "remark, " +
                        "change_time AS created_at " +
                        "FROM v_inventory_change_detail " +
                        "WHERE change_type LIKE ? " +
                        "   OR change_type_name LIKE ? " +
                        "ORDER BY inventory_change_id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (changeType == null) {
                changeType = "";
            }

            String key = "%" + changeType.trim() + "%";

            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, key);
            ps.setString(2, key);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapInventoryLogDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 搜索库存流水。
     * 支持按商品名称、变动类型、来源ID、备注搜索。
     */
    public ArrayList<InventoryLogDetail> searchLog(String keyword) {
        ArrayList<InventoryLogDetail> list = new ArrayList<InventoryLogDetail>();

        String sql =
                "SELECT " +
                        "inventory_change_id AS log_id, " +
                        "product_name, " +
                        "change_type, " +
                        "change_type_name, " +
                        "change_quantity, " +
                        "before_quantity, " +
                        "after_quantity, " +
                        "change_type_name AS source_type, " +
                        "source_id, " +
                        "remark, " +
                        "change_time AS created_at " +
                        "FROM v_inventory_change_detail " +
                        "WHERE product_name LIKE ? " +
                        "   OR change_type LIKE ? " +
                        "   OR change_type_name LIKE ? " +
                        "   OR IFNULL(remark, '') LIKE ? " +
                        "   OR CAST(source_id AS CHAR) LIKE ? " +
                        "ORDER BY inventory_change_id DESC";

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
            ps.setString(3, key);
            ps.setString(4, key);
            ps.setString(5, key);

            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapInventoryLogDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 把 ResultSet 转换为 InventoryLogDetail 对象。
     * 已删除仓库名称映射。
     */
    private InventoryLogDetail mapInventoryLogDetail(ResultSet rs) throws SQLException {
        InventoryLogDetail detail = new InventoryLogDetail();

        detail.setLogId(rs.getInt("log_id"));
        detail.setProductName(rs.getString("product_name"));
        detail.setChangeType(rs.getString("change_type_name"));
        detail.setChangeQuantity(rs.getInt("change_quantity"));
        detail.setBeforeQuantity(rs.getInt("before_quantity"));
        detail.setAfterQuantity(rs.getInt("after_quantity"));
        detail.setSourceType(rs.getString("source_type"));
        detail.setSourceId(rs.getInt("source_id"));
        detail.setRemark(rs.getString("remark"));

        if (rs.getTimestamp("created_at") != null) {
            detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return detail;
    }
}