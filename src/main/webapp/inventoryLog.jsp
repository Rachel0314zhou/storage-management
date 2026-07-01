<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>库存流水</title>
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
            width: 92%;
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

        input[type="text"] {
            padding: 8px 10px;
            width: 260px;
            border: 1px solid #ccc;
            border-radius: 4px;
            margin-right: 8px;
        }
        input[type="text"]:focus {
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
            background: #3498db;
        }
        .btn:hover, button:hover { opacity: 0.85; }

        .btn-gray {
            background: #7f8c8d;
        }
        .btn-gray:hover { background: #666; }

        .btn-in {
            background: #27ae60;
        }
        .btn-in:hover { background: #1e8449; }

        .btn-out {
            background: #e67e22;
        }
        .btn-out:hover { background: #d35400; }

        .message-error {
            background: #fdecea;
            color: #c0392b;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
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

        .type-in {
            color: #27ae60;
            font-weight: bold;
        }
        .type-out {
            color: #e67e22;
            font-weight: bold;
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
    <a href="${pageContext.request.contextPath}/inventoryLog" class="highlight">库存流水</a>
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
    <div class="title">
        <c:choose>
            <c:when test="${not empty pageTitle}">
                ${pageTitle}
            </c:when>
            <c:otherwise>
                库存流水记录
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="message-error">${errorMessage}</div>
    </c:if>

    <div class="form-row">
        <form action="${pageContext.request.contextPath}/inventoryLog" method="get" style="display:inline-block;">
            <input type="hidden" name="action" value="search">
            <input type="text" name="keyword" placeholder="输入商品、入库、出库、备注或来源ID" value="${keyword}">
            <button class="btn" type="submit">搜索流水</button>
        </form>

        <a class="btn btn-in" href="${pageContext.request.contextPath}/inventoryLog?action=type&changeType=入库">只看入库</a>
        <a class="btn btn-out" href="${pageContext.request.contextPath}/inventoryLog?action=type&changeType=出库">只看出库</a>
        <a class="btn btn-gray" href="${pageContext.request.contextPath}/inventoryLog">查看全部</a>
    </div>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>流水ID</th>
                <th>商品名称</th>
                <th>变动类型</th>
                <th>变动数量</th>
                <th>变动前库存</th>
                <th>变动后库存</th>
                <th>来源类型</th>
                <th>来源ID</th>
                <th>备注</th>
                <th>创建时间</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${logList}">
                <tr>
                    <td>${item.logId}</td>
                    <td>${item.productName}</td>
                    <td>
                        <c:choose>
                            <c:when test="${fn:contains(item.changeType, '入库')}">
                                <span class="type-in">${item.changeType}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="type-out">${item.changeType}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${item.changeQuantity}</td>
                    <td>${item.beforeQuantity}</td>
                    <td>${item.afterQuantity}</td>
                    <td>${item.sourceType}</td>
                    <td>${item.sourceId}</td>
                    <td>${item.remark}</td>
                    <td>${item.createdAt}</td>
                </tr>
            </c:forEach>

            <c:if test="${empty logList}">
                <tr>
                    <td class="empty-row" colspan="10">暂无库存流水记录</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>