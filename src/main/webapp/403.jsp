<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>权限不足</title>
  <style>
    body {
      font-family: "Microsoft YaHei", Arial, sans-serif;
      background: #f5f7fa;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
    }
    .box {
      text-align: center;
      background: white;
      padding: 50px 60px;
      border-radius: 10px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.08);
    }
    .box h1 { font-size: 72px; color: #e74c3c; margin: 0; }
    .box h2 { color: #333; margin: 10px 0; }
    .box p { color: #888; margin: 15px 0 25px; }
    .btn { padding: 10px 30px; background: #2c3e50; color: white; border: none; border-radius: 5px; cursor: pointer; text-decoration: none; display: inline-block; }
    .btn:hover { background: #1a2a3a; }
  </style>
</head>
<body>
<div class="box">
  <h1>403</h1>
  <h2>权限不足</h2>
  <p>您没有权限访问此页面或执行此操作。</p>
  <a href="${pageContext.request.contextPath}/inventory" class="btn">返回首页</a>
</div>
</body>
</html>