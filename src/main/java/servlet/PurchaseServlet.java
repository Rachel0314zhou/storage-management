package servlet;

import dao.BasicDataDao;
import dao.PurchaseDao;
import model.PurchaseDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class PurchaseServlet extends HttpServlet {

    private final PurchaseDao purchaseDao = new PurchaseDao();
    private final BasicDataDao basicDataDao = new BasicDataDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String purchaseKeyword = request.getParameter("purchaseKeyword");
        String stockInKeyword = request.getParameter("stockInKeyword");

        try {
            ArrayList<PurchaseDetail> purchaseList;
            ArrayList<Map<String, Object>> stockInList;

            // 采购订单记录查询
            if (purchaseKeyword != null && !purchaseKeyword.trim().isEmpty()) {
                purchaseList = purchaseDao.searchPurchaseDetail(purchaseKeyword);
            } else {
                purchaseList = purchaseDao.findAllPurchaseDetail();
            }

            // 采购入库明细 / 进货单记录查询
            if (stockInKeyword != null && !stockInKeyword.trim().isEmpty()) {
                stockInList = purchaseDao.searchStockInDetail(stockInKeyword);
            } else {
                stockInList = purchaseDao.findAllStockInDetail();
            }

            request.setAttribute("purchaseList", purchaseList);
            request.setAttribute("pendingOrderList", purchaseDao.findPendingPurchaseOrders());
            request.setAttribute("stockInList", stockInList);

            request.setAttribute("purchaseKeyword", purchaseKeyword);
            request.setAttribute("stockInKeyword", stockInKeyword);
            request.setAttribute("pageTitle", "采购订单与入库管理");

            setSuccessMessage(request);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("errorMessage", "采购记录查询失败：" + e.getMessage());
            request.setAttribute("purchaseList", new ArrayList<PurchaseDetail>());
            request.setAttribute("pendingOrderList", new ArrayList<Map<String, Object>>());
            request.setAttribute("stockInList", new ArrayList<Map<String, Object>>());
            request.setAttribute("pageTitle", "采购订单与入库管理");
        }

        setBasicData(request);
        request.getRequestDispatcher("/purchase.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");

        try {
            if ("stockIn".equals(action)) {
                handleStockIn(request, response);
            } else {
                handleCreateOrder(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "采购业务处理失败：" + e.getMessage());
            reloadPurchasePage(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "系统错误：" + e.getMessage());
            reloadPurchasePage(request, response);
        }
    }

    /**
     * 新增采购订单。
     * 只创建采购订单和采购明细，不增加库存。
     */
    private void handleCreateOrder(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        int supplierId = parseInt(request.getParameter("supplierId"), 0);
        int productId = parseInt(request.getParameter("productId"), 0);
        int quantity = parseInt(request.getParameter("quantity"), 0);
        BigDecimal unitPrice = parseBigDecimal(request.getParameter("unitPrice"), BigDecimal.ZERO);
        String remark = request.getParameter("remark");

        if (supplierId <= 0 || productId <= 0 || quantity <= 0) {
            request.setAttribute("errorMessage", "创建采购订单失败：供应商、商品和采购数量不能为空。");
            reloadPurchasePage(request, response);
            return;
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            request.setAttribute("errorMessage", "创建采购订单失败：采购单价不能为负数。");
            reloadPurchasePage(request, response);
            return;
        }

        purchaseDao.createPurchaseOrder(
                supplierId,
                productId,
                quantity,
                unitPrice,
                remark
        );

        response.sendRedirect(request.getContextPath() + "/purchase?success=order");
    }

    /**
     * 办理采购入库。
     * 根据采购订单插入采购入库记录，库存由触发器自动增加。
     */
    private void handleStockIn(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        int purchaseId = parseInt(request.getParameter("purchaseId"), 0);
        int productId = parseInt(request.getParameter("productId"), 0);
        int stockInQuantity = parseInt(request.getParameter("stockInQuantity"), 0);
        String remark = request.getParameter("remark");

        if (purchaseId <= 0 || productId <= 0 || stockInQuantity <= 0) {
            request.setAttribute("errorMessage", "采购入库失败：采购订单、商品和入库数量不能为空。");
            reloadPurchasePage(request, response);
            return;
        }

        purchaseDao.stockInPurchaseOrder(
                purchaseId,
                productId,
                stockInQuantity,
                remark
        );

        response.sendRedirect(request.getContextPath() + "/purchase?success=stockIn");
    }

    /**
     * 操作失败时重新加载页面数据。
     */
    private void reloadPurchasePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("pageTitle", "采购订单与入库管理");
        request.setAttribute("purchaseList", purchaseDao.findAllPurchaseDetail());
        request.setAttribute("pendingOrderList", purchaseDao.findPendingPurchaseOrders());
        request.setAttribute("stockInList", purchaseDao.findAllStockInDetail());

        setBasicData(request);
        request.getRequestDispatcher("/purchase.jsp").forward(request, response);
    }

    /**
     * 设置供应商、商品下拉框数据。
     */
    private void setBasicData(HttpServletRequest request) {
        request.setAttribute("supplierList", basicDataDao.findAllSuppliers());
        request.setAttribute("productList", basicDataDao.findAllProducts());
    }

    /**
     * 根据 success 参数显示成功信息。
     */
    private void setSuccessMessage(HttpServletRequest request) {
        String success = request.getParameter("success");

        if ("order".equals(success)) {
            request.setAttribute("successMessage", "采购订单创建成功。当前只是下达采购订单，库存暂未增加。");
        } else if ("stockIn".equals(success)) {
            request.setAttribute("successMessage", "采购入库成功。系统已生成进货单，库存已由数据库触发器自动增加。");
        } else if ("1".equals(success)) {
            request.setAttribute("successMessage", "采购业务处理成功。");
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