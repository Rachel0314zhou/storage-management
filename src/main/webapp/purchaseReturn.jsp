<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>采购退货管理</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: #f5f7fa;
            margin: 0;
            padding: 0;
        }

        .header {
            background: #2c3e50;
            color: white;
            padding: 18px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header h1 { margin: 0; font-size: 24px; }
        .header .user-info {
            font-size: 14px;
            color: #bdc3c7;
        }
        .header .user-info strong {
            color: white;
        }

        .nav {
            background: #34495e;
            padding: 12px 40px;
            display: flex;
            flex-wrap: wrap;
            gap: 5px;
        }
        .nav a {
            color: white;
            text-decoration: none;
            margin-right: 18px;
            font-size: 15px;
            padding: 4px 6px;
            border-radius: 3px;
            transition: background 0.3s;
        }
        .nav a:hover { background: #3d566e; }
        .nav a.highlight { color: #f1c40f; }

        .container {
            width: 94%;
            margin: 25px auto;
            background: white;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .title {
            font-size: 22px;
            color: #333;
            margin-bottom: 20px;
        }

        .section-title {
            font-size: 18px;
            margin: 25px 0 12px;
            color: #2c3e50;
            border-left: 4px solid #c0392b;
            padding-left: 10px;
        }

        input[type="text"],
        input[type="number"] {
            padding: 8px 10px;
            width: 180px;
            border: 1px solid #ccc;
            border-radius: 4px;
            margin-right: 8px;
            margin-bottom: 10px;
            box-sizing: border-box;
        }
        input[type="text"]:focus,
        input[type="number"]:focus {
            outline: none;
            border-color: #3498db;
        }

        input.small-input {
            width: 105px;
        }

        input.reason-input {
            width: 140px;
        }

        .btn, button {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-block;
            transition: background 0.2s;
            color: white;
            background: #3498db;
        }
        .btn:hover, button:hover { opacity: 0.85; }

        .btn-danger {
            background: #c0392b;
        }
        .btn-danger:hover { background: #922b21; }

        .btn-gray {
            background: #7f8c8d;
        }
        .btn-gray:hover { background: #666; }

        .message-success {
            background: #eafaf1;
            color: #1e8449;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }

        .message-error {
            background: #fdecea;
            color: #c0392b;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }

        .tip {
            color: #777;
            font-size: 13px;
            margin-bottom: 12px;
            line-height: 1.8;
        }

        .readonly-notice {
            background: #fef9e7;
            border-left: 4px solid #f39c12;
            padding: 12px 18px;
            border-radius: 4px;
            margin-bottom: 15px;
            color: #7d6608;
        }

        .table-block {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
            margin-top: 10px;
        }

        th {
            background: #ecf0f1;
            color: #333;
            padding: 10px 12px;
            border: 1px solid #ddd;
            text-align: center;
            white-space: nowrap;
        }

        td {
            padding: 9px 12px;
            border: 1px solid #ddd;
            text-align: center;
            white-space: nowrap;
        }

        tr:nth-child(even) { background: #fafafa; }
        tr:hover { background: #f0f7ff; }
        .empty-row { text-align: center; color: #999; padding: 30px 0; }

        .status {
            color: #27ae60;
            font-weight: bold;
        }

        .status-returnable {
            color: #c0392b;
            font-weight: bold;
        }

        .inline-form {
            margin: 0;
            white-space: nowrap;
        }

        .pagination {
            margin-top: 15px;
            text-align: center;
        }

        .pagination span {
            margin: 0 12px;
            color: #555;
        }

        .summary {
            color: #555;
            font-size: 14px;
            margin-top: 10px;
        }

        .readonly-tag {
            color: #999;
            font-size: 12px;
        }
    </style>
</head>
<body>

<!-- ===== 顶部导航栏 ===== -->
<div class="header">
    <h1>仓储管理系统</h1>
    <div class="user-info">
        欢迎，<strong>${sessionScope.currentUser.username}</strong>
        （${sessionScope.currentUser.roleName}）
        <a href="${pageContext.request.contextPath}/logout" style="color:#e74c3c;margin-left:15px;text-decoration:none;">退出</a>
    </div>
</div>

<div class="nav">
    <%-- ===== 所有用户可见的公共菜单 ===== --%>
    <a href="${pageContext.request.contextPath}/product">商品管理</a>
    <a href="${pageContext.request.contextPath}/category">商品分类</a>
    <a href="${pageContext.request.contextPath}/supplier">供应商管理</a>
    <a href="${pageContext.request.contextPath}/customer">客户管理</a>
    <a href="${pageContext.request.contextPath}/inventory">库存管理</a>
    <a href="${pageContext.request.contextPath}/inventoryLog">库存流水</a>
    <a href="${pageContext.request.contextPath}/salesStatistics">月度统计</a>
    <a href="${pageContext.request.contextPath}/windowFunctions">销售分析</a>

    <%-- ===== 业务员和管理员可见（roleId=1 或 2） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
        <a href="${pageContext.request.contextPath}/purchase">采购入库</a>
        <a href="${pageContext.request.contextPath}/sale">销售出库</a>
        <a href="${pageContext.request.contextPath}/purchaseReturn" class="highlight">采购退货</a>
        <a href="${pageContext.request.contextPath}/salesReturn">销售退货</a>
    </c:if>

    <%-- ===== 仅管理员可见（roleId=1） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 1}">
        <a href="${pageContext.request.contextPath}/user">用户管理</a>
        <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
    </c:if>
</div>

<div class="container">
    <div class="title">
        <c:choose>
            <c:when test="${not empty pageTitle}">
                ${pageTitle}
            </c:when>
            <c:otherwise>
                采购退货管理
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="message-success">${successMessage}</div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="message-error">${errorMessage}</div>
    </c:if>

    <div class="section-title">一、搜索可退货采购记录</div>

    <div class="tip">
        只有已经部分入库或已入库，并且仍有可退货数量的采购记录才会显示。<br>
        用户不需要手动填写采购订单ID和商品ID，直接在下方列表中点击“退货”即可。
    </div>

    <form action="${pageContext.request.contextPath}/purchaseReturn" method="get" style="margin-bottom: 15px;">
        <input type="text"
               name="keyword"
               placeholder="输入采购订单ID、供应商、商品名称或商品编号"
               value="${keyword}">
        <button class="btn" type="submit">搜索可退货记录</button>
        <a class="btn btn-gray" href="${pageContext.request.contextPath}/purchaseReturn">查看全部</a>
    </form>

    <div class="summary">
        共找到 ${totalCount} 条可退货记录，每页显示 ${pageSize} 条。
    </div>

    <div class="section-title">二、可退货采购记录列表</div>

    <%-- ===== 只读用户提示（只读用户不能退货） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 3}">
        <div class="readonly-notice">
            ⚠️ 您当前为只读用户，只能查看可退货记录，无法执行退货操作。
        </div>
    </c:if>

    <div class="table-block">
        <table>
            <thead>
            <tr>
                <th>采购订单ID</th>
                <th>采购日期</th>
                <th>订单状态</th>
                <th>供应商</th>
                <th>商品ID</th>
                <th>商品编号</th>
                <th>商品名称</th>
                <th>采购单价</th>
                <th>已入库数量</th>
                <th>已退货数量</th>
                <th>可退货数量</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${returnableList}">
                <tr>
                    <td>${item.purchaseId}</td>
                    <td>${item.purchaseDate}</td>
                    <td><span class="status">${item.status}</span></td>
                    <td>${item.supplierName}</td>
                    <td>${item.productId}</td>
                    <td>${item.productCode}</td>
                    <td>${item.productName}</td>
                    <td>${item.unitPrice}</td>
                    <td>${item.stockedInQuantity}</td>
                    <td>${item.returnedQuantity}</td>
                    <td><span class="status-returnable">${item.returnableQuantity}</span></td>
                    <td>
                            <%-- 只有非只读用户才能看到“退货”表单 --%>
                        <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
                            <form class="inline-form"
                                  action="${pageContext.request.contextPath}/purchaseReturn"
                                  method="post">
                                <input type="hidden" name="purchaseId" value="${item.purchaseId}">
                                <input type="hidden" name="productId" value="${item.productId}">
                                <input type="hidden" name="keyword" value="${keyword}">
                                <input type="hidden" name="page" value="${currentPage}">

                                <input class="small-input"
                                       type="number"
                                       name="returnQuantity"
                                       min="1"
                                       max="${item.returnableQuantity}"
                                       placeholder="退货数量"
                                       required>

                                <input class="reason-input"
                                       type="text"
                                       name="reason"
                                       placeholder="退货原因"
                                       required>

                                <input class="reason-input"
                                       type="text"
                                       name="remark"
                                       placeholder="备注">

                                <button type="submit" class="btn-danger">退货</button>
                            </form>
                        </c:if>
                        <c:if test="${sessionScope.currentUser.roleId == 3}">
                            <span class="readonly-tag">只读</span>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty returnableList}">
                <tr>
                    <td class="empty-row" colspan="12">暂无可退货采购记录</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <div class="pagination">
        <c:if test="${currentPage > 1}">
            <c:url var="prevUrl" value="/purchaseReturn">
                <c:param name="page" value="${currentPage - 1}" />
                <c:param name="keyword" value="${keyword}" />
            </c:url>
            <a class="btn" href="${prevUrl}">上一页</a>
        </c:if>

        <span>第 ${currentPage} 页 / 共 ${totalPages} 页</span>

        <c:if test="${currentPage < totalPages}">
            <c:url var="nextUrl" value="/purchaseReturn">
                <c:param name="page" value="${currentPage + 1}" />
                <c:param name="keyword" value="${keyword}" />
            </c:url>
            <a class="btn" href="${nextUrl}">下一页</a>
        </c:if>
    </div>

    <div class="section-title">三、采购退货历史记录</div>

    <form action="${pageContext.request.contextPath}/purchaseReturn" method="get" style="margin-bottom: 15px;">
        <input type="hidden" name="keyword" value="${keyword}">
        <input type="text"
               name="returnKeyword"
               placeholder="输入供应商、商品名称或退货原因"
               value="${returnKeyword}">
        <button class="btn" type="submit">搜索退货记录</button>
        <a class="btn btn-gray" href="${pageContext.request.contextPath}/purchaseReturn">查看全部</a>
    </form>

    <div class="table-block">
        <table>
            <thead>
            <tr>
                <th>退货ID</th>
                <th>采购订单ID</th>
                <th>采购日期</th>
                <th>订单状态</th>
                <th>供应商</th>
                <th>商品ID</th>
                <th>商品</th>
                <th>退货数量</th>
                <th>退货时间</th>
                <th>退货原因</th>
                <th>备注</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${returnList}">
                <tr>
                    <td>${item.returnId}</td>
                    <td>${item.purchaseId}</td>
                    <td>${item.purchaseDate}</td>
                    <td><span class="status">${item.purchaseStatus}</span></td>
                    <td>${item.supplierName}</td>
                    <td>${item.productId}</td>
                    <td>${item.productName}</td>
                    <td>${item.returnQuantity}</td>
                    <td>${item.returnTime}</td>
                    <td>${item.reason}</td>
                    <td>${item.remark}</td>
                </tr>
            </c:forEach>

            <c:if test="${empty returnList}">
                <tr>
                    <td class="empty-row" colspan="11">暂无采购退货记录</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>