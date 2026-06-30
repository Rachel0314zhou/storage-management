package servlet;

import dao.PurchaseReturnDao;
import model.PurchaseReturnDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class PurchaseReturnServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private final PurchaseReturnDao purchaseReturnDao = new PurchaseReturnDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            loadPageData(request);
            setSuccessMessage(request);
            request.getRequestDispatcher("/purchaseReturn.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("pageTitle", "采购退货管理");
            request.setAttribute("errorMessage", "采购退货页面加载失败：" + e.getMessage());
            request.setAttribute("returnableList", new ArrayList<Map<String, Object>>());
            request.setAttribute("returnList", new ArrayList<PurchaseReturnDetail>());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 1);
            request.setAttribute("totalCount", 0);
            request.setAttribute("pageSize", PAGE_SIZE);

            request.getRequestDispatcher("/purchaseReturn.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            int purchaseId = parseInt(request.getParameter("purchaseId"), 0);
            int productId = parseInt(request.getParameter("productId"), 0);
            int returnQuantity = parseInt(request.getParameter("returnQuantity"), 0);
            String reason = request.getParameter("reason");
            String remark = request.getParameter("remark");

            if (purchaseId <= 0 || productId <= 0 || returnQuantity <= 0) {
                request.setAttribute("errorMessage", "采购退货失败：采购订单、商品和退货数量不能为空。");
                loadPageData(request);
                request.getRequestDispatcher("/purchaseReturn.jsp").forward(request, response);
                return;
            }

            if (reason == null || reason.trim().isEmpty()) {
                request.setAttribute("errorMessage", "采购退货失败：请填写退货原因。");
                loadPageData(request);
                request.getRequestDispatcher("/purchaseReturn.jsp").forward(request, response);
                return;
            }

            purchaseReturnDao.addPurchaseReturn(
                    purchaseId,
                    productId,
                    returnQuantity,
                    reason,
                    remark
            );

            response.sendRedirect(request.getContextPath() + "/purchaseReturn?success=1");

        } catch (SQLException e) {
            e.printStackTrace();

            String message = e.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = "数据库操作失败";
            }

            request.setAttribute("errorMessage", "采购退货失败：" + message);
            loadPageData(request);
            request.getRequestDispatcher("/purchaseReturn.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("errorMessage", "系统错误：" + e.getMessage());
            loadPageData(request);
            request.getRequestDispatcher("/purchaseReturn.jsp").forward(request, response);
        }
    }

    /**
     * 加载页面所需数据：
     * 1. 可退货采购记录，支持搜索和分页。
     * 2. 采购退货历史记录，支持单独搜索。
     */
    private void loadPageData(HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        String returnKeyword = request.getParameter("returnKeyword");

        int currentPage = parseInt(request.getParameter("page"), 1);
        if (currentPage < 1) {
            currentPage = 1;
        }

        int totalCount = purchaseReturnDao.countReturnablePurchaseList(keyword);
        int totalPages = (int) Math.ceil(totalCount * 1.0 / PAGE_SIZE);

        if (totalPages < 1) {
            totalPages = 1;
        }

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        ArrayList<Map<String, Object>> returnableList =
                purchaseReturnDao.findReturnablePurchaseList(keyword, currentPage, PAGE_SIZE);

        ArrayList<PurchaseReturnDetail> returnList;

        if (returnKeyword != null && !returnKeyword.trim().isEmpty()) {
            returnList = purchaseReturnDao.searchPurchaseReturnDetail(returnKeyword);
        } else {
            returnList = purchaseReturnDao.findAllPurchaseReturnDetail();
        }

        request.setAttribute("pageTitle", "采购退货管理");
        request.setAttribute("keyword", keyword);
        request.setAttribute("returnKeyword", returnKeyword);
        request.setAttribute("returnableList", returnableList);
        request.setAttribute("returnList", returnList);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("pageSize", PAGE_SIZE);
    }

    private void setSuccessMessage(HttpServletRequest request) {
        String success = request.getParameter("success");

        if ("1".equals(success)) {
            request.setAttribute("successMessage", "采购退货成功，库存已由触发器自动减少，并生成库存流水。");
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
}