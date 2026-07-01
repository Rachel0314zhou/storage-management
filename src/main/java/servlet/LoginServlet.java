package servlet;

import dao.UserDao;
import dao.RoleDao;
import model.User;
import model.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final RoleDao roleDao = new RoleDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 如果已经登录，直接跳转到库存首页
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/inventory");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 1. 根据用户名查询用户
        User user = userDao.findByUsername(username);

        if (user == null) {
            req.setAttribute("error", "用户名不存在");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 2. 检查用户状态
        if (user.getStatus() == 0) {
            req.setAttribute("error", "账户已被禁用，请联系管理员");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 3. 校验密码（课设阶段：明文比对；生产环境应使用加密）
        if (!password.equals(user.getPassword())) {
            req.setAttribute("error", "密码错误");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 4. 查询角色名称
        Role role = roleDao.findById(user.getRoleId());
        if (role == null) {
            req.setAttribute("error", "用户角色不存在，请检查数据库");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // 5. 将用户信息存入 Session
        user.setRoleName(role.getRoleName());
        HttpSession session = req.getSession();
        session.setAttribute("currentUser", user);

        // 6. 登录成功，跳转到库存首页
        resp.sendRedirect(req.getContextPath() + "/inventory");
    }
}