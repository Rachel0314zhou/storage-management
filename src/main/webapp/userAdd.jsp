<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>添加用户</title>
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

        .form-group {
            display: flex;
            margin-bottom: 15px;
            align-items: center;
        }
        .form-group label {
            width: 80px;
            font-weight: bold;
            color: #333;
            flex-shrink: 0;
        }
        .form-group input,
        .form-group select {
            flex: 1;
            padding: 8px 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #3498db;
        }

        .btn, button {
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
        .btn:hover, button:hover { opacity: 0.85; }
        .btn-green { background: #27ae60; }
        .btn-green:hover { background: #1e8449; }
        .btn-gray { background: #7f8c8d; }
        .btn-gray:hover { background: #666; }

        .message-error {
            background: #fdecea;
            color: #c0392b;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
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
    <a href="${pageContext.request.contextPath}/windowFunctions">销售分析</a>
    <a href="${pageContext.request.contextPath}/user" class="highlight">用户管理</a>
    <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
</div>

<div class="container">
    <div class="title">添加用户</div>

    <!-- 1. 优先显示请求转发传递的错误信息（解决中文乱码问题） -->
    <c:if test="${not empty error}">
        <div class="message-error">${error}</div>
    </c:if>

    <!-- 2. 兼容旧的 URL 参数传错方式（保留） -->
    <c:if test="${not empty param.error}">
        <div class="message-error">${param.error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/user?action=add" method="post">
        <div class="form-group">
            <label>用户名：</label>
            <input type="text" name="username" required>
        </div>
        <div class="form-group">
            <label>密码：</label>
            <input type="password" name="password" required>
        </div>
        <div class="form-group">
            <label>角色：</label>
            <select name="roleId">
                <c:forEach var="r" items="${roleList}">
                    <option value="${r.roleId}">${r.roleName}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group" style="justify-content:center; gap:10px;">
            <button class="btn-green" type="submit">添加</button>
            <a href="${pageContext.request.contextPath}/user" class="btn btn-gray">返回</a>
        </div>
    </form>
</div>

</body>
</html>