package servlet;

import dao.RoleDao;
import dao.UserDao;
import model.Role;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 用户管理控制器（Servlet）
 *
 * 主要功能：
 * 1. 查询用户列表（带角色名称）
 * 2. 跳转到添加用户页面
 * 3. 添加用户（含用户名唯一性校验）
 * 4. 跳转到编辑用户页面
 * 5. 更新用户信息（用户名、角色、状态）
 * 6. 删除用户（物理删除）
 *
 * 数据来源：
 * system_user 表（系统用户表）——通过 UserDao 操作
 * role 表（角色字典表）——通过 RoleDao 操作，用于渲染角色下拉框
 *
 * 注意：
 * 1. system_user 与 customer 表相互独立，删除用户不影响客户数据。
 * 2. 用户名在数据库中有 UNIQUE 约束，添加前先通过 UserDao.isUsernameExist 校验。
 * 3. 密码修改暂未提供单独接口，后续可扩展。
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private UserDao userDao = new UserDao();
    private RoleDao roleDao = new RoleDao();

    /**
     * 统一处理所有请求（GET 和 POST）
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        // ===== 权限校验：仅管理员（roleId=1）可以访问用户管理 =====
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || currentUser.getRoleId() != 1) {
            resp.sendRedirect(req.getContextPath() + "/403.jsp");
            return;
        }

        String action = req.getParameter("action");

        if ("add".equals(action)) {
            doAdd(req, resp);
        } else if ("update".equals(action)) {
            doUpdate(req, resp);
        } else if ("delete".equals(action)) {
            deleteUser(req, resp);
        } else if ("toAdd".equals(action)) {
            toAddPage(req, resp);
        } else if ("toEdit".equals(action)) {
            toEditPage(req, resp);
        } else {
            doList(req, resp);
        }
    }

    // 为确保 GET 和 POST 都能正确处理，也可重写 doGet/doPost 调用 service
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service(req, resp);
    }

    // ---------- 业务方法 ----------

    //查询用户列表，跳转到userList.jsp
    private void doList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ArrayList<User> userList = userDao.findAll();
        req.setAttribute("userList", userList);
        req.getRequestDispatcher("/userList.jsp").forward(req, resp);
    }

    //跳转到用户添加界面(userAdd.jsp)
    private void toAddPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ArrayList<Role> roleList = roleDao.findAll();
        req.setAttribute("roleList", roleList);
        req.getRequestDispatcher("/userAdd.jsp").forward(req, resp);
    }

    //添加用户：校验用户名是否存在，存在则转发回页面显示错误，否则插入数据库
    private void doAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        int roleId = Integer.parseInt(req.getParameter("roleId"));

        // 1. 检查用户名是否已存在
        if (userDao.isUsernameExist(username)) {
            req.setAttribute("error", "用户名已存在");
            toAddPage(req, resp); // 转发到添加页面，保留 roleList 和错误信息
            return;
        }

        // 2. 组装用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoleId(roleId);
        user.setStatus(1);

        // 3. 执行添加操作
        if (userDao.add(user)) {
            // 添加成功 -> 重定向到列表页（避免刷新页面重复提交）
            resp.sendRedirect(req.getContextPath() + "/user");
        } else {
            // 添加失败 -> 转发并显示错误
            req.setAttribute("error", "添加失败，请重试");
            toAddPage(req, resp);
        }
    }

    //跳转编辑用户页面，预加载用户信息及角色列表，跳转到userEdit.jsp
    private void toEditPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = Integer.parseInt(req.getParameter("id"));
        User user = userDao.findById(userId);
        ArrayList<Role> roleList = roleDao.findAll();
        req.setAttribute("user", user);
        req.setAttribute("roleList", roleList);
        req.getRequestDispatcher("/userEdit.jsp").forward(req, resp);
    }

    //更新用户信息（用户名、角色、状态），成功重定向列表页，失败返回编辑页
    private void doUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = Integer.parseInt(req.getParameter("userId"));
        String username = req.getParameter("username");
        int roleId = Integer.parseInt(req.getParameter("roleId"));
        int status = Integer.parseInt(req.getParameter("status"));

        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setRoleId(roleId);
        user.setStatus(status);

        if (userDao.update(user)) {
            resp.sendRedirect(req.getContextPath() + "/user");
        } else {
            resp.sendRedirect(req.getContextPath() + "/user?action=toEdit&id=" + userId + "&error=更新失败");
        }
    }

    //物理删除用户（不可恢复），成功后重定向列表页
    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = Integer.parseInt(req.getParameter("id"));
        userDao.delete(userId);
        resp.sendRedirect(req.getContextPath() + "/user");
    }
}