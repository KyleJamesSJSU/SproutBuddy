<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate,java.util.List,sproutbuddy.model.CareTask"%>
<%
    List<CareTask> careTasks = (List<CareTask>) request.getAttribute("careTasks");
    LocalDate today = LocalDate.now();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Plant Schedule | SproutBuddy</title>
    <link rel="stylesheet" href="css/auth.css">
</head>
<body>
<main class="screen">
    <h1>Plant schedule</h1>
    <% if (careTasks == null || careTasks.isEmpty()) { %>
        <p>You are all caught up. Your plants have no open reminders.</p>
    <% } else { %>
        <% for (CareTask task : careTasks) {
            boolean overdue = task.getDueDate().toLocalDate().isBefore(today);
        %>
        <article class="task-card <%= overdue ? "overdue" : "" %>">
            <h2><%= task.getNickname() %></h2>
            <p><%= task.getTaskType() %> &middot; <%= task.getCommonName() %></p>
            <p>Every <%= task.getIntervalDays() %> days &middot; Due <%= task.getDueDate() %></p>
            <form method="post" action="reminders">
                <input type="hidden" name="taskId" value="<%= task.getTaskId() %>">
                <button class="btn" type="submit">Mark done</button>
            </form>
        </article>
        <% } %>
    <% } %>
    <a href="index.jsp">Back to home</a>
</main>
</body>
</html>
