package servlet;

import db.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.math.BigDecimal;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class SalesReturnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        String keyword = request.getParameter("keyword");

        // 处理从 doPost 转发过来的成功/失败消息
        if (request.getParameter("success") != null) {
            request.setAttribute("successMessage", "操作成功！");
        }
        if (request.getParameter("error") != null) {
            request.setAttribute("errorMessage", "操作失败：" + request.getParameter("error"));
        }

        List<Map<String, Object>> returnList = new ArrayList<>();
        List<Map<String, Object>> orderList = new ArrayList<>();

        try (Connection conn = DB.getConnection()) {

            // ========== 1. 查询可退货的商品 ==========
            String orderSql =
                    "SELECT so.sales_order_id, c.customer_name, so.order_date, soi.sales_order_item_id, " +
                            "p.product_id, p.product_name, soi.quantity, soi.unit_price, soi.subtotal, " +
                            "IFNULL(SUM(sr.return_quantity), 0) AS returned_quantity, " +
                            "(soi.quantity - IFNULL(SUM(sr.return_quantity), 0)) AS can_return " +
                            "FROM sales_order so " +
                            "JOIN customer c ON so.customer_id = c.customer_id " +
                            "JOIN sales_order_item soi ON so.sales_order_id = soi.sales_order_id " +
                            "JOIN product p ON soi.product_id = p.product_id " +
                            "LEFT JOIN sales_return sr ON so.sales_order_id = sr.sales_order_id " +
                            "  AND p.product_id = sr.product_id AND sr.status != 3 " +
                            "WHERE so.status = 2 " +
                            "GROUP BY so.sales_order_id, c.customer_name, so.order_date, soi.sales_order_item_id, " +
                            "p.product_id, p.product_name, soi.quantity, soi.unit_price, soi.subtotal " +
                            "HAVING can_return > 0";

            if (keyword != null && !keyword.trim().isEmpty()) {
                orderSql = "SELECT * FROM (" + orderSql + ") t WHERE customer_name LIKE ? OR product_name LIKE ?";
                try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
                    String key = "%" + keyword.trim() + "%";
                    ps.setString(1, key);
                    ps.setString(2, key);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        orderList.add(mapOrderRow(rs));
                    }
                }
            } else {
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery(orderSql);
                    while (rs.next()) {
                        orderList.add(mapOrderRow(rs));
                    }
                }
            }

            // ========== 2. 查询退货记录 ==========
            String returnSql =
                    "SELECT sr.sales_return_id, sr.sales_order_id, c.customer_name, sr.product_id, " +
                            "p.product_name, sr.return_quantity, sr.return_amount, sr.return_date, sr.reason, sr.status " +
                            "FROM sales_return sr " +
                            "JOIN sales_order so ON sr.sales_order_id = so.sales_order_id " +
                            "JOIN customer c ON so.customer_id = c.customer_id " +
                            "JOIN product p ON sr.product_id = p.product_id " +
                            "ORDER BY sr.sales_return_id DESC";

            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(returnSql);
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("sales_return_id", rs.getInt("sales_return_id"));
                    row.put("sales_order_id", rs.getInt("sales_order_id"));
                    row.put("customer_name", rs.getString("customer_name"));
                    row.put("product_id", rs.getInt("product_id"));
                    row.put("product_name", rs.getString("product_name"));
                    row.put("return_quantity", rs.getInt("return_quantity"));
                    row.put("return_amount", rs.getBigDecimal("return_amount"));
                    row.put("return_date", rs.getTimestamp("return_date"));
                    row.put("reason", rs.getString("reason"));
                    int status = rs.getInt("status");
                    row.put("status", status);
                    String statusText;
                    if (status == 1) statusText = "待审核";
                    else if (status == 2) statusText = "已通过";
                    else if (status == 3) statusText = "已驳回";
                    else if (status == 4) statusText = "已入库";
                    else if (status == 5) statusText = "已作废";
                    else statusText = "未知";
                    row.put("status_text", statusText);
                    returnList.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", e.getMessage());
        }

        request.setAttribute("orderList", orderList);
        request.setAttribute("returnList", returnList);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/salesReturn.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ===== 权限校验：只读用户禁止写操作 =====
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || currentUser.getRoleId() == 3) {
            response.sendRedirect(request.getContextPath() + "/403.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            // ========== 新增退货申请 ==========
            if ("add".equals(action)) {
                int salesOrderId = Integer.parseInt(request.getParameter("salesOrderId"));
                int productId = Integer.parseInt(request.getParameter("productId"));
                int returnQuantity = Integer.parseInt(request.getParameter("returnQuantity"));
                String reason = request.getParameter("reason");

                try (Connection conn = DB.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO sales_return (sales_order_id, product_id, return_quantity, return_amount, reason) " +
                                     "SELECT ?, ?, ?, sale_price * ?, ? FROM product WHERE product_id = ?")) {

                    ps.setInt(1, salesOrderId);
                    ps.setInt(2, productId);
                    ps.setInt(3, returnQuantity);
                    ps.setInt(4, returnQuantity);
                    ps.setString(5, reason);
                    ps.setInt(6, productId);
                    ps.executeUpdate();
                }
                response.sendRedirect(request.getContextPath() + "/salesReturn?success=1");
                return;
            }

            // ========== 审核通过 ==========
            if ("approve".equals(action)) {
                int returnId = Integer.parseInt(request.getParameter("returnId"));
                try (Connection conn = DB.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE sales_return SET status = 2 WHERE sales_return_id = ?")) {
                    ps.setInt(1, returnId);
                    ps.executeUpdate();
                }
                response.sendRedirect(request.getContextPath() + "/salesReturn?success=1");
                return;
            }

            // ========== 审核驳回 ==========
            if ("reject".equals(action)) {
                int returnId = Integer.parseInt(request.getParameter("returnId"));
                try (Connection conn = DB.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE sales_return SET status = 3 WHERE sales_return_id = ?")) {
                    ps.setInt(1, returnId);
                    ps.executeUpdate();
                }
                response.sendRedirect(request.getContextPath() + "/salesReturn?success=1");
                return;
            }

            // ========== 确认入库（增加库存） ==========
            if ("confirm".equals(action)) {
                int returnId = Integer.parseInt(request.getParameter("returnId"));
                try (Connection conn = DB.getConnection()) {
                    conn.setAutoCommit(false);

                    // 1. 查询退货信息
                    String querySql =
                            "SELECT sr.product_id, sr.return_quantity, sr.return_amount, sr.sales_order_id, " +
                                    "p.sale_price, p.purchase_price " +
                                    "FROM sales_return sr JOIN product p ON sr.product_id = p.product_id " +
                                    "WHERE sr.sales_return_id = ? AND sr.status = 2";
                    int productId = 0;
                    int returnQuantity = 0;
                    BigDecimal returnAmount = BigDecimal.ZERO;
                    BigDecimal salePrice = BigDecimal.ZERO;
                    int salesOrderId = 0;

                    try (PreparedStatement ps = conn.prepareStatement(querySql)) {
                        ps.setInt(1, returnId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            productId = rs.getInt("product_id");
                            returnQuantity = rs.getInt("return_quantity");
                            returnAmount = rs.getBigDecimal("return_amount");
                            salePrice = rs.getBigDecimal("sale_price");
                            salesOrderId = rs.getInt("sales_order_id");
                        } else {
                            throw new SQLException("退货记录不存在或状态不是已通过");
                        }
                    }

                    // 2. 获取当前库存
                    int beforeQuantity = 0;
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT quantity FROM inventory WHERE product_id = ?")) {
                        ps.setInt(1, productId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            beforeQuantity = rs.getInt("quantity");
                        }
                    }

                    int afterQuantity = beforeQuantity + returnQuantity;

                    // 3. 更新库存
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE inventory SET quantity = quantity + ? WHERE product_id = ?")) {
                        ps.setInt(1, returnQuantity);
                        ps.setInt(2, productId);
                        ps.executeUpdate();
                    }

                    // 4. 写入库存变动记录 (change_type = 3 表示销售退货入库)
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO inventory_change (product_id, change_type, change_quantity, " +
                                    "before_quantity, after_quantity, source_id, remark) VALUES (?, 3, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, productId);
                        ps.setInt(2, returnQuantity);
                        ps.setInt(3, beforeQuantity);
                        ps.setInt(4, afterQuantity);
                        ps.setInt(5, returnId);
                        ps.setString(6, "销售退货入库，退货单ID: " + returnId);
                        ps.executeUpdate();
                    }

                    // 5. 更新退货单状态为“已入库”（用 status = 4 表示已入库）
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE sales_return SET status = 4 WHERE sales_return_id = ?")) {
                        ps.setInt(1, returnId);
                        ps.executeUpdate();
                    }

                    conn.commit();
                }
                response.sendRedirect(request.getContextPath() + "/salesReturn?success=1");
                return;
            }
            // ========== 不入库（作废退货） ==========
            if ("noStockIn".equals(action)) {
                int returnId = Integer.parseInt(request.getParameter("returnId"));
                try (Connection conn = DB.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE sales_return SET status = 5 WHERE sales_return_id = ?")) {
                    ps.setInt(1, returnId);
                    ps.executeUpdate();
                }
                response.sendRedirect(request.getContextPath() + "/salesReturn?success=1");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/salesReturn?error=" + e.getMessage());
        }
    }

    private Map<String, Object> mapOrderRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("sales_order_id", rs.getInt("sales_order_id"));
        row.put("customer_name", rs.getString("customer_name"));
        row.put("order_date", rs.getTimestamp("order_date"));
        row.put("sales_order_item_id", rs.getInt("sales_order_item_id"));
        row.put("product_id", rs.getInt("product_id"));
        row.put("product_name", rs.getString("product_name"));
        row.put("quantity", rs.getInt("quantity"));
        row.put("unit_price", rs.getBigDecimal("unit_price"));
        row.put("subtotal", rs.getBigDecimal("subtotal"));
        row.put("can_return", rs.getInt("can_return"));
        return row;
    }
}