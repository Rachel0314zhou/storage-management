package dao;

import db.DB;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 用户 DAO
 *
 * 主要功能：
 * 1. 查询所有用户列表（关联角色表，获取角色名称）
 * 2. 根据用户ID查询单个用户信息
 * 3. 添加新用户
 * 4. 更新用户信息（用户名、角色、状态）
 * 5. 删除用户（物理删除）
 * 6. 检查用户名是否已存在（用于添加时防重校验）
 *
 * 数据来源：
 * system_user 表（系统用户表）
 * role 表（角色字典表，通过 LEFT JOIN 关联获取角色名称）
 *
 * 使用场景：
 * 在 UserServlet 中调用，供 userList.jsp、userAdd.jsp、userEdit.jsp 使用。
 *
 * 注意：
 * 1. 删除用户为物理删除（DELETE），非逻辑删除（软删除）。
 * 2. 用户名在数据库中有 UNIQUE 约束，添加前需先调用 isUsernameExist 校验。
 * 3. 本表与 customer 表无关联，删除用户不影响客户数据。
 */
public class UserDao {

    /**
     * 查询所有用户（带角色名）
     *
     * 数据来源：system_user 表 LEFT JOIN role 表
     * 排序方式：按 user_id 升序排列
     *
     * @return ArrayList<User> 用户列表，每个用户对象包含角色名称，若无数据则返回空列表
     */
    public ArrayList<User> findAll() {
        ArrayList<User> list = new ArrayList<>();

        // LEFT JOIN role 表，获取角色名称，便于页面显示
        String sql = "SELECT u.*, r.role_name FROM system_user u LEFT JOIN role r ON u.role_id = r.role_id ORDER BY u.user_id";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            // 遍历结果集，将每条记录封装为 User 对象
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name")); // 从 role 表关联查询
                user.setStatus(rs.getInt("status"));
                list.add(user);
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
     * 根据用户ID查询用户
     *
     * 使用场景：在编辑用户时，根据 userId 回显用户当前信息
     *
     * @param userId 用户编号
     * @return User 用户对象，如果不存在则返回 null
     */
    public User findById(int userId) {
        String sql = "SELECT * FROM system_user WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRoleId(rs.getInt("role_id"));
                user.setStatus(rs.getInt("status"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }

        return null;
    }

    /**
     * 添加用户
     *
     * 执行流程：
     * 1. 插入 system_user 表（用户名、密码、角色ID、状态）
     * 2. 如果 status 未设置，默认设为 1（启用）
     *
     * 注意：
     * 调用前需先通过 isUsernameExist 校验用户名是否已存在，
     * 否则将触发数据库 UNIQUE 约束异常。
     *
     * @param user 用户对象（必须包含 username、password、roleId）
     * @return boolean 添加成功返回 true，失败返回 false
     */
    public boolean add(User user) {
        String sql = "INSERT INTO system_user (username, password, role_id, status) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getRoleId());
            // 如果 status 未设置，默认设为 1（启用）
            ps.setInt(4, user.getStatus() != null ? user.getStatus() : 1);

            // executeUpdate 返回受影响的行数，大于 0 表示插入成功
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DB.close(ps, conn);
        }
    }

    /**
     * 更新用户信息
     *
     * 更新字段：用户名、角色ID、状态
     *
     * 注意：本方法不更新密码，密码修改应单独提供接口。
     *
     * @param user 用户对象（必须包含 userId、username、roleId、status）
     * @return boolean 更新成功返回 true，失败返回 false
     */
    public boolean update(User user) {
        String sql = "UPDATE system_user SET username = ?, role_id = ?, status = ? WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setInt(2, user.getRoleId());
            ps.setInt(3, user.getStatus());
            ps.setInt(4, user.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DB.close(ps, conn);
        }
    }

    /**
     * 删除用户（物理删除）
     *
     * 执行 DELETE 语句从 system_user 表中移除用户记录。
     *
     * 注意：
     * 1. 本操作不可恢复，建议在调用前进行二次确认。
     * 2. 删除用户不影响 customer（客户）表中的数据。
     *
     * @param userId 用户编号
     * @return boolean 删除成功返回 true，失败返回 false
     */
    public boolean delete(int userId) {
        String sql = "DELETE FROM system_user WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DB.close(ps, conn);
        }
    }

    /**
     * 检查用户名是否已存在
     *
     * 使用场景：在添加用户前调用，防止插入重复用户名。
     * 数据库层面 username 字段已有 UNIQUE 约束，此方法用于提前校验并给出友好提示。
     *
     * @param username 用户名
     * @return boolean 用户名已存在返回 true，不存在返回 false
     */
    public boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM system_user WHERE username = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                // COUNT(*) 结果大于 0 表示用户名已被占用
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