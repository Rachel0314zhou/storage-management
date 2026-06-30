package servlet;

import dao.SaleDao;
import db.DB;
import model.SaleDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaleServlet extends HttpServlet {

    private final SaleDao saleDao = new SaleDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        String keyword = request.getParameter("keyword");

        ArrayList<SaleDetail> saleList;

        try {
            if ("search".equals(action)) {
                saleList = saleDao.searchSaleDetail(keyword);
                request.setAttribute("keyword", keyword);
                request.setAttribute("pageTitle", "销售搜索结果");
            } else {
                saleList = saleDao.findAllSaleDetail();
                request.setAttribute("pageTitle", "销售出库明细");
            }

            request.setAttribute("saleList", saleList);

            String success = request.getParameter("success");
            if ("1".equals(success)) {
                request.setAttribute("successMessage", "销售出库成功，库存已由触发器自动扣减。");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "销售记录查询失败：" + e.getMessage());
            request.setAttribute("saleList", new ArrayList<SaleDetail>());
        }

        request.getRequestDispatcher("/sale.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            String customerName = request.getParameter("customerName");
            String[] productIds = request.getParameterValues("productId");
            String[] quantities = request.getParameterValues("quantity");
            String[] unitPrices = request.getParameterValues("unitPrice");
            String remark = request.getParameter("remark");

            if (customerName == null || customerName.trim().isEmpty()
                    || productIds == null || productIds.length == 0) {
                request.setAttribute("errorMessage", "销售出库失败：客户名称和至少一个商品不能为空。");
                request.setAttribute("saleList", saleDao.findAllSaleDetail());
                request.getRequestDispatcher("/sale.jsp").forward(request, response);
                return;
            }

            int customerId = getCustomerIdByName(customerName.trim());

            // 使用 SaleDao 的 SaleItem
            java.util.List<dao.SaleDao.SaleItem> items = new java.util.ArrayList<>();
            for (int i = 0; i < productIds.length; i++) {
                if (productIds[i] != null && !productIds[i].trim().isEmpty()) {
                    dao.SaleDao.SaleItem item = new dao.SaleDao.SaleItem();
                    item.setProductId(Integer.parseInt(productIds[i].trim()));
                    item.setQuantity(Integer.parseInt(quantities[i].trim()));
                    item.setUnitPrice(new BigDecimal(unitPrices[i].trim()));
                    items.add(item);
                }
            }

            if (items.isEmpty()) {
                request.setAttribute("errorMessage", "销售出库失败：至少需要一个有效商品。");
                request.setAttribute("saleList", saleDao.findAllSaleDetail());
                request.getRequestDispatcher("/sale.jsp").forward(request, response);
                return;
            }

            saleDao.addMultiSaleStockOut(customerId, items, remark);

            response.sendRedirect(request.getContextPath() + "/sale?success=1");

        } catch (SQLException e) {
            e.printStackTrace();
            String message = e.getMessage();
            if (message != null && message.contains("库存不足")) {
                request.setAttribute("errorMessage", "销售出库失败：" + message);
            } else {
                request.setAttribute("errorMessage", "销售出库失败：" + message);
            }
            request.setAttribute("saleList", saleDao.findAllSaleDetail());
            request.getRequestDispatcher("/sale.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "系统错误：" + e.getMessage());
            request.setAttribute("saleList", saleDao.findAllSaleDetail());
            request.getRequestDispatcher("/sale.jsp").forward(request, response);
        }
    }

    private int parseInt(String value, int defaultValue) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int getCustomerIdByName(String customerName) {
        int customerId = 0;
        String sql = "SELECT customer_id FROM customer WHERE customer_name = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, customerName);
            rs = ps.executeQuery();
            if (rs.next()) {
                customerId = rs.getInt("customer_id");
            } else {
                String insertSql = "INSERT INTO customer (customer_name) VALUES (?)";
                PreparedStatement psInsert = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
                psInsert.setString(1, customerName);
                psInsert.executeUpdate();
                ResultSet rsInsert = psInsert.getGeneratedKeys();
                if (rsInsert.next()) {
                    customerId = rsInsert.getInt(1);
                }
                psInsert.close();
                rsInsert.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.close(rs, ps, conn);
        }
        return customerId;
    }

    private BigDecimal parseBigDecimal(String value, BigDecimal defaultValue) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}