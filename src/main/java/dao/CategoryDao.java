package dao;

import db.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDao {

    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name, description FROM product_category ORDER BY category_id DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("category_id", rs.getInt("category_id"));
                map.put("category_name", rs.getString("category_name"));
                map.put("description", rs.getString("description"));
                list.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Map<String, Object> findById(int id) {
        Map<String, Object> map = new HashMap<>();
        String sql = "SELECT category_id, category_name, description FROM product_category WHERE category_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    map.put("category_id", rs.getInt("category_id"));
                    map.put("category_name", rs.getString("category_name"));
                    map.put("description", rs.getString("description"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public void addCategory(String name, String description) {
        String sql = "INSERT INTO product_category(category_name, description) VALUES (?, ?)";

        try (Connection conn = DB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            int result = ps.executeUpdate();
            System.out.println("添加分类成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCategory(int id, String name, String description) {
        String sql = "UPDATE product_category SET category_name=?, description=? WHERE category_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, id);
            int result = ps.executeUpdate();
            System.out.println("修改分类成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(int id) {
        String sql = "DELETE FROM product_category WHERE category_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int result = ps.executeUpdate();
            System.out.println("删除分类成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
