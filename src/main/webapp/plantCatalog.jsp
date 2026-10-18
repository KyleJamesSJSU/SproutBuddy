<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List" %>
<%@ page import="sproutbuddy.model.PlantSpecies" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Plant Catalog</title>
</head>

<body>

<h1>Plant Catalog</h1>

<a href="index.jsp">Back to Home</a>

<hr>

<%
    List<PlantSpecies> speciesList =
        (List<PlantSpecies>) request.getAttribute("speciesList");

    if (speciesList != null && !speciesList.isEmpty()) {

        for (PlantSpecies species : speciesList) {
%>

            <h2><%= species.getCommonName() %></h2>

            <p>
                Scientific Name:
                <%= species.getScientificName() %>
            </p>

            <p>
                Light:
                <%= species.getLightRequirement() %>
            </p>

            <p>
                Water:
                <%= species.getWaterRequirement() %>
            </p>

            <p>
                Care:
                <%= species.getCareInstructions() %>
            </p>

            <hr>

<%
        }

    } else {
%>

        <p>No plant species found.</p>

<%
    }
%>

</body>
</html>