package dao;

import db.DB;
import model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 角色 DAO
 *
 * 主要功能：
 * 1. 查询所有启用状态的角色列表（用于用户管理下拉框）
 * 2. 根据角色ID查询单个角色信息
 *
 * 数据来源：
 * role 表（系统角色字典表）
 *
 * 使用场景：
 * 在用户管理模块中，为用户分配角色时提供角色列表数据。
 * 在 UserServlet 中调用，供 userAdd.jsp 和 userEdit.jsp 渲染角色下拉框。
 */
public class RoleDao {

    /**
     * 查询所有启用状态的角色
     *
     * 数据来源：role 表
     * 过滤条件：status = 1（仅查询启用状态的角色）
     * 排序方式：按 role_id 升序排列
     *
     * @return ArrayList<Role> 启用状态的角色列表，若无数据则返回空列表
     */
    public ArrayList<Role> findAll() {
        ArrayList<Role> list = new ArrayList<>();

        // 只查询启用状态的角色，用于页面下拉框展示
        String sql = "SELECT role_id, role_name, description, status FROM role WHERE status = 1 ORDER BY role_id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            // 遍历结果集，将每条记录封装为 Role 对象
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                role.setStatus(rs.getInt("status"));
                list.add(role);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放数据库资源
            DB.close(rs, ps, conn);
        }

        return list;
    }

    /**
     * 根据角色ID查询角色
     *
     * 使用场景：在编辑用户时，根据用户已有的 roleId 回显对应的角色信息
     *
     * @param roleId 角色编号
     * @return Role 角色对象，如果不存在则返回 null
     */
    public Role findById(int roleId) {
        String sql = "SELECT role_id, role_name, description, status FROM role WHERE role_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roleId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                role.setStatus(rs.getInt("status"));
                return role;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return null;
    }
}