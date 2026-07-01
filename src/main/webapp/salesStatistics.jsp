<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>月度销售统计</title>
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

        .section-title {
            font-size: 18px;
            margin: 25px 0 12px;
            color: #2c3e50;
            border-left: 4px solid #3498db;
            padding-left: 10px;
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

        input[type="number"] {
            padding: 8px 10px;
            width: 120px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        input[type="number"]:focus {
            outline: none;
            border-color: #3498db;
        }

        .btn {
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
        .btn:hover { background: #2980b9; }

        button {
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
        button:hover { background: #2980b9; }

        .btn-gray {
            background: #7f8c8d;
        }
        .btn-gray:hover { background: #666; }

        .message-error {
            background: #fdecea;
            color: #c0392b;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }

        .no-data {
            color: #999;
            text-align: center;
            padding: 30px 0;
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
    <a href="${pageContext.request.contextPath}/salesStatistics" class="highlight">月度统计</a>
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
                月度销售统计
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="message-error">${errorMessage}</div>
    </c:if>

    <div class="form-row">
        <form action="${pageContext.request.contextPath}/salesStatistics" method="get" style="display:inline-block;">
            <input type="number" name="year" placeholder="年份" value="${year}" required>
            <input type="number" name="month" placeholder="月份" value="${month}" required min="1" max="12">
            <button type="submit">查询统计</button>
        </form>
        <a class="btn btn-gray" href="${pageContext.request.contextPath}/salesStatistics">查看本月</a>
    </div>

    <c:if test="${empty summaryList and empty errorMessage}">
        <div class="no-data">请输入年份和月份进行查询</div>
    </c:if>

    <c:if test="${not empty summaryList}">
        <div class="section-title">汇总信息</div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>统计年份</th>
                    <th>统计月份</th>
                    <th>月销售总额</th>
                    <th>月销售总数量</th>
                    <th>月订单总数</th>
                    <th>平均订单金额</th>
                    <th>月利润总额</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="row" items="${summaryList}">
                    <tr>
                        <td>${row['统计年份']}</td>
                        <td>${row['统计月份']}</td>
                        <td>${row['月销售总额']}</td>
                        <td>${row['月销售总数量']}</td>
                        <td>${row['月订单总数']}</td>
                        <td>${row['平均订单金额']}</td>
                        <td>${row['月利润总额']}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="section-title">商品销售排行</div>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>商品ID</th>
                    <th>商品编码</th>
                    <th>商品名称</th>
                    <th>销售数量</th>
                    <th>销售金额</th>
                    <th>利润</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="row" items="${productRankList}">
                    <tr>
                        <td>${row['product_id']}</td>
                        <td>${row['product_code']}</td>
                        <td>${row['product_name']}</td>
                        <td>${row['销售数量']}</td>
                        <td>${row['销售金额']}</td>
                        <td>${row['利润']}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty productRankList}">
                    <tr>
                        <td class="empty-row" colspan="6">该月暂无销售数据</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </c:if>
</div>

</body>
</html>