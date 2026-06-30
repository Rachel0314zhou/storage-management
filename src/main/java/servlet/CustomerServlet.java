package servlet;

import dao.CustomerDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    private final CustomerDao dao = new CustomerDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            dao.deleteCustomer(id);
            resp.sendRedirect(req.getContextPath() + "/customer");
            return;
        }

        if ("edit".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Map<String, Object> customer = dao.findById(id);
            req.setAttribute("customer", customer);
            req.getRequestDispatcher("/editCustomer.jsp").forward(req, resp);
            return;
        }

        List<Map<String, Object>> list = dao.findAll();
        req.setAttribute("list", list);
        req.getRequestDispatcher("/customer.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("add".equals(action)) {
            String name = req.getParameter("customer_name");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            dao.addCustomer(name, phone, address);
            resp.sendRedirect(req.getContextPath() + "/customer");
            return;
        }

        if ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("customer_id"));
            String name = req.getParameter("customer_name");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            dao.updateCustomer(id, name, phone, address);
            resp.sendRedirect(req.getContextPath() + "/customer");
        }
    }
}
