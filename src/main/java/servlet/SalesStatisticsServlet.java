package servlet;

import db.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class SalesStatisticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String yearStr = request.getParameter("year");
        String monthStr = request.getParameter("month");

        java.time.YearMonth now = java.time.YearMonth.now();
        if (yearStr == null || yearStr.trim().isEmpty()) {
            yearStr = String.valueOf(now.getYear());
            monthStr = String.valueOf(now.getMonthValue());
        }

        List<Map<String, Object>> summaryList = new ArrayList<>();
        List<Map<String, Object>> productRankList = new ArrayList<>();
        String errorMessage = null;

        try (Connection conn = DB.getConnection();
             CallableStatement cs = conn.prepareCall(
                     "{CALL sp_monthly_sales_summary_out(?, ?, ?, ?, ?, ?)}"
             )) {

            cs.setInt(1, Integer.parseInt(yearStr));
            cs.setInt(2, Integer.parseInt(monthStr));
            cs.registerOutParameter(3, Types.DECIMAL);
            cs.registerOutParameter(4, Types.INTEGER);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.registerOutParameter(6, Types.DECIMAL);

            boolean hasResultSet = cs.execute();

            if (hasResultSet) {
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("统计年份", rs.getInt("统计年份"));
                    row.put("统计月份", rs.getInt("统计月份"));
                    row.put("月销售总额", rs.getBigDecimal("月销售总额"));
                    row.put("月销售总数量", rs.getInt("月销售总数量"));
                    row.put("月订单总数", rs.getInt("月订单总数"));
                    row.put("平均订单金额", rs.getBigDecimal("平均订单金额"));
                    row.put("月利润总额", rs.getBigDecimal("月利润总额"));
                    summaryList.add(row);
                }
                rs.close();
                cs.getMoreResults();
            }

            ResultSet rs2 = cs.getResultSet();
            while (rs2.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("product_id", rs2.getInt("product_id"));
                row.put("product_name", rs2.getString("product_name"));
                row.put("product_code", rs2.getString("product_code"));
                row.put("销售数量", rs2.getInt("销售数量"));
                row.put("销售金额", rs2.getBigDecimal("销售金额"));
                row.put("利润", rs2.getBigDecimal("利润"));
                productRankList.add(row);
            }
            rs2.close();

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "查询失败：" + e.getMessage();
        }

        request.setAttribute("summaryList", summaryList);
        request.setAttribute("productRankList", productRankList);
        request.setAttribute("year", yearStr);
        request.setAttribute("month", monthStr);
        request.setAttribute("errorMessage", errorMessage);

        request.getRequestDispatcher("/salesStatistics.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}