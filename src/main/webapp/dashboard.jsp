<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDateTime,java.time.format.DateTimeFormatter"%>
<%
LocalDateTime now = LocalDateTime.now();
// TODO i want to maybe sort the dashboard by order of importance
// maybe calculate some order up here and then load the sections in that order?
String user = "TEMP_USERNAME"; // need to fetch actual user name from login details
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>SproutBuddy Dashboard</title>
</head>

<body>

    <h1>Dashboard</h1>
    <p>Welcome <%= user %></p>
    <a href="logout.jsp"> <!-- placeholder! idk what the logout screen will look like yet or where this should link to -->
    	<button id="logout-button">Logout</button>
    </a>
    
    <!-- Display a grid segment for each important thing -->
    <div id="db-plants" class="DB-GRID">
    	<h2>Plant Status Overview</h2>
    	<!-- Get information about plants -->
    	<a href="plants.jsp">
    		<button class="GRID-BUTTON">View all plants</button>
    	</a>
    </div>
    
    <div id="db-schedule" class="DB-GRID">
    	<h2>Schedule</h2>
    	<!-- Display snippet of calendar with current date -->
    	<p id="date"><%=now.format(DateTimeFormatter.ofPattern("EEEE MMMM d, uuuu"))%></p>
    	<% // TODO need to get from plant schedule and display upcoming events %>
    	<a href="calendar.jsp"> <!-- TODO replace with proper link -->
    		<button class="GRID-BUTTON">View calendar</button>
    	</a>
    </div>
    
    <div id="db-garden" class="DB-GRID">
    	<h2>Virtual Garden</h2>
    	<!-- Get plant info and display some of them -->
    	<a href="garden.jsp"> <!-- TODO replace with proper link -->
    		<button class="GRID-BUTTON">Visit garden</button>
    	</a>
    </div>
    
    <div id="db-streak" class="DB-GRID">
    	<h2>Streaks</h2>
    	<!-- Get streak data and display it-->
    	<% 
    	int streak = 0; // TODO replace with actual call to get streak info
    	if (streak == 0) { 
    	%>
    	<h3>Start a new streak today!</h3>
    	<% } else { %>
    	<h3>Daily streak: <b><%= streak %></b>! Keep it up!</h3>
    	<% } %>
    	<a href="social.jsp">
    		<button class="GRID-BUTTON">See friends' streaks</button>
    	</a>
    </div>
	
	<div id="db-tips" class="DB-GRID">
    	<h2>Tips</h2>
    	<% // fetch a random tip to display %>
    	<a href="tips.jsp">
    		<button class="GRID-BUTTON">View more plant education tips</button>
    	</a>
    </div>
    
    <div id="db-end" class="DB-GRID">
    	<h2>End of dashboard</h2>
    </div>
	
</body>

</html>