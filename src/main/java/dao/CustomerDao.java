package dao;

import db.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDao {

    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT customer_id, customer_name, phone, address FROM customer ORDER BY customer_id DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("customer_id", rs.getInt("customer_id"));
                map.put("customer_name", rs.getString("customer_name"));
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
        String sql = "SELECT customer_id, customer_name, phone, address FROM customer WHERE customer_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    map.put("customer_id", rs.getInt("customer_id"));
                    map.put("customer_name", rs.getString("customer_name"));
                    map.put("phone", rs.getString("phone"));
                    map.put("address", rs.getString("address"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public void addCustomer(String name, String phone, String address) {
        String sql = "INSERT INTO customer(customer_name, phone, address) VALUES (?,?,?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, address);
            int result = ps.executeUpdate();
            System.out.println("添加客户成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer(int id, String name, String phone, String address) {
        String sql = "UPDATE customer SET customer_name=?, phone=?, address=? WHERE customer_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setInt(4, id);
            int result = ps.executeUpdate();
            System.out.println("修改客户成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(int id) {
        String sql = "DELETE FROM customer WHERE customer_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int result = ps.executeUpdate();
            System.out.println("删除客户成功行数：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
