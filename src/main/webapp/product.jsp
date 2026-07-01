<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%-- 引入 JSTL 核心标签库，用于权限判断 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品管理</title>
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
        .nav .logout-link { color: #e74c3c; margin-left: auto; }

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

        .toolbar {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 10px;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
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
        .btn-green { background: #27ae60; }
        .btn-blue { background: #3498db; }
        .btn-gray { background: #7f8c8d; }
        .btn-red { background: #e74c3c; }
        .btn-small { padding: 4px 10px; font-size: 12px; }

        .search-form {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 8px;
            margin-bottom: 15px;
            padding: 10px 0;
        }
        .search-form input {
            padding: 7px 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 13px;
            width: 140px;
        }
        .search-form input:focus {
            outline: none;
            border-color: #3498db;
        }

        .table-wrap { overflow-x: auto; }
        .data-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }
        .data-table th {
            background: #ecf0f1;
            color: #333;
            padding: 10px 12px;
            border: 1px solid #ddd;
            text-align: center;
        }
        .data-table td {
            padding: 9px 12px;
            border: 1px solid #ddd;
            text-align: center;
        }
        .data-table tr:nth-child(even) { background: #fafafa; }
        .data-table tr:hover { background: #f0f7ff; }
        .data-table .empty-row { text-align: center; color: #999; padding: 30px 0; }
        .readonly-tag {
            color: #999;
            font-size: 12px;
        }
    </style>
</head>
<body>

<%
    // 从 Session 中获取当前登录用户（用于JSP脚本片段中判断）
    // 同时也在 JSTL 中通过 ${sessionScope.currentUser} 获取
%>

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
    <a href="${pageContext.request.contextPath}/product" class="highlight">商品管理</a>
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
        <a href="${pageContext.request.contextPath}/purchaseReturn">采购退货</a>
        <a href="${pageContext.request.contextPath}/salesReturn">销售退货</a>
    </c:if>

    <%-- ===== 仅管理员可见（roleId=1） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 1}">
        <a href="${pageContext.request.contextPath}/user">用户管理</a>
        <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
    </c:if>
</div>

<!-- ===== 主内容 ===== -->
<div class="container">
    <div class="title">商品列表</div>

    <div class="toolbar">
        <%-- 只有非只读用户（管理员或业务员）才能看到“新增商品”按钮 --%>
        <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
            <a class="btn btn-green" href="<%=request.getContextPath()%>/addProduct.jsp">+ 新增商品</a>
        </c:if>
        <a class="btn btn-blue" href="<%=request.getContextPath()%>/category">分类管理</a>
        <a class="btn btn-blue" href="<%=request.getContextPath()%>/supplier">供应商管理</a>
        <a class="btn btn-gray" href="<%=request.getContextPath()%>/customer">客户管理</a>
    </div>

    <!-- 搜索表单（所有用户可见） -->
    <form class="search-form" action="<%=request.getContextPath()%>/product" method="get">
        <input name="product_code" placeholder="商品编号"
               value="<%=request.getAttribute("product_code") == null ? "" : request.getAttribute("product_code")%>">
        <input name="product_name" placeholder="商品名称"
               value="<%=request.getAttribute("product_name") == null ? "" : request.getAttribute("product_name")%>">
        <input name="supplier_id" placeholder="供应商编号"
               value="<%=request.getAttribute("supplier_id") == null ? "" : request.getAttribute("supplier_id")%>">
        <input name="category_id" placeholder="商品类别编号"
               value="<%=request.getAttribute("category_id") == null ? "" : request.getAttribute("category_id")%>">
        <button class="btn btn-blue" type="submit">查询</button>
        <a class="btn btn-gray" href="<%=request.getContextPath()%>/product">查看全部</a>
    </form>

    <div class="table-wrap">
        <table class="data-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>商品编号</th>
                <th>商品名称</th>
                <th>分类</th>
                <th>供应商</th>
                <th>采购价</th>
                <th>销售价</th>
                <th>更新时间</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Map<String,Object>> list =
                        (List<Map<String,Object>>)request.getAttribute("list");

                if(list != null && !list.isEmpty()){
                    for(Map<String,Object> p : list){
            %>
            <tr>
                <td><%=p.get("product_id")%></td>
                <td><%=p.get("product_code")%></td>
                <td><%=p.get("product_name")%></td>
                <td><%=p.get("category_name")%></td>
                <td><%=p.get("supplier_name")%></td>
                <td><%=p.get("purchase_price")%></td>
                <td><%=p.get("sale_price")%></td>
                <td><%=p.get("updated_at")%></td>
                <td>
                    <%-- 只有非只读用户才能看到“修改”和“删除”按钮 --%>
                    <c:set var="roleId" value="${sessionScope.currentUser.roleId}" />
                    <c:choose>
                        <c:when test="${roleId == 1 or roleId == 2}">
                            <a class="btn btn-green btn-small" href="<%=request.getContextPath()%>/product?action=edit&id=<%=p.get("product_id")%>">修改</a>
                            <a class="btn btn-red btn-small" href="<%=request.getContextPath()%>/product?action=delete&id=<%=p.get("product_id")%>" onclick="return confirm('确认删除该商品？')">删除</a>
                        </c:when>
                        <c:otherwise>
                            <span class="readonly-tag">只读</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td class="empty-row" colspan="9">暂无商品数据</td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>