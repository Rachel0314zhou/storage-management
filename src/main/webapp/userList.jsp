<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户管理</title>
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

        .toolbar {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
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
        .btn-add { background: #27ae60; }
        .btn-add:hover { background: #1e8449; }
        .btn-edit { background: #f39c12; }
        .btn-edit:hover { background: #d68910; }
        .btn-del { background: #e74c3c; }
        .btn-del:hover { background: #c0392b; }
        .btn-gray { background: #7f8c8d; }
        .btn-gray:hover { background: #666; }
        .btn-small { padding: 4px 10px; font-size: 12px; }

        .message-error {
            background: #fdecea;
            color: #c0392b;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }

        .table-wrap { overflow-x: auto; }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
            margin-top: 15px;
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
    <a href="${pageContext.request.contextPath}/windowFunctions">销售分析</a>
    <a href="${pageContext.request.contextPath}/user" class="highlight">用户管理</a>
    <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
</div>

<div class="container">
    <div class="title">用户列表</div>

    <!-- 1. 优先显示请求转发传递的错误信息（解决中文乱码问题） -->
    <c:if test="${not empty error}">
        <div class="message-error">${error}</div>
    </c:if>

    <!-- 2. 兼容旧的 URL 参数传错方式（保留） -->
    <c:if test="${not empty param.error}">
        <div class="message-error">${param.error}</div>
    </c:if>

    <div class="toolbar">
        <a href="${pageContext.request.contextPath}/user?action=toAdd" class="btn btn-add">+ 添加用户</a>
    </div>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>用户ID</th>
                <th>用户名</th>
                <th>角色</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="u" items="${userList}">
                <tr>
                    <td>${u.userId}</td>
                    <td>${u.username}</td>
                    <td>${u.roleName != null ? u.roleName : '未分配'}</td>
                    <td>${u.status == 1 ? '启用' : '禁用'}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/user?action=toEdit&id=${u.userId}" class="btn btn-edit btn-small">编辑</a>
                        <a href="${pageContext.request.contextPath}/user?action=delete&id=${u.userId}" class="btn btn-del btn-small" onclick="return confirm('确认删除用户 ${u.username}？')">删除</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty userList}">
                <tr>
                    <td class="empty-row" colspan="5">暂无用户数据</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>