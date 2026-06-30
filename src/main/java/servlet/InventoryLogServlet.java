package servlet;

import dao.InventoryLogDao;
import model.InventoryLogDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 库存流水控制器
 *
 * 功能：
 * 1. 查询全部库存流水
 * 2. 按入库/出库类型筛选
 * 3. 按关键字搜索库存流水
 *
 * 访问地址：
 * /inventoryLog
 */

public class InventoryLogServlet extends HttpServlet {

    private final InventoryLogDao inventoryLogDao = new InventoryLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        String keyword = request.getParameter("keyword");
        String changeType = request.getParameter("changeType");

        ArrayList<InventoryLogDetail> logList;

        try {
            if ("search".equals(action)) {
                logList = inventoryLogDao.searchLog(keyword);
                request.setAttribute("keyword", keyword);
                request.setAttribute("pageTitle", "库存流水搜索结果");
            } else if ("type".equals(action)) {
                logList = inventoryLogDao.findLogByChangeType(changeType);
                request.setAttribute("changeType", changeType);
                request.setAttribute("pageTitle", changeType + "流水记录");
            } else {
                logList = inventoryLogDao.findAllLog();
                request.setAttribute("pageTitle", "库存流水记录");
            }

            request.setAttribute("logList", logList);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "库存流水查询失败：" + e.getMessage());
            request.setAttribute("logList", new ArrayList<InventoryLogDetail>());
        }

        request.getRequestDispatcher("/inventoryLog.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}