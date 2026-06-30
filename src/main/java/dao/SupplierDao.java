package dao;

import db.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierDao {

    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT supplier_id, supplier_name, contact_name, phone, address FROM supplier ORDER BY supplier_id DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("supplier_id", rs.getInt("supplier_id"));
                map.put("supplier_name", rs.getString("supplier_name"));
                map.put("contact_name", rs.getString("contact_name"));
                map.put("phone", rs.getString("phone"));
                map.put("address", rs.getString("address"));
                list.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Map<String, Object> findById(int id) {
        Map<String, Object> map = new HashMap<>();
        String sql = "SELECT supplier_id, supplier_name, contact_name, phone, address FROM supplier WHERE supplier_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    map.put("supplier_id", rs.getInt("supplier_id"));
                    map.put("supplier_name", rs.getString("supplier_name"));
                    map.put("contact_name", rs.getString("contact_name"));
                    map.put("phone", rs.getString("phone"));
                    map.put("address", rs.getString("address"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public void addSupplier(String name, String contactName, String phone, String address) {
        String sql = "INSERT INTO supplier(supplier_name, contact_name, phone, address) VALUES (?,?,?,?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contactName);
            ps.setString(3, phone);
            ps.setString(4, address);
            int result = ps.executeUpdate();
            System.out.println("添加供应商成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSupplier(int id, String name, String contactName, String phone, String address) {
        String sql = "UPDATE supplier SET supplier_name=?, contact_name=?, phone=?, address=? WHERE supplier_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contactName);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setInt(5, id);
            int result = ps.executeUpdate();
            System.out.println("修改供应商成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSupplier(int id) {
        String sql = "DELETE FROM supplier WHERE supplier_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int result = ps.executeUpdate();
            System.out.println("删除供应商成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
