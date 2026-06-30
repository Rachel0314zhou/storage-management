<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品分类管理</title>
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
            margin-bottom: 20px;
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
        .btn-gray { background: #7f8c8d; }
        .btn-red { background: #e74c3c; }
        .btn-small { padding: 4px 10px; font-size: 12px; }

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
            text-align: left;
        }
        .data-table td {
            padding: 9px 12px;
            border: 1px solid #ddd;
            text-align: left;
        }
        .data-table tr:nth-child(even) { background: #fafafa; }
        .data-table tr:hover { background: #f0f7ff; }
        .data-table .empty-row { text-align: center; color: #999; padding: 30px 0; }
    </style>
</head>
<body>

<!-- ===== 顶部导航栏 ===== -->
<div class="header">
    <h1>仓储管理系统</h1>
</div>

<div class="nav">
    <a href="${pageContext.request.contextPath}/product">商品管理</a>
    <a href="${pageContext.request.contextPath}/category" class="highlight">商品分类</a>
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
    <a href="${pageContext.request.contextPath}/user">用户管理</a>
    <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
</div>

<!-- ===== 主内容 ===== -->
<div class="container">
    <div class="title">商品分类列表</div>

    <div class="toolbar">
        <a class="btn btn-green" href="<%=request.getContextPath()%>/addCategory.jsp">+ 新增分类</a>
        <a class="btn btn-gray" href="<%=request.getContextPath()%>/product">返回商品列表</a>
    </div>

    <div class="table-wrap">
        <table class="data-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>分类名称</th>
                <th>分类说明</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Map<String,Object>> list =
                        (List<Map<String,Object>>)request.getAttribute("list");

                if(list != null && !list.isEmpty()){
                    for(Map<String,Object> c : list){
            %>
            <tr>
                <td><%=c.get("category_id")%></td>
                <td><%=c.get("category_name")%></td>
                <td><%=c.get("description")%></td>
                <td>
                    <a class="btn btn-green btn-small" href="<%=request.getContextPath()%>/category?action=edit&id=<%=c.get("category_id")%>">修改</a>
                    <a class="btn btn-red btn-small" href="<%=request.getContextPath()%>/category?action=delete&id=<%=c.get("category_id")%>" onclick="return confirm('确认删除该分类？')">删除</a>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td class="empty-row" colspan="4">暂无分类数据</td>
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