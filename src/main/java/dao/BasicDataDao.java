package dao;

import db.DB;
import model.Product;
import model.Supplier;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 基础数据 DAO
 * 负责查询供应商、商品、仓库等下拉框数据。
 *
 * 当前数据库说明：
 * 1. product 表没有 specification 字段，使用 description 兼容原来的规格字段。
 * 2. 当前公共库存设计已经取消 warehouse 表和 warehouse_id。
 * 3. 为了兼容旧 JSP / Servlet，findAllWarehouses() 返回一个虚拟仓库对象。
 */
public class BasicDataDao {

    /**
     * 查询启用状态的供应商
     */
    public ArrayList<Supplier> findAllSuppliers() {
        ArrayList<Supplier> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "supplier_id, " +
                        "supplier_name, " +
                        "contact_name, " +
                        "phone, " +
                        "address, " +
                        "status, " +
                        "created_at, " +
                        "updated_at " +
                        "FROM supplier " +
                        "WHERE status = 1 " +
                        "ORDER BY supplier_id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Supplier supplier = new Supplier();

                supplier.setSupplierId(rs.getInt("supplier_id"));
                supplier.setSupplierName(rs.getString("supplier_name"));
                supplier.setContactName(rs.getString("contact_name"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));

                if (rs.getTimestamp("created_at") != null) {
                    supplier.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }

                list.add(supplier);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 查询启用状态的商品
     * <p>
     * 当前 product 表没有 specification 字段。
     * 为了兼容原来的 Product 模型和 JSP 页面，这里把 description 起别名为 specification。
     */
    public ArrayList<Product> findAllProducts() {
        ArrayList<Product> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "product_id, " +
                        "category_id, " +
                        "supplier_id, " +
                        "product_code, " +
                        "product_name, " +
                        "description AS specification, " +
                        "description, " +
                        "unit, " +
                        "purchase_price, " +
                        "sale_price, " +
                        "stock_lower_limit, " +
                        "stock_upper_limit, " +
                        "status, " +
                        "created_at, " +
                        "updated_at " +
                        "FROM product " +
                        "WHERE status = 1 " +
                        "ORDER BY product_id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Product product = new Product();

                product.setProductId(rs.getInt("product_id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setSupplierId(rs.getInt("supplier_id"));
                product.setProductCode(rs.getString("product_code"));
                product.setProductName(rs.getString("product_name"));

                // 当前数据库没有 specification 字段，用 description 兼容旧页面。
                product.setSpecification(rs.getString("specification"));

                product.setDescription(rs.getString("description"));
                product.setUnit(rs.getString("unit"));
                product.setPurchasePrice(rs.getBigDecimal("purchase_price"));
                product.setSalePrice(rs.getBigDecimal("sale_price"));
                product.setStatus(rs.getInt("status"));

                list.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return list;
    }
    /**
     * 校验商品是否属于指定供应商。
     * 用于采购订单提交时做后端防护，避免前端被绕过后提交错误的 supplierId + productId 组合。
     */
    public boolean isProductBelongToSupplier(int productId, int supplierId) {
        String sql =
                "SELECT COUNT(*) " +
                        "FROM product " +
                        "WHERE product_id = ? " +
                        "  AND supplier_id = ? " +
                        "  AND status = 1";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            ps.setInt(2, supplierId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return false;
    }

}
