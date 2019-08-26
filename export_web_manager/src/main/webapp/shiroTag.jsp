<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <shiro:hasPermission name="购销合同">
        <a href="#">购销合同</a>
    </shiro:hasPermission>

    <shiro:hasPermission name="出货表">
        <a href="#">出货表</a>
    </shiro:hasPermission>

    <shiro:hasPermission name="系统管理">
        <a href="#">系统管理</a>
    </shiro:hasPermission>

    <shiro:hasPermission name="日志管理">
        <a href="#">日志管理</a>
    </shiro:hasPermission>
</body>
</html>
