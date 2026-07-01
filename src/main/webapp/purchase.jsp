<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>采购管理</title>
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

    .header h1 {
      margin: 0;
      font-size: 24px;
    }

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

    .nav a:hover {
      background: #3d566e;
    }

    .nav a.highlight {
      color: #f1c40f;
    }

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

    input[type="text"],
    input[type="number"],
    select {
      padding: 8px 10px;
      width: 210px;
      border: 1px solid #ccc;
      border-radius: 4px;
      margin-right: 8px;
      margin-bottom: 10px;
      box-sizing: border-box;
    }

    input[type="text"]:focus,
    input[type="number"]:focus,
    select:focus {
      outline: none;
      border-color: #3498db;
    }

    input.small-input {
      width: 110px;
    }

    .btn,
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

    .btn:hover,
    button:hover {
      opacity: 0.85;
    }

    .btn-success {
      background: #27ae60;
    }

    .btn-success:hover {
      background: #1e8449;
    }

    .btn-orange {
      background: #e67e22;
    }

    .btn-orange:hover {
      background: #d35400;
    }

    .btn-gray {
      background: #7f8c8d;
    }

    .btn-gray:hover {
      background: #666;
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

    .table-block {
      overflow-x: auto;
    }

    table {
      width: 100%;
      border-collapse: collapse;
      font-size: 14px;
      margin-top: 10px;
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

    tr:nth-child(even) {
      background: #fafafa;
    }

    tr:hover {
      background: #f0f7ff;
    }

    .empty-row {
      text-align: center;
      color: #999;
      padding: 30px 0;
    }

    .status {
      color: #27ae60;
      font-weight: bold;
    }

    .status-pending {
      color: #e67e22;
      font-weight: bold;
    }

    .inline-form {
      margin: 0;
      white-space: nowrap;
    }

    .purchase-row {
      margin-bottom: 8px;
      padding: 8px 0;
      border-bottom: 1px dashed #ddd;
    }

    .purchase-row:last-child {
      border-bottom: none;
    }

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
    <a href="${pageContext.request.contextPath}/purchase" class="highlight">采购入库</a>
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
        采购订单与入库管理
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
  <%-- 一、新增采购订单（仅管理员和业务员可见） --%>
  <%-- ============================================================ --%>
  <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
    <div class="section-title">一、新增采购订单</div>

    <div class="tip">
      第一步只创建采购订单和采购明细，不会增加库存。<br>
      库存只有在下面“待入库采购订单”中点击“办理入库”后，才会由数据库触发器自动增加。
    </div>

    <form action="${pageContext.request.contextPath}/purchase" method="post" id="purchaseForm">
      <input type="hidden" name="action" value="createOrder">

      <div style="margin-bottom: 12px;">
        <label style="font-weight:bold;">供应商：</label>
        <select name="supplierId" id="supplierSelect" required>
          <option value="">请选择供应商</option>
          <c:forEach var="supplier" items="${supplierList}">
            <option value="${supplier.supplierId}">
                ${supplier.supplierId} - ${supplier.supplierName}
            </option>
          </c:forEach>
        </select>
      </div>

      <template id="productOptionsTemplate">
        <option value="">请选择商品</option>
        <c:forEach var="product" items="${productList}">
          <option value="${product.productId}"
                  data-supplier-id="${product.supplierId}"
                  data-price="${product.purchasePrice}">
              ${product.productId} - ${product.productName}
          </option>
        </c:forEach>
      </template>

      <div id="purchaseRows">
        <div class="purchase-row">
          <label>商品：</label>
          <select name="productId" class="product-select" required disabled>
            <option value="">请先选择供应商</option>
          </select>

          <label style="margin-left:10px;">数量：</label>
          <input type="number"
                 name="quantity"
                 placeholder="采购数量"
                 min="1"
                 required
                 style="width:90px;">

          <label style="margin-left:10px;">单价：</label>
          <input type="text"
                 name="unitPrice"
                 placeholder="采购单价"
                 required
                 style="width:110px;">

          <button type="button"
                  onclick="removePurchaseRow(this)"
                  style="margin-left:10px; padding:4px 10px; background:#c0392b;">
            删除
          </button>
        </div>
      </div>

      <div style="margin: 10px 0;">
        <button type="button"
                onclick="addPurchaseRow()"
                style="padding:6px 16px; background:#7f8c8d;">
          + 添加商品
        </button>
      </div>

      <div style="margin-bottom: 12px;">
        <label style="font-weight:bold;">备注：</label>
        <input type="text"
               name="remark"
               placeholder="备注"
               style="width:300px;">
      </div>

      <button type="submit" class="btn-success">提交采购订单</button>
    </form>
  </c:if>

  <%-- ===== 只读用户看到提示（业务区域不可操作） ===== --%>
  <c:if test="${sessionScope.currentUser.roleId == 3}">
    <div class="readonly-notice">
      ⚠️ 您当前为只读用户，只能查看采购订单记录，无法新增采购订单或办理入库。
    </div>
  </c:if>

  <%-- ============================================================ --%>
  <%-- 二、待入库采购订单（办理入库仅管理员和业务员可见） --%>
  <%-- ============================================================ --%>
  <div class="section-title">二、待入库采购订单</div>

  <div class="tip">
    这里显示已经创建但尚未完全入库的采购订单。点击“办理入库”后会生成进货单，库存增加由触发器完成。
  </div>

  <div class="table-block">
    <table>
      <thead>
      <tr>
        <th>采购订单ID</th>
        <th>采购日期</th>
        <th>状态</th>
        <th>供应商</th>
        <th>商品ID</th>
        <th>商品名称</th>
        <th>采购数量</th>
        <th>已入库</th>
        <th>剩余入库</th>
        <th>单价</th>
        <th>金额</th>
        <th>办理入库</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="order" items="${pendingOrderList}">
        <tr>
          <td>${order.purchaseId}</td>
          <td>${order.purchaseDate}</td>
          <td><span class="status-pending">${order.status}</span></td>
          <td>${order.supplierName}</td>
          <td>${order.productId}</td>
          <td>${order.productName}</td>
          <td>${order.orderQuantity}</td>
          <td>${order.stockedInQuantity}</td>
          <td>${order.remainingQuantity}</td>
          <td>${order.unitPrice}</td>
          <td>${order.amount}</td>
          <td>
              <%-- 只有非只读用户才能看到“办理入库”表单 --%>
            <c:if test="${sessionScope.currentUser.roleId == 1 or sessionScope.currentUser.roleId == 2}">
              <form class="inline-form" action="${pageContext.request.contextPath}/purchase" method="post">
                <input type="hidden" name="action" value="stockIn">
                <input type="hidden" name="purchaseId" value="${order.purchaseId}">
                <input type="hidden" name="productId" value="${order.productId}">
                <input class="small-input"
                       type="number"
                       name="stockInQuantity"
                       min="1"
                       max="${order.remainingQuantity}"
                       placeholder="数量"
                       required>
                <input class="small-input" type="text" name="remark" placeholder="备注">
                <button type="submit" class="btn-orange">办理入库</button>
              </form>
            </c:if>
            <c:if test="${sessionScope.currentUser.roleId == 3}">
              <span class="readonly-tag">只读</span>
            </c:if>
          </td>
        </tr>
      </c:forEach>

      <c:if test="${empty pendingOrderList}">
        <tr>
          <td class="empty-row" colspan="12">暂无待入库采购订单</td>
        </tr>
      </c:if>
      </tbody>
    </table>
  </div>

  <%-- ============================================================ --%>
  <%-- 三、采购记录查询（所有用户可见） --%>
  <%-- ============================================================ --%>
  <div class="section-title">三、采购记录查询</div>

  <div class="section-title">3.1 采购订单记录</div>

  <form action="${pageContext.request.contextPath}/purchase" method="get" style="margin-bottom: 15px;">
    <input type="text"
           name="purchaseKeyword"
           placeholder="输入供应商或商品名称查询采购订单"
           value="${purchaseKeyword}">

    <input type="hidden" name="stockInKeyword" value="${stockInKeyword}">

    <button class="btn" type="submit">搜索采购订单</button>
    <a class="btn btn-gray" href="${pageContext.request.contextPath}/purchase?stockInKeyword=${stockInKeyword}">
      清空订单查询
    </a>
  </form>

  <div class="table-block">
    <table>
      <thead>
      <tr>
        <th>采购订单ID</th>
        <th>采购日期</th>
        <th>状态</th>
        <th>供应商</th>
        <th>明细ID</th>
        <th>商品名称</th>
        <th>采购数量</th>
        <th>单价</th>
        <th>金额</th>
        <th>备注</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="item" items="${purchaseList}">
        <tr>
          <td>${item.purchaseId}</td>
          <td>${item.purchaseDate}</td>
          <td><span class="status">${item.status}</span></td>
          <td>${item.supplierName}</td>
          <td>${item.purchaseItemId}</td>
          <td>${item.productName}</td>
          <td>${item.quantity}</td>
          <td>${item.unitPrice}</td>
          <td>${item.amount}</td>
          <td>${item.remark}</td>
        </tr>
      </c:forEach>

      <c:if test="${empty purchaseList}">
        <tr>
          <td class="empty-row" colspan="10">暂无采购订单记录</td>
        </tr>
      </c:if>
      </tbody>
    </table>
  </div>

  <div class="section-title">3.2 采购入库明细 / 进货单记录</div>

  <form action="${pageContext.request.contextPath}/purchase" method="get" style="margin-bottom: 15px;">
    <input type="hidden" name="purchaseKeyword" value="${purchaseKeyword}">

    <input type="text"
           name="stockInKeyword"
           placeholder="输入供应商、商品名称或备注查询进货单"
           value="${stockInKeyword}">

    <button class="btn" type="submit">搜索进货单</button>
    <a class="btn btn-gray" href="${pageContext.request.contextPath}/purchase?purchaseKeyword=${purchaseKeyword}">
      清空进货单查询
    </a>
  </form>

  <div class="table-block">
    <table>
      <thead>
      <tr>
        <th>入库单ID</th>
        <th>采购订单ID</th>
        <th>入库时间</th>
        <th>订单状态</th>
        <th>供应商</th>
        <th>商品ID</th>
        <th>商品名称</th>
        <th>入库数量</th>
        <th>采购单价</th>
        <th>入库金额</th>
        <th>备注</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="stockIn" items="${stockInList}">
        <tr>
          <td>${stockIn.stockInId}</td>
          <td>${stockIn.purchaseId}</td>
          <td>${stockIn.stockInTime}</td>
          <td><span class="status">${stockIn.purchaseStatus}</span></td>
          <td>${stockIn.supplierName}</td>
          <td>${stockIn.productId}</td>
          <td>${stockIn.productName}</td>
          <td>${stockIn.stockInQuantity}</td>
          <td>${stockIn.unitPrice}</td>
          <td>${stockIn.stockInAmount}</td>
          <td>${stockIn.remark}</td>
        </tr>
      </c:forEach>

      <c:if test="${empty stockInList}">
        <tr>
          <td class="empty-row" colspan="11">暂无采购入库记录</td>
        </tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>

<script>
  document.addEventListener("DOMContentLoaded", function () {
    const supplierSelect = document.getElementById("supplierSelect");

    if (!supplierSelect) {
      return;
    }

    supplierSelect.addEventListener("change", function () {
      refreshAllProductSelects();
    });

    document.querySelectorAll(".purchase-row").forEach(function (row) {
      bindProductSelect(row);
    });

    refreshAllProductSelects();
  });

  function getFilteredProductOptionsHtml() {
    const supplierSelect = document.getElementById("supplierSelect");
    const template = document.getElementById("productOptionsTemplate");

    if (!supplierSelect || !template) {
      return '<option value="">商品数据加载失败</option>';
    }

    const supplierId = supplierSelect.value;

    if (!supplierId) {
      return '<option value="">请先选择供应商</option>';
    }

    const allOptions = Array.from(template.content.querySelectorAll("option"));
    let html = '<option value="">请选择商品</option>';
    let matchedCount = 0;

    allOptions.forEach(function (option) {
      if (option.value !== "" && option.dataset.supplierId === supplierId) {
        html += option.outerHTML;
        matchedCount++;
      }
    });

    if (matchedCount === 0) {
      html = '<option value="">该供应商暂无商品</option>';
    }

    return html;
  }

  function refreshAllProductSelects() {
    const supplierSelect = document.getElementById("supplierSelect");
    const supplierId = supplierSelect.value;
    const html = getFilteredProductOptionsHtml();
    const productSelects = document.querySelectorAll(".product-select");

    productSelects.forEach(function (select) {

      // 🔥 先保存当前选中的值
      const oldValue = select.value;

      select.innerHTML = html;

      // 🔥 恢复选中值
      if (oldValue) {
        select.value = oldValue;
      }

      if (!supplierId || html.indexOf("该供应商暂无商品") !== -1) {
        select.disabled = true;
      } else {
        select.disabled = false;
      }
    });

    document.querySelectorAll("#purchaseRows input[name='unitPrice']").forEach(function (input) {
      
    });
  }

  function bindProductSelect(row) {
    const select = row.querySelector(".product-select");
    const priceInput = row.querySelector("input[name='unitPrice']");

    if (!select || !priceInput) {
      return;
    }

    select.addEventListener("change", function () {
      const selectedOption = select.options[select.selectedIndex];

      if (selectedOption && selectedOption.dataset.price) {
        priceInput.value = selectedOption.dataset.price;
      } else {
        priceInput.value = "";
      }
    });
  }

  function addPurchaseRow() {
    const container = document.getElementById("purchaseRows");
    const row = document.createElement("div");
    const supplierSelect = document.getElementById("supplierSelect");
    const supplierId = supplierSelect ? supplierSelect.value : "";
    const productOptionsHtml = getFilteredProductOptionsHtml();
    // setTimeout(refreshAllProductSelects, 0);
    row.className = "purchase-row";

    row.innerHTML = `
      <label>商品：</label>
      <select name="productId" class="product-select" required>
        ${productOptionsHtml}
      </select>

      <label style="margin-left:10px;">数量：</label>
      <input type="number"
             name="quantity"
             placeholder="采购数量"
             min="1"
             required
             style="width:90px;">

      <label style="margin-left:10px;">单价：</label>
      <input type="text"
             name="unitPrice"
             placeholder="采购单价"
             required
             style="width:110px;">

      <button type="button"
              onclick="removePurchaseRow(this)"
              style="margin-left:10px; padding:4px 10px; background:#c0392b;">
        删除
      </button>
    `;

    container.appendChild(row);

    const select = row.querySelector(".product-select");
    if (!supplierId || productOptionsHtml.indexOf("该供应商暂无商品") !== -1) {
      select.disabled = true;
    }

    bindProductSelect(row);
    refreshAllProductSelects();
  }

  function removePurchaseRow(btn) {
    const row = btn.parentElement;
    const container = document.getElementById("purchaseRows");

    if (container.children.length > 1) {
      container.removeChild(row);
    } else {
      alert("至少保留一个商品！");
    }
  }
</script>

</body>
</html>