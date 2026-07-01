<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>备份与恢复</title>
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
            border-left: 4px solid #e67e22;
            padding-left: 10px;
        }

        .message-box {
            padding: 15px;
            border-radius: 4px;
            margin: 15px 0;
            border-left: 4px solid #3498db;
            background: #f0f0f0;
        }
        .message-box.success { border-left-color: #27ae60; background: #eafaf1; }
        .message-box.error { border-left-color: #e74c3c; background: #fdecea; }
        .message-box pre {
            margin: 0;
            white-space: pre-wrap;
            font-family: inherit;
            font-size: 14px;
        }

        .btn, button {
            padding: 10px 24px;
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
        .btn-warning { background: #e67e22; }
        .btn-warning:hover { background: #d35400; }
        .btn-gray { background: #7f8c8d; }
        .btn-gray:hover { background: #666; }

        input[type="text"] {
            padding: 8px 12px;
            width: 500px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 13px;
        }
        input[type="text"]:focus {
            outline: none;
            border-color: #3498db;
        }

        .form-group {
            margin: 12px 0;
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 10px;
        }
        .form-group label {
            font-weight: bold;
            color: #333;
            min-width: 100px;
        }

        .tip {
            color: #777;
            font-size: 13px;
            margin: 10px 0;
            line-height: 1.8;
        }
        .tip strong { color: #c0392b; }

        .backup-form {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            margin: 10px 0;
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
        <a href="${pageContext.request.contextPath}/purchaseReturn">采购退货</a>
        <a href="${pageContext.request.contextPath}/salesReturn">销售退货</a>
    </c:if>

    <%-- ===== 仅管理员可见（roleId=1） ===== --%>
    <c:if test="${sessionScope.currentUser.roleId == 1}">
        <a href="${pageContext.request.contextPath}/user">用户管理</a>
        <a href="${pageContext.request.contextPath}/backup" class="highlight">备份恢复</a>
    </c:if>
</div>

<div class="container">
    <div class="title">数据库备份与恢复</div>

    <!-- 消息显示区域 -->
    <c:if test="${not empty msg}">
        <div class="message-box ${msg.contains('成功') ? 'success' : 'error'}">
            <pre>${msg}</pre>
        </div>
    </c:if>

    <!-- ==================== 备份 ==================== -->
    <div class="section-title">一、备份数据库</div>
    <div class="tip">
        将当前数据库完整备份到指定目录。<br>
        <strong>提示：</strong>留空则使用默认路径（项目根目录）。
    </div>

    <div class="backup-form">
        <form action="${pageContext.request.contextPath}/backup" method="get">
            <input type="hidden" name="action" value="backup">

            <div class="form-group">
                <label for="saveDir">📂 保存路径：</label>
                <input type="text" id="saveDir" name="saveDir"
                       placeholder="例如 D:/backup/ （留空则使用项目根目录）"
                       style="width: 400px;">
            </div>

            <div class="form-group" style="margin-top: 15px;">
                <label></label>
                <button class="btn-green" type="submit" onclick="return confirm('确认备份当前数据库？')">执行备份</button>
            </div>
        </form>
    </div>

    <!-- ==================== 恢复 ==================== -->
    <div class="section-title">二、恢复数据库</div>
    <div class="tip">
        输入备份文件的<strong>完整路径</strong>（包括文件名），点击恢复。<br>
        <strong>警告：恢复操作将覆盖当前数据！</strong>
    </div>

    <form action="${pageContext.request.contextPath}/backup" method="get">
        <input type="hidden" name="action" value="restore">
        <div class="form-group">
            <label for="filePath">📁 文件路径：</label>
            <input type="text" id="filePath" name="filePath"
                   placeholder="例如 C:/Users/你的用户名/Desktop/backup_2026-06-30_11-13-00.sql"
                   style="width: 500px;">
            <button class="btn-warning" type="submit"
                    onclick="return confirm('确认恢复数据？将覆盖当前数据！')">执行恢复</button>
        </div>
    </form>

    <div style="margin-top: 30px; border-top: 1px solid #eee; padding-top: 15px;">
        <a href="${pageContext.request.contextPath}/" class="btn btn-gray">返回</a>
    </div>
</div>

</body>
</html>