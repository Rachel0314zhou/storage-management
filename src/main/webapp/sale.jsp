<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>销售出库</title>
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
            border-left: 4px solid #e67e22;
            padding-left: 10px;
        }

        input[type="text"],
        input[type="number"] {
            padding: 8px 10px;
            width: 200px;
            border: 1px solid #ccc;
            border-radius: 4px;
            margin-right: 8px;
            margin-bottom: 10px;
        }
        input[type="text"]:focus,
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
        }
        button:hover { opacity: 0.85; }

        .btn-warning {
            background: #e67e22;
        }
        .btn-warning:hover { background: #d35400; }

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
    <a href="${pageContext.request.contextPath}/salesStatistics">月度统计</a>
    <a href="${pageContext.request.contextPath}/windowFunctions">销售分析</a>

    <%-- ===== 业务员和管理员可见（roleId=1 或 2） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
        <a href="${pageContext.request.contextPath}/purchase">采购入库</a>
        <a href="${pageContext.request.contextPath}/sale" class="highlight">销售出库</a>
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
                销售出库管理
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="message-success">${successMessage}</div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="message-error">${errorMessage}</div>
    </c:if>

    <%-- ============================================================ --%>
    <%-- 新增销售出库（仅管理员和业务员可见） --%>
    <%-- ============================================================ --%>
    <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
        <div class="section-title">新增销售出库</div>

        <div class="tip">
            测试数据提示：商品ID可填 1、2、3、4。可添加多个商品。如果出库数量超过库存，系统会提示库存不足。
        </div>

        <form action="${pageContext.request.contextPath}/sale" method="post" id="saleForm">
            <!-- 客户名称 -->
            <div style="margin-bottom: 12px;">
                <label style="font-weight:bold;">客户名称：</label>
                <input type="text" name="customerName" placeholder="客户名称" required style="width:250px; padding:8px 10px; border:1px solid #ccc; border-radius:4px;">
            </div>

            <!-- 商品列表 -->
            <div id="productRows">
                <div class="product-row" style="margin-bottom: 8px;">
                    <label>商品ID：</label>
                    <input type="number" name="productId" placeholder="商品ID" required style="width:100px; padding:8px 10px; border:1px solid #ccc; border-radius:4px;">
                    <label style="margin-left:10px;">数量：</label>
                    <input type="number" name="quantity" placeholder="数量" required style="width:80px; padding:8px 10px; border:1px solid #ccc; border-radius:4px;">
                    <label style="margin-left:10px;">单价：</label>
                    <input type="text" name="unitPrice" placeholder="单价" required style="width:100px; padding:8px 10px; border:1px solid #ccc; border-radius:4px;">
                    <button type="button" onclick="removeRow(this)" style="margin-left:10px; padding:4px 10px; background:#c0392b; color:white; border:none; border-radius:4px; cursor:pointer;">删除</button>
                </div>
            </div>

            <div style="margin: 10px 0;">
                <button type="button" onclick="addRow()" style="padding:6px 16px; background:#7f8c8d; color:white; border:none; border-radius:4px; cursor:pointer;">+ 添加商品</button>
            </div>

            <!-- 备注 -->
            <div style="margin-bottom: 12px;">
                <label style="font-weight:bold;">备注：</label>
                <input type="text" name="remark" placeholder="备注" style="width:300px; padding:8px 10px; border:1px solid #ccc; border-radius:4px;">
            </div>

            <button type="submit" class="btn-warning">提交销售出库</button>
        </form>
    </c:if>

    <%-- ===== 只读用户看到提示（业务区域不可操作） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 3}">
        <div class="readonly-notice">
            ⚠️ 您当前为只读用户，只能查看销售记录，无法新增销售出库。
        </div>
    </c:if>

    <%-- ============================================================ --%>
    <%-- 销售记录查询（所有用户可见） --%>
    <%-- ============================================================ --%>
    <div class="section-title">销售记录查询</div>

    <form action="${pageContext.request.contextPath}/sale" method="get" style="margin-bottom: 15px;">
        <input type="hidden" name="action" value="search">
        <input type="text" name="keyword" placeholder="输入客户或商品名称" value="${keyword}">
        <button class="btn" type="submit">搜索销售记录</button>
        <a class="btn btn-gray" href="${pageContext.request.contextPath}/sale">查看全部</a>
    </form>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>销售订单ID</th>
                <th>销售日期</th>
                <th>客户名称</th>
                <th>明细ID</th>
                <th>商品名称</th>
                <th>数量</th>
                <th>单价</th>
                <th>金额</th>
                <th>备注</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${saleList}">
                <tr>
                    <td>${item.saleId}</td>
                    <td>${item.saleDate}</td>
                    <td>${item.customerName}</td>
                    <td>${item.saleItemId}</td>
                    <td>${item.productName}</td>
                    <td>${item.quantity}</td>
                    <td>${item.unitPrice}</td>
                    <td>${item.amount}</td>
                    <td>${item.remark}</td>
                </tr>
            </c:forEach>

            <c:if test="${empty saleList}">
                <tr>
                    <td class="empty-row" colspan="9">暂无销售记录</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<script>
    function addRow() {
        var container = document.getElementById("productRows");
        var row = document.createElement("div");
        row.className = "product-row";
        row.style.marginBottom = "8px";
        row.innerHTML = `
            <label>商品ID：</label>
            <input type="number" name="productId" placeholder="商品ID" required style="width:100px; padding:8px 10px; border:1px solid #ccc; border-radius:4px;">
            <label style="margin-left:10px;">数量：</label>
            <input type="number" name="quantity" placeholder="数量" required style="width:80px; padding:8px 10px; border:1px solid #ccc; border-radius:4px;">
            <label style="margin-left:10px;">单价：</label>
            <input type="text" name="unitPrice" placeholder="单价" required style="width:100px; padding:8px 10px; border:1px solid #ccc; border-radius:4px;">
            <button type="button" onclick="removeRow(this)" style="margin-left:10px; padding:4px 10px; background:#c0392b; color:white; border:none; border-radius:4px; cursor:pointer;">删除</button>
        `;
        container.appendChild(row);
    }

    function removeRow(btn) {
        var row = btn.parentElement;
        var container = document.getElementById("productRows");
        if (container.children.length > 1) {
            container.removeChild(row);
        } else {
            alert("至少保留一个商品！");
        }
    }
</script>

</body>
</html>