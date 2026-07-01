<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>销售退货</title>
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

    select, input[type="text"], input[type="number"] {
      padding: 8px 10px;
      width: 200px;
      border: 1px solid #ccc;
      border-radius: 4px;
      margin-right: 8px;
      margin-bottom: 10px;
    }
    select:focus, input[type="text"]:focus, input[type="number"]:focus {
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

    .btn-warning {
      background: #e67e22;
    }
    .btn-warning:hover { background: #d35400; }

    .btn-success {
      background: #27ae60;
    }
    .btn-success:hover { background: #1e8449; }

    .btn-danger {
      background: #c0392b;
    }
    .btn-danger:hover { background: #922b21; }

    .btn-gray {
      background: #7f8c8d;
    }
    .btn-gray:hover { background: #666; }

    .btn-sm {
      padding: 4px 10px;
      font-size: 12px;
      margin: 2px;
    }

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
      margin-top: 12px;
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

    .status-pending { color: #f39c12; font-weight: bold; }
    .status-approved { color: #27ae60; font-weight: bold; }
    .status-rejected { color: #e74c3c; font-weight: bold; }
    .status-returned { color: #2980b9; font-weight: bold; }
    .status-cancelled { color: #95a5a6; font-weight: bold; }

    .readonly-tag {
      color: #999;
      font-size: 12px;
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
    <a href="${pageContext.request.contextPath}/salesReturn" class="highlight">销售退货</a>
  </c:if>

  <%-- ===== 仅管理员可见（roleId=1） ===== --%>
  <c:if test="${sessionScope.currentUser.roleId == 1}">
    <a href="${pageContext.request.contextPath}/user">用户管理</a>
    <a href="${pageContext.request.contextPath}/backup">备份恢复</a>
  </c:if>
</div>

<div class="container">
  <div class="title">销售退货管理</div>

  <c:if test="${not empty successMessage}">
    <div class="message-success">${successMessage}</div>
  </c:if>

  <c:if test="${not empty errorMessage}">
    <div class="message-error">${errorMessage}</div>
  </c:if>

  <!-- ========== 可退货商品列表 ========== -->
  <div class="section-title">可退货的商品</div>
  <div class="tip">
    只有已完成且未全部退货的订单才能退货。选择订单和商品后，输入退货数量提交。
  </div>

  <form action="${pageContext.request.contextPath}/salesReturn" method="get" style="margin-bottom:15px;">
    <input type="hidden" name="action" value="search">
    <input type="text" name="keyword" placeholder="输入客户或商品名称" value="${keyword}">
    <button class="btn" type="submit">搜索</button>
    <a class="btn btn-gray" href="${pageContext.request.contextPath}/salesReturn">查看全部可退货商品</a>
  </form>

  <div class="table-wrap">
    <table>
      <thead>
      <tr>
        <th>订单ID</th>
        <th>客户</th>
        <th>订单日期</th>
        <th>商品</th>
        <th>可退货数量</th>
        <th>单价</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="item" items="${orderList}">
        <tr>
          <td>${item['sales_order_id']}</td>
          <td>${item['customer_name']}</td>
          <td>${item['order_date']}</td>
          <td>${item['product_name']}</td>
          <td>${item['can_return']}</td>
          <td>${item['unit_price']}</td>
          <td>
              <%-- 只有非只读用户（管理员或业务员）才能看到“申请退货”表单 --%>
            <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
              <form action="${pageContext.request.contextPath}/salesReturn" method="post" style="display:inline;">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="salesOrderId" value="${item['sales_order_id']}">
                <input type="hidden" name="productId" value="${item['product_id']}">
                <input type="number" name="returnQuantity" placeholder="数量" max="${item['can_return']}" min="1" style="width:60px;padding:4px;" required>
                <input type="text" name="reason" placeholder="退货原因" style="width:100px;padding:4px;">
                <button type="submit" class="btn-warning btn-sm">申请退货</button>
              </form>
            </c:if>
            <c:if test="${sessionScope.currentUser.roleId == 3}">
              <span class="readonly-tag">只读</span>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty orderList}">
        <tr>
          <td class="empty-row" colspan="7">暂无可以退货的商品</td>
        </tr>
      </c:if>
      </tbody>
    </table>
  </div>

  <!-- ========== 退货记录 ========== -->
  <div class="section-title">退货记录</div>

  <%-- ===== 只读用户提示（只读用户不能操作退货审核） ===== --%>
  <c:if test="${sessionScope.currentUser.roleId == 3}">
    <div class="readonly-notice">
      ⚠️ 您当前为只读用户，只能查看退货记录，无法执行审核、入库等操作。
    </div>
  </c:if>

  <div class="table-wrap">
    <table>
      <thead>
      <tr>
        <th>退货ID</th>
        <th>订单ID</th>
        <th>客户</th>
        <th>商品</th>
        <th>数量</th>
        <th>金额</th>
        <th>退货日期</th>
        <th>原因</th>
        <th>状态</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="item" items="${returnList}">
        <tr>
          <td>${item['sales_return_id']}</td>
          <td>${item['sales_order_id']}</td>
          <td>${item['customer_name']}</td>
          <td>${item['product_name']}</td>
          <td>${item['return_quantity']}</td>
          <td>${item['return_amount']}</td>
          <td>${item['return_date']}</td>
          <td>${item['reason']}</td>
          <td>
            <c:choose>
              <c:when test="${item['status'] == 1}"><span class="status-pending">待审核</span></c:when>
              <c:when test="${item['status'] == 2}"><span class="status-approved">已通过</span></c:when>
              <c:when test="${item['status'] == 3}"><span class="status-rejected">已驳回</span></c:when>
              <c:when test="${item['status'] == 4}"><span class="status-returned">已入库</span></c:when>
              <c:when test="${item['status'] == 5}"><span class="status-cancelled">已作废</span></c:when>
              <c:otherwise>未知</c:otherwise>
            </c:choose>
          </td>
          <td>
              <%-- 只有非只读用户（管理员或业务员）才能看到操作按钮 --%>
            <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
              <!-- 待审核：显示【通过】【驳回】 -->
              <c:if test="${item['status'] == 1}">
                <form action="${pageContext.request.contextPath}/salesReturn" method="post" style="display:inline;">
                  <input type="hidden" name="action" value="approve">
                  <input type="hidden" name="returnId" value="${item['sales_return_id']}">
                  <button type="submit" class="btn-success btn-sm">通过</button>
                </form>
                <form action="${pageContext.request.contextPath}/salesReturn" method="post" style="display:inline;">
                  <input type="hidden" name="action" value="reject">
                  <input type="hidden" name="returnId" value="${item['sales_return_id']}">
                  <button type="submit" class="btn-danger btn-sm">驳回</button>
                </form>
              </c:if>
              <!-- 已通过：显示【确认入库】和【作废】 -->
              <c:if test="${item['status'] == 2}">
                <form action="${pageContext.request.contextPath}/salesReturn" method="post" style="display:inline;">
                  <input type="hidden" name="action" value="confirm">
                  <input type="hidden" name="returnId" value="${item['sales_return_id']}">
                  <button type="submit" class="btn-success btn-sm">入库</button>
                </form>
                <form action="${pageContext.request.contextPath}/salesReturn" method="post" style="display:inline;">
                  <input type="hidden" name="action" value="noStockIn">
                  <input type="hidden" name="returnId" value="${item['sales_return_id']}">
                  <button type="submit" class="btn-danger btn-sm">作废</button>
                </form>
              </c:if>
              <!-- 已入库/已驳回/已作废：无操作 -->
              <c:if test="${item['status'] == 3 || item['status'] == 4 || item['status'] == 5}">
                <span style="color:#999;font-size:12px;">—</span>
              </c:if>
            </c:if>
            <c:if test="${sessionScope.currentUser.roleId == 3}">
              <span class="readonly-tag">只读</span>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty returnList}">
        <tr>
          <td class="empty-row" colspan="10">暂无退货记录</td>
        </tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>

</body>
</html>