<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>仓储管理系统 - 登录</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: "Microsoft YaHei", Arial, sans-serif;
      background: #f0f2f5;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
    }
    .login-box {
      background: white;
      padding: 40px 35px;
      border-radius: 10px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.1);
      width: 380px;
    }
    .login-box h1 {
      text-align: center;
      font-size: 26px;
      color: #2c3e50;
      margin-bottom: 8px;
    }
    .login-box .sub {
      text-align: center;
      color: #888;
      font-size: 14px;
      margin-bottom: 25px;
    }
    .form-group {
      margin-bottom: 18px;
    }
    .form-group label {
      display: block;
      font-weight: bold;
      color: #333;
      margin-bottom: 5px;
      font-size: 14px;
    }
    .form-group input {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 14px;
      transition: border 0.3s;
    }
    .form-group input:focus {
      outline: none;
      border-color: #3498db;
    }
    .btn-login {
      width: 100%;
      padding: 11px;
      background: #2c3e50;
      color: white;
      border: none;
      border-radius: 5px;
      font-size: 16px;
      cursor: pointer;
      transition: background 0.3s;
    }
    .btn-login:hover { background: #1a2a3a; }
    .error-msg {
      color: #e74c3c;
      font-size: 13px;
      background: #fdecea;
      padding: 8px 12px;
      border-radius: 4px;
      margin-bottom: 15px;
      text-align: center;
    }
    .test-hint {
      margin-top: 20px;
      padding-top: 15px;
      border-top: 1px solid #eee;
      font-size: 13px;
      color: #888;
      line-height: 1.8;
    }
    .test-hint strong { color: #2c3e50; }
  </style>
</head>
<body>
<div class="login-box">
  <h1>仓储管理系统</h1>
  <div class="sub">登录后进入系统</div>

  <%
    String error = (String) request.getAttribute("error");
    if (error != null && !error.isEmpty()) {
  %>
  <div class="error-msg"><%= error %></div>
  <%
    }
  %>

  <form action="${pageContext.request.contextPath}/login" method="post">
    <div class="form-group">
      <label>用户名</label>
      <input type="text" name="username" placeholder="请输入用户名" required>
    </div>
    <div class="form-group">
      <label>密码</label>
      <input type="password" name="password" placeholder="请输入密码" required>
    </div>
    <button type="submit" class="btn-login">登 录</button>
  </form>

  <div class="test-hint">
    <strong>测试账号：</strong><br>
    管理员：admin / 123456 &nbsp;|&nbsp; 业务员：purchase_user / 123456<br>
    只读用户：sales_user / 123456
  </div>
</div>
</body>
</html>