<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>仓储管理系统 - 库存管理</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: #f5f7fa;
            min-height: 100vh;
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

        .form-row {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
        }

        input[type="text"],
        input[type="number"] {
            padding: 8px 10px;
            width: 220px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        input[type="text"]:focus,
        input[type="number"]:focus {
            outline: none;
            border-color: #3498db;
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
        }
        .btn:hover, button:hover { opacity: 0.85; }

        .btn-gray {
            background: #7f8c8d;
        }
        .btn-gray:hover { background: #666; }

        .btn-warning {
            background: #e67e22;
        }
        .btn-warning:hover { background: #d35400; }

        .table-wrap { overflow-x: auto; }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }
        th {
            background: #ecf0f1;
            color: #333;
            padding: 10px 12px;
            border: 1px solid #ddd;
            text-align: center;
        }
        td {
            padding: 9px 12px;
            border: 1px solid #ddd;
            text-align: center;
        }
        tr:nth-child(even) { background: #fafafa; }
        tr:hover { background: #f0f7ff; }
        .empty-row { text-align: center; color: #999; padding: 30px 0; }

        .low-stock { color: #e74c3c; font-weight: bold; }
        .normal-stock { color: #27ae60; font-weight: bold; }
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
    <a href="${pageContext.request.contextPath}/">首页</a>
    <a href="${pageContext.request.contextPath}/product">商品管理</a>
    <a href="${pageContext.request.contextPath}/category">商品分类</a>
    <a href="${pageContext.request.contextPath}/supplier">供应商管理</a>
    <a href="${pageContext.request.contextPath}/customer">客户管理</a>
    <a href="${pageContext.request.contextPath}/inventory" class="highlight">库存管理</a>
    <a href="${pageContext.request.contextPath}/inventoryLog">库存流水</a>
    <a href="${pageContext.request.contextPath}/salesStatistics">月度统计</a>
    <a href="${pageContext.request.contextPath}/windowFunctions">销售分析</a>

    <%-- ===== 业务员和管理员可见（roleId=1 或 2） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
        <a href="${pageContext.request.contextPath}/purchase">采购入库</a>
        <a href="${pageContext.request.contextPath}/sale">销售出库</a>
        <a href="${pageContext.request.contextPath}/purchaseReturn">采购退货</a>
        <a href="${pageContext.request.contextPath}/salesReturn">销售退货</a>
    </c:if>

    <%-- ===== 仅管理员可见（roleId=1） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 1}">
        <a href="${pageContext.request.contextPath}/user">用户管理</a>
        <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
    </c:if>
</div>

<div class="container">
    <%
        Integer threshold = (Integer) session.getAttribute("lowStockThreshold");
        if (threshold == null) {
            threshold = 10;
        }
    %>

    <!-- 库存预警 -->
    <div style="background: #fef9e7; border-left: 4px solid #f39c12; padding: 10px 18px; border-radius: 4px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
        <div style="display: flex; align-items: center; gap: 10px; flex-wrap: wrap;">
            <span style="font-weight: bold; color: #e67e22;">⚠️ 库存预警</span>
            <span style="color: #333;">低于</span>
            <form action="${pageContext.request.contextPath}/inventory" method="get" style="display:inline-block;" id="thresholdForm">
                <input type="hidden" name="action" value="setThresholdAndQuery">
                <input type="number" name="threshold" placeholder="阈值" value="<%= threshold %>" style="width:70px; padding:4px 8px; border:1px solid #ccc; border-radius:4px;" required>
                <button type="submit" style="padding:4px 12px; background:#e67e22; color:white; border:none; border-radius:4px; cursor:pointer;">设定预警</button>
            </form>
            <span style="color: #333;">的商品，当前有 <strong>${lowStockCount != null ? lowStockCount : 0}</strong> 种</span>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/inventory?action=low&threshold=<%= threshold %>" style="background: #e67e22; color: white; padding:4px 12px; border-radius:4px; text-decoration: none; font-size:13px;">查看详情</a>
        </div>
    </div>

    <div class="title">库存明细</div>

    <div class="form-row">
        <form action="${pageContext.request.contextPath}/inventory" method="get" style="display:inline-block;">
            <input type="hidden" name="action" value="search">
            <input type="text" name="keyword" placeholder="输入商品名称或分类" value="${keyword}">
            <button class="btn" type="submit">搜索库存</button>
        </form>

        <form action="${pageContext.request.contextPath}/inventory" method="get" style="display:inline-block;">
            <input type="hidden" name="action" value="low">
            <input type="number" name="threshold" placeholder="低库存阈值，例如 10" value="${threshold}">
            <button type="submit" class="btn-warning">低库存查询</button>
        </form>

        <a class="btn btn-gray" href="${pageContext.request.contextPath}/inventory">查看全部</a>
    </div>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>库存ID</th>
                <th>商品ID</th>
                <th>商品名称</th>
                <th>分类</th>
                <th>规格</th>
                <th>单位</th>
                <th>库存数量</th>
                <th>采购价</th>
                <th>销售价</th>
                <th>更新时间</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${inventoryList}">
                <tr>
                    <td>${item.inventoryId}</td>
                    <td>${item.productId}</td>
                    <td>${item.productName}</td>
                    <td>${item.categoryName}</td>
                    <td>${item.specification}</td>
                    <td>${item.unit}</td>
                    <td>
                        <c:choose>
                            <c:when test="${item.quantity <= 10}">
                                <span class="low-stock">${item.quantity}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="normal-stock">${item.quantity}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${item.purchasePrice}</td>
                    <td>${item.salePrice}</td>
                    <td>${item.updatedAt}</td>
                </tr>
            </c:forEach>

            <c:if test="${empty inventoryList}">
                <tr>
                    <td class="empty-row" colspan="10">暂无库存数据</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>