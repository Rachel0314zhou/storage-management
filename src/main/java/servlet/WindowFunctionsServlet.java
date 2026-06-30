package servlet;

import db.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class WindowFunctionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        List<Map<String, Object>> rankList = new ArrayList<>();
        List<Map<String, Object>> customerRankList = new ArrayList<>();
        List<Map<String, Object>> trendList = new ArrayList<>();
        String errorMessage = null;

        try (Connection conn = DB.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_window_functions_demo()}")) {

            boolean hasResultSet = cs.execute();

            // 第1个结果集：商品销量排名
            if (hasResultSet) {
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("商品名称", rs.getString("product_name"));
                    row.put("总销量", rs.getInt("总销量"));
                    row.put("排名_带间隔", rs.getInt("排名_带间隔"));
                    row.put("排名_连续", rs.getInt("排名_连续"));
                    rankList.add(row);
                }
                rs.close();
                cs.getMoreResults();
            }

            // 第2个结果集：客户消费排名
            ResultSet rs2 = cs.getResultSet();
            while (rs2.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("客户名称", rs2.getString("customer_name"));
                row.put("总消费额", rs2.getBigDecimal("总消费额"));
                row.put("消费排名", rs2.getInt("消费排名"));
                customerRankList.add(row);
            }
            rs2.close();
            cs.getMoreResults();

            // 第3个结果集：月度销售趋势
            ResultSet rs3 = cs.getResultSet();
            while (rs3.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("月份", rs3.getString("月份"));
                row.put("月销售额", rs3.getBigDecimal("月销售额"));
                row.put("累计销售额", rs3.getBigDecimal("累计销售额"));
                row.put("上月销售额", rs3.getBigDecimal("上月销售额"));
                row.put("环比增长", rs3.getBigDecimal("环比增长"));
                trendList.add(row);
            }
            rs3.close();

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "窗口函数查询失败：" + e.getMessage();
        }

        request.setAttribute("rankList", rankList);
        request.setAttribute("customerRankList", customerRankList);
        request.setAttribute("trendList", trendList);
        request.setAttribute("errorMessage", errorMessage);

        request.getRequestDispatcher("/windowFunctions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}