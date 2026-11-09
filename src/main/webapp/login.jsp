<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<%@ page import="sproutbuddy.model.User" %>


<!DOCTYPE html>
<html lang="en">

<body>
    <div class="card">
        <h1>Login</h1>

        <% if (request.getAttribute("error") != null) { %>
            <p class="error">${error}</p>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required autofocus>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>

            <button type="submit">Log In</button>
        </form>

        <div class="links">
            <a href="index.jsp">Home</a>
        </div>
    </div>
</body>

</html>