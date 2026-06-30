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

    .btn-success {
      background: #27ae60;
    }
    .btn-success:hover { background: #1e8449; }

    .btn-orange {
      background: #e67e22;
    }
    .btn-orange:hover { background: #d35400; }

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

    tr:nth-child(even) { background: #fafafa; }
    tr:hover { background: #f0f7ff; }
    .empty-row { text-align: center; color: #999; padding: 30px 0; }

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
  <a href="${pageContext.request.contextPath}/purchase" class="highlight">采购入库</a>
  <a href="${pageContext.request.contextPath}/sale">销售出库</a>
  <a href="${pageContext.request.contextPath}/inventoryLog">库存流水</a>
  <a href="${pageContext.request.contextPath}/purchaseReturn">采购退货</a>
  <a href="${pageContext.request.contextPath}/salesReturn">销售退货</a>
  <a href="${pageContext.request.contextPath}/salesStatistics">月度统计</a>
  <a href="${pageContext.request.contextPath}/windowFunctions">销售分析</a>
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

  <div class="section-title">一、新增采购订单</div>

  <div class="tip">
    第一步只创建采购订单和采购明细，不会增加库存。<br>
    库存只有在下面“待入库采购订单”中点击“办理入库”后，才会由数据库触发器自动增加。
  </div>

  <form action="${pageContext.request.contextPath}/purchase" method="post">
    <input type="hidden" name="action" value="createOrder">

    <select name="supplierId" id="supplierSelect" required>
      <option value="">请选择供应商</option>
      <c:forEach var="supplier" items="${supplierList}">
        <option value="${supplier.supplierId}">
            ${supplier.supplierId} - ${supplier.supplierName}
        </option>
      </c:forEach>
    </select>

    <select name="productId" id="productSelect" required disabled>
      <option value="">请先选择供应商</option>
      <c:forEach var="product" items="${productList}">
        <option value="${product.productId}"
                data-supplier-id="${product.supplierId}"
                data-price="${product.purchasePrice}">
            ${product.productId} - ${product.productName}
        </option>
      </c:forEach>
    </select>

    <input type="number" name="quantity" placeholder="采购数量" min="1" required>
    <input type="text" name="unitPrice" placeholder="采购单价，例如 30.00" required>
    <input type="text" name="remark" placeholder="备注">
    <button type="submit" class="btn-success">提交采购订单</button>
  </form>

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
    const productSelect = document.getElementById("productSelect");
    const unitPriceInput = document.querySelector("input[name='unitPrice']");

    const allProductOptions = Array.from(productSelect.querySelectorAll("option"))
            .filter(option => option.value !== "");

    function refreshProductOptions() {
      const supplierId = supplierSelect.value;

      productSelect.innerHTML = "";

      const defaultOption = document.createElement("option");
      defaultOption.value = "";
      defaultOption.textContent = supplierId ? "请选择商品" : "请先选择供应商";
      productSelect.appendChild(defaultOption);

      if (!supplierId) {
        productSelect.disabled = true;
        if (unitPriceInput) {
          unitPriceInput.value = "";
        }
        return;
      }

      let matchedCount = 0;

      allProductOptions.forEach(option => {
        if (option.dataset.supplierId === supplierId) {
          productSelect.appendChild(option.cloneNode(true));
          matchedCount++;
        }
      });

      productSelect.disabled = matchedCount === 0;

      if (matchedCount === 0) {
        defaultOption.textContent = "该供应商暂无可采购商品";
      }

      if (unitPriceInput) {
        unitPriceInput.value = "";
      }
    }

    productSelect.addEventListener("change", function () {
      const selectedOption = productSelect.options[productSelect.selectedIndex];
      if (unitPriceInput && selectedOption && selectedOption.dataset.price) {
        unitPriceInput.value = selectedOption.dataset.price;
      }
    });

    supplierSelect.addEventListener("change", refreshProductOptions);

    refreshProductOptions();
  });
</script>
</body>
</html>