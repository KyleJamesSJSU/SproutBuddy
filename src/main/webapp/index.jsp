<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    // The app starts at the login page. New users reach sign-up from the
    // "Create an account" button on login.html.
    response.sendRedirect(request.getContextPath() + "/login.html");
%>
