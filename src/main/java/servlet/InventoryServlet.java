package servlet;

import dao.InventoryDao;
import model.InventoryDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

public class InventoryServlet extends HttpServlet {

    private final InventoryDao inventoryDao = new InventoryDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        String keyword = request.getParameter("keyword");
        String thresholdStr = request.getParameter("threshold");

        ArrayList<InventoryDetail> inventoryList;

        try {
            if ("search".equals(action)) {
                inventoryList = inventoryDao.queryInventory(keyword);
                request.setAttribute("keyword", keyword);
                request.setAttribute("pageTitle", "库存搜索结果");
            } else if ("setThresholdAndQuery".equals(action)) {
                String thStr = request.getParameter("threshold");
                if (thStr != null && !thStr.trim().isEmpty()) {
                    int newThreshold = Integer.parseInt(thStr.trim());
                    request.getSession().setAttribute("lowStockThreshold", newThreshold);
                }
                // 只保存阈值，不查询，刷新当前页面
                response.sendRedirect(request.getContextPath() + "/inventory");
                return;
            } else if ("low".equals(action)) {
                int threshold = parseInt(thresholdStr, 10);
                if (thresholdStr == null || thresholdStr.trim().isEmpty()) {
                    Integer sessionThreshold = (Integer) request.getSession().getAttribute("lowStockThreshold");
                    if (sessionThreshold != null) {
                        threshold = sessionThreshold;
                    } else {
                        threshold = 10;
                    }
                } else {
                    request.getSession().setAttribute("lowStockThreshold", threshold);
                }
                inventoryList = inventoryDao.findLowStock(threshold);
                request.setAttribute("threshold", threshold);
                request.setAttribute("pageTitle", "低库存商品");
                int count = 0;
                for (InventoryDetail item : inventoryList) {
                    if (item.getQuantity() < threshold) {
                        count++;
                    }
                }
                request.setAttribute("lowStockCount", count);
            } else {
                inventoryList = inventoryDao.findAllInventory();
                request.setAttribute("pageTitle", "库存明细");
            }

            request.setAttribute("inventoryList", inventoryList);
            // 计算低库存商品数量（供预警条显示）
            Integer sessionThreshold = (Integer) request.getSession().getAttribute("lowStockThreshold");
            if (sessionThreshold == null) {
                sessionThreshold = 10;
            }
            int count = 0;
            for (InventoryDetail item : inventoryList) {
                if (item.getQuantity() < sessionThreshold) {
                    count++;
                }
            }
            request.setAttribute("lowStockCount", count);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "库存查询失败：" + e.getMessage());
            request.setAttribute("inventoryList", new ArrayList<InventoryDetail>());
        }

        request.getRequestDispatcher("/inventory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
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
}