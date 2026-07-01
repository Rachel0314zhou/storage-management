package servlet;

import dao.CategoryDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/category")
public class CategoryServlet extends HttpServlet {

    private final CategoryDao dao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        // ===== 删除操作：需要权限校验（只读用户禁止） =====
        if ("delete".equals(action)) {
            // 权限校验：只读用户禁止删除
            HttpSession session = req.getSession(false);
            User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
            if (currentUser == null || currentUser.getRoleId() == 3) {
                resp.sendRedirect(req.getContextPath() + "/403.jsp");
                return;
            }

            int id = Integer.parseInt(req.getParameter("id"));
            dao.deleteCategory(id);
            resp.sendRedirect(req.getContextPath() + "/category");
            return;
        }

        if ("edit".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Map<String, Object> category = dao.findById(id);
            req.setAttribute("category", category);
            req.getRequestDispatcher("/editCategory.jsp").forward(req, resp);
            return;
        }

        List<Map<String, Object>> list = dao.findAll();
        req.setAttribute("list", list);
        req.getRequestDispatcher("/category.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        // ===== 权限校验：只读用户禁止写操作 =====
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || currentUser.getRoleId() == 3) {
            resp.sendRedirect(req.getContextPath() + "/403.jsp");
            return;
        }

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            String name = req.getParameter("category_name");
            String description = req.getParameter("description");
            dao.addCategory(name, description);
            resp.sendRedirect(req.getContextPath() + "/category");
            return;
        }

        if ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("category_id"));
            String name = req.getParameter("category_name");
            String description = req.getParameter("description");
            dao.updateCategory(id, name, description);
            resp.sendRedirect(req.getContextPath() + "/category");
        }
    }
}