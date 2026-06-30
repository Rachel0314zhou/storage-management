package dao;

import db.DB;
import java.sql.*;
import java.util.*;

public class ProductDao {

    private Map<String, Object> buildProductMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();

        map.put("product_id", rs.getInt("product_id"));
        map.put("product_code", rs.getString("product_code"));
        map.put("product_name", rs.getString("product_name"));
        map.put("unit", rs.getString("unit"));
        map.put("purchase_price", rs.getDouble("purchase_price"));
        map.put("sale_price", rs.getDouble("sale_price"));
        map.put("updated_at", rs.getTimestamp("updated_at"));
        map.put("category_name", rs.getString("category_name"));
        map.put("supplier_name", rs.getString("supplier_name"));

        return map;
    }

    // ===================== 查询商品 =====================
    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql = "SELECT p.product_id, p.product_code, p.product_name, p.unit, " +
                "p.purchase_price, p.sale_price, p.updated_at, c.category_name, s.supplier_name " +
                "FROM product p " +
                "LEFT JOIN product_category c ON p.category_id = c.category_id " +
                "LEFT JOIN supplier s ON p.supplier_id = s.supplier_id " +
                "ORDER BY p.product_id DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(buildProductMap(rs));
            }

            System.out.println("查询商品数量：" + list.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===================== 条件查询商品 =====================
    public List<Map<String, Object>> searchProducts(String productCode,
                                                    String productName,
                                                    String supplierId,
                                                    String categoryId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.product_id, p.product_code, p.product_name, p.unit, ");
        sql.append("p.purchase_price, p.sale_price, p.updated_at, c.category_name, s.supplier_name ");
        sql.append("FROM product p ");
        sql.append("LEFT JOIN product_category c ON p.category_id = c.category_id ");
        sql.append("LEFT JOIN supplier s ON p.supplier_id = s.supplier_id ");
        sql.append("WHERE 1=1 ");

        if (productCode != null && !"".equals(productCode.trim())) {
            sql.append("AND p.product_code = ? ");
            params.add(productCode.trim());
        }

        if (productName != null && !"".equals(productName.trim())) {
            sql.append("AND p.product_name = ? ");
            params.add(productName.trim());
        }

        if (supplierId != null && !"".equals(supplierId.trim())) {
            sql.append("AND p.supplier_id = ? ");
            params.add(Integer.parseInt(supplierId.trim()));
        }

        if (categoryId != null && !"".equals(categoryId.trim())) {
            sql.append("AND p.category_id = ? ");
            params.add(Integer.parseInt(categoryId.trim()));
        }

        sql.append("ORDER BY p.product_id DESC");

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildProductMap(rs));
                }
            }

            System.out.println("条件查询商品数量：" + list.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===================== 添加商品（修改为返回 boolean） =====================
    public boolean addProduct(String code, String name, String categoryId,
                              String supplierId, String purchasePrice, String salePrice) {

        String sql = "INSERT INTO product(product_code, product_name, category_id, supplier_id, purchase_price, sale_price) VALUES (?,?,?,?,?,?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 检查连接是否正常
            if (conn == null) {
                System.err.println("❌ 数据库连接失败，无法插入商品");
                return false;
            }

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, Integer.parseInt(categoryId));
            ps.setInt(4, Integer.parseInt(supplierId));
            ps.setDouble(5, Double.parseDouble(purchasePrice));
            ps.setDouble(6, Double.parseDouble(salePrice));

            int result = ps.executeUpdate();
            System.out.println("✅ 插入成功行数：" + result);
            return result > 0;

        } catch (SQLException e) {
            // 打印详细的SQL异常信息（包括外键约束错误）
            System.err.println("❌ SQL异常：");
            e.printStackTrace();
            // 如果是外键约束错误，给出更明确的提示
            String msg = e.getMessage();
            if (msg != null && msg.contains("foreign key constraint")) {
                System.err.println("⚠️ 请检查 category_id 和 supplier_id 是否在对应表中存在有效值！");
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ 添加商品异常：");
            e.printStackTrace();
            return false;
        }
    }

    // ===================== 删除商品 =====================
    public boolean deleteProduct(int id) {

        String sql = "DELETE FROM product WHERE product_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("❌ 数据库连接失败，无法删除商品");
                return false;
            }

            ps.setInt(1, id);

            int result = ps.executeUpdate();
            System.out.println("✅ 删除成功行数：" + result);
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String,Object> findById(int id){

        Map<String,Object> map = new HashMap<>();

        String sql = "SELECT * FROM product WHERE product_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("❌ 数据库连接失败，无法查询商品");
                return map;
            }

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if(rs.next()){
                    map.put("product_id", rs.getInt("product_id"));
                    map.put("product_code", rs.getString("product_code"));
                    map.put("product_name", rs.getString("product_name"));
                    map.put("category_id", rs.getInt("category_id"));
                    map.put("supplier_id", rs.getInt("supplier_id"));
                    map.put("purchase_price", rs.getDouble("purchase_price"));
                    map.put("sale_price", rs.getDouble("sale_price"));
                }
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return map;
    }

    public boolean updateProduct(int id, String code, String name,
                                 String categoryId,
                                 String supplierId,
                                 String purchasePrice,
                                 String salePrice) {

        String sql = "UPDATE product SET product_code=?, product_name=?, category_id=?, supplier_id=?, purchase_price=?, sale_price=? WHERE product_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("❌ 数据库连接失败，无法更新商品");
                return false;
            }

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, Integer.parseInt(categoryId));
            ps.setInt(4, Integer.parseInt(supplierId));
            ps.setDouble(5, Double.parseDouble(purchasePrice));
            ps.setDouble(6, Double.parseDouble(salePrice));
            ps.setInt(7, id);

            int result = ps.executeUpdate();

            System.out.println("✅ 更新成功：" + result);
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}