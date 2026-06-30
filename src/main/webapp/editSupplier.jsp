<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>

<%
    Map<String,Object> s =
            (Map<String,Object>)request.getAttribute("supplier");
%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>修改供应商</title>
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
            width: 50%;
            margin: 25px auto;
            background: white;
            padding: 30px;
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
            margin-bottom: 15px;
            align-items: center;
        }
        .form-row label {
            width: 100px;
            font-weight: bold;
            color: #333;
            flex-shrink: 0;
        }
        .form-row input {
            flex: 1;
            padding: 8px 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-row input:focus {
            outline: none;
            border-color: #3498db;
        }

        .actions {
            text-align: center;
            margin-top: 20px;
        }
        .actions .btn { margin: 0 8px; }

        .btn {
            padding: 8px 20px;
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
        .btn-gray { background: #7f8c8d; }
    </style>
</head>
<body>

<!-- ===== 顶部导航栏 ===== -->
<div class="header">
    <h1>仓储管理系统</h1>
</div>

<div class="nav">
    <a href="${pageContext.request.contextPath}/product">商品管理</a>
    <a href="${pageContext.request.contextPath}/category">商品分类</a>
    <a href="${pageContext.request.contextPath}/supplier" class="highlight">供应商管理</a>
    <a href="${pageContext.request.contextPath}/customer">客户管理</a>
    <a href="${pageContext.request.contextPath}/inventory">库存管理</a>
    <a href="${pageContext.request.contextPath}/purchase">采购入库</a>
    <a href="${pageContext.request.contextPath}/sale">销售出库</a>
    <a href="${pageContext.request.contextPath}/inventoryLog">库存流水</a>
    <a href="${pageContext.request.contextPath}/purchaseReturn">采购退货</a>
    <a href="${pageContext.request.contextPath}/salesReturn">销售退货</a>
    <a href="${pageContext.request.contextPath}/salesStatistics">月度统计</a>
    <a href="${pageContext.request.contextPath}/windowFunctions">销售分析</a>
    <a href="${pageContext.request.contextPath}/user">用户管理</a>
    <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
</div>

<!-- ===== 主内容 ===== -->
<div class="container">
    <div class="title">修改供应商</div>

    <form action="<%=request.getContextPath()%>/supplier?action=update" method="post">
        <input type="hidden" name="supplier_id" value="<%=s.get("supplier_id")%>">

        <div class="form-row">
            <label>供应商名称</label>
            <input name="supplier_name" value="<%=s.get("supplier_name")%>">
        </div>
        <div class="form-row">
            <label>联系人</label>
            <input name="contact_name" value="<%=s.get("contact_name")%>">
        </div>
        <div class="form-row">
            <label>联系方式</label>
            <input name="phone" value="<%=s.get("phone")%>">
        </div>
        <div class="form-row">
            <label>地址</label>
            <input name="address" value="<%=s.get("address")%>">
        </div>

        <div class="actions">
            <button class="btn btn-green" type="submit">保存修改</button>
            <a class="btn btn-gray" href="<%=request.getContextPath()%>/supplier">返回供应商列表</a>
        </div>
    </form>
</div>

</body>
</html>