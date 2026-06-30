<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>销售分析</title>
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
        }
        .header h1 { margin: 0; font-size: 24px; }

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
            border-left: 4px solid #e67e22;
            padding-left: 10px;
        }

        .form-row {
            margin-bottom: 20px;
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
        }
        .btn:hover { opacity: 0.85; }
        .btn-gray { background: #7f8c8d; }
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

<div class="header">
    <h1>仓储管理系统</h1>
</div>

<div class="nav">
    <a href="${pageContext.request.contextPath}/product">商品管理</a>
    <a href="${pageContext.request.contextPath}/category">商品分类</a>
    <a href="${pageContext.request.contextPath}/supplier">供应商管理</a>
    <a href="${pageContext.request.contextPath}/customer">客户管理</a>
    <a href="${pageContext.request.contextPath}/inventory">库存管理</a>
    <a href="${pageContext.request.contextPath}/purchase">采购入库</a>
    <a href="${pageContext.request.contextPath}/sale">销售出库</a>
    <a href="${pageContext.request.contextPath}/inventoryLog">库存流水</a>
    <a href="${pageContext.request.contextPath}/purchaseReturn">采购退货</a>
    <a href="${pageContext.request.contextPath}/salesReturn">销售退货</a>
    <a href="${pageContext.request.contextPath}/salesStatistics">月度统计</a>
    <a href="${pageContext.request.contextPath}/windowFunctions" class="highlight">销售分析</a>
    <a href="${pageContext.request.contextPath}/user">用户管理</a>
    <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
</div>

<div class="container">
    <div class="title">
        <c:choose>
            <c:when test="${not empty pageTitle}">
                ${pageTitle}
            </c:when>
            <c:otherwise>
                销售分析
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="message-error">${errorMessage}</div>
    </c:if>

    <div class="form-row">
        <a class="btn btn-gray" href="${pageContext.request.contextPath}/windowFunctions">刷新数据</a>
    </div>

    <div class="section-title">商品销量排名</div>
    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>商品名称</th>
                <th>总销量</th>
                <th>排名（带间隔）</th>
                <th>排名（连续）</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${rankList}">
                <tr>
                    <td>${row['商品名称']}</td>
                    <td>${row['总销量']}</td>
                    <td>${row['排名_带间隔']}</td>
                    <td>${row['排名_连续']}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty rankList}">
                <tr>
                    <td class="empty-row" colspan="4">暂无数据</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <div class="section-title">客户消费排名</div>
    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>客户名称</th>
                <th>总消费额</th>
                <th>消费排名</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${customerRankList}">
                <tr>
                    <td>${row['客户名称']}</td>
                    <td>${row['总消费额']}</td>
                    <td>${row['消费排名']}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty customerRankList}">
                <tr>
                    <td class="empty-row" colspan="3">暂无数据</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <div class="section-title">月度销售趋势</div>
    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>月份</th>
                <th>月销售额</th>
                <th>累计销售额</th>
                <th>上月销售额</th>
                <th>环比增长</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="row" items="${trendList}">
                <tr>
                    <td>${row['月份']}</td>
                    <td>${row['月销售额']}</td>
                    <td>${row['累计销售额']}</td>
                    <td>${row['上月销售额']}</td>
                    <td>${row['环比增长']}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty trendList}">
                <tr>
                    <td class="empty-row" colspan="5">暂无数据</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>