
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="sproutbuddy.model.PlantSpecies" %>
<%@ page import="sproutbuddy.model.User" %>

<%!
    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>

<%
    @SuppressWarnings("unchecked")
    List<PlantSpecies> speciesList =
        (List<PlantSpecies>) request.getAttribute("speciesList");

    User user = (User) session.getAttribute("user");

    if (speciesList == null || user == null) {
        response.sendError(404);
        return;
    }

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">
    <title>Add Plant | SproutBuddy</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/auth.css">
    <link rel="stylesheet"
          href="<%= contextPath %>/css/app.css">
    <link rel="stylesheet"
          href="<%= contextPath %>/css/seasons.css">

    <!-- Seasonal theme: loads in <head> so the colours are right before anything draws -->
    <script src="<%= contextPath %>/js/theme.js"></script>

    <style>
        .add-plant-content {
            padding: 24px;
        }

        .add-plant-content h1 {
            margin-bottom: 24px;
        }

        .add-plant-content .field {
            margin-bottom: 20px;
        }

        .add-plant-content input,
        .add-plant-content select {
            width: 100%;
            box-sizing: border-box;
            padding: 12px;
            border-radius: 12px;
        }

        .add-plant-actions {
            display: flex;
            gap: 12px;
            margin-top: 24px;
        }

        .add-plant-actions > * {
            flex: 1;
            text-align: center;
        }
    </style>
</head>

<body>
<main class="app-screen white">

    <header class="topbar between">
        <a class="icon-btn"
           href="<%= contextPath %>/garden#garden"
           aria-label="Back to garden">
            &#8592;
        </a>
    </header>

    <div class="page-scroll add-plant-content">

        <h1>Add a Plant</h1>

        <form action="<%= contextPath %>/add-plant"
              method="post">

            <div class="field">
                <label for="speciesId">Plant Species</label>

                <select id="speciesId"
                        name="speciesId"
                        required>

                    <option value="">
                        Select a plant species
                    </option>

                    <% for (PlantSpecies species : speciesList) {
                        if (species.isCustom() &&
                            !java.util.Objects.equals(
                                species.getCreatedByUserId(),
                                user.getId())) {
                            continue;
                        }
                    %>
                        <option value="<%= species.getSpeciesId() %>">
                            <%= escapeHtml(species.getCommonName()) %>
                            (<%= escapeHtml(species.getScientificName()) %>)
                        </option>
                    <% } %>

                </select>
            </div>

            <div class="field">
                <label for="nickname">Plant Nickname</label>

                <input type="text"
                       id="nickname"
                       name="nickname"
                       maxlength="100"
                       placeholder="e.g. Monty"
                       required>
            </div>

            <div class="field">
                <label for="location">Location</label>

                <input type="text"
                       id="location"
                       name="location"
                       maxlength="100"
                       placeholder="e.g. Living Room">
            </div>

            <div class="add-plant-actions">
                <a class="btn"
                   href="<%= contextPath %>/garden#garden">
                    Cancel
                </a>

                <button type="submit" class="btn">
                    Add Plant
                </button>
            </div>

        </form>

    </div>
</main>
</body>
</html>
