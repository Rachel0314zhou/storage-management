package servlet;

import dao.ProductDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    ProductDao dao = new ProductDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            String id = req.getParameter("id");
            dao.deleteProduct(Integer.parseInt(id));
            //resp.sendRedirect("product");
            resp.sendRedirect(req.getContextPath() + "/product");
            return;
        }

        if ("edit".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));

            Map<String,Object> product = dao.findById(id);

            req.setAttribute("product", product);

            req.getRequestDispatcher("/editProduct.jsp").forward(req, resp);
            return;
        }

        String productCode = req.getParameter("product_code");
        String productName = req.getParameter("product_name");
        String supplierId = req.getParameter("supplier_id");
        String categoryId = req.getParameter("category_id");

        boolean hasSearchCondition =
                (productCode != null && !"".equals(productCode.trim()))
                        || (productName != null && !"".equals(productName.trim()))
                        || (supplierId != null && !"".equals(supplierId.trim()))
                        || (categoryId != null && !"".equals(categoryId.trim()));

        List<Map<String, Object>> list;
        if (hasSearchCondition) {
            list = dao.searchProducts(productCode, productName, supplierId, categoryId);
        } else {
            list = dao.findAll();
        }

        req.setAttribute("list", list);
        req.setAttribute("product_code", productCode);
        req.setAttribute("product_name", productName);
        req.setAttribute("supplier_id", supplierId);
        req.setAttribute("category_id", categoryId);

        req.getRequestDispatcher("/product.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("utf-8");

        String action = req.getParameter("action");

        ProductDao dao = new ProductDao();

        // ================= 新增 =================
        if ("add".equals(action)) {

            String code = req.getParameter("product_code");
            String name = req.getParameter("product_name");
            String categoryId = req.getParameter("category_id");
            String supplierId = req.getParameter("supplier_id");
            String purchasePrice = req.getParameter("purchase_price");
            String salePrice = req.getParameter("sale_price");

            dao.addProduct(code, name, categoryId, supplierId, purchasePrice, salePrice);

            resp.sendRedirect("product");
            return;
        }

        // ================= 修改 =================
        if ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("product_id"));
            String code = req.getParameter("product_code");
            String name = req.getParameter("product_name");
            String categoryId = req.getParameter("category_id");
            String supplierId = req.getParameter("supplier_id");
            String purchasePrice = req.getParameter("purchase_price");
            String salePrice = req.getParameter("sale_price");

            dao.updateProduct(id, code, name, categoryId, supplierId, purchasePrice, salePrice);

            resp.sendRedirect("product");
            return;
        }
    }

}
