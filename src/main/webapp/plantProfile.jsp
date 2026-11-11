
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%!
    // Escape database text before displaying it in HTML
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
    // Retrieve the plant provided by PlantProfileServlet
    sproutbuddy.model.UserPlant plant =
        (sproutbuddy.model.UserPlant)
            request.getAttribute("plant");

    if (plant == null || plant.getSpecies() == null) {
        response.sendError(
            javax.servlet.http.HttpServletResponse.SC_NOT_FOUND,
            "Plant profile not found."
        );
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1, viewport-fit=cover">

    <meta name="theme-color" content="#FFFFFF">

    <title>
        <%= escapeHtml(plant.getNickname()) %> | SproutBuddy
    </title>

    <link rel="preconnect"
          href="https://fonts.googleapis.com">

    <link rel="preconnect"
          href="https://fonts.gstatic.com"
          crossorigin>

    <link
        href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap"
        rel="stylesheet">

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/auth.css">

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/app.css">

    <style>
        /* Hide editing forms by default */
        #rename-form[hidden],
        #details-form[hidden],
        #details-display[hidden] {
            display: none !important;
        }

        /* Form styling */
        #rename-form,
        #details-form {
            margin: 16px auto 24px;
            max-width: 320px;
        }

        #rename-form .form-actions,
        #details-form .form-actions {
            display: flex;
            gap: 10px;
        }

        #rename-form .form-actions button,
        #details-form .form-actions button {
            flex: 1;
        }

        #edit-name,
        #edit-details {
            cursor: pointer;
            vertical-align: middle;
        }

        #plant-name {
            vertical-align: middle;
        }

        /* Preserve line breaks in personal notes */
        .plant-notes {
            white-space: pre-wrap;
            overflow-wrap: anywhere;
        }
    </style>
</head>

<body>

<main class="app-screen white">

    <!-- =====================================
         TOP NAVIGATION
         ===================================== -->

    <header class="topbar between">

        <a class="icon-btn"
           id="btn-back"
           href="<%= request.getContextPath() %>/garden.html"
           aria-label="Back to your garden">
            &#8592;
        </a>

        <div class="topbar-right">

            <button class="icon-btn"
                    id="btn-settings"
                    type="button"
                    aria-label="Settings"
                    disabled>
            </button>

            <button class="avatar-btn"
                    id="btn-account"
                    type="button"
                    aria-label="Account"
                    disabled>
            </button>

        </div>

    </header>

    <div class="page-scroll">

        <!-- =====================================
             PLANT NAME AND PHOTO
             ===================================== -->

        <div class="plant-head">

            <!-- Plant name with edit pencil -->
            <h1 class="plant-title">

                <span id="plant-name">
                    <%= escapeHtml(plant.getNickname()) %>
                </span>

                <button
                    type="button"
                    class="edit-btn"
                    id="edit-name"
                    aria-label="Rename plant"
                    aria-controls="rename-form"
                    aria-expanded="false"
                    title="Edit plant name">
                    &#9998;
                </button>

            </h1>

            <!-- RENAME FORM -->
            <form
                id="rename-form"
                action="<%= request.getContextPath() %>/plant-profile"
                method="post"
                class="sheet-form"
                hidden>

                <input type="hidden"
                       name="id"
                       value="<%= plant.getPlantId() %>">

                <input type="hidden"
                       name="action"
                       value="rename">

                <div class="field">

                    <label for="nickname">
                        New Plant Name
                    </label>

                    <input
                        type="text"
                        id="nickname"
                        name="nickname"
                        maxlength="100"
                        required
                        value="<%= escapeHtml(plant.getNickname()) %>">

                </div>

                <div class="form-actions">

                    <button type="button"
                            class="btn"
                            id="cancel-rename">
                        Cancel
                    </button>

                    <button type="submit"
                            class="btn">
                        Save Name
                    </button>

                </div>

            </form>

            <!-- Scientific species name -->
            <p class="plant-scientific-name">
                <%= escapeHtml(
                    plant.getSpecies().getScientificName()
                ) %>
            </p>

            <!-- Plant photo placeholder -->
            <div class="photo-wrap">

                <div id="photo-slot">
                    <span aria-label="Plant photo placeholder">
                        &#127793;
                    </span>
                </div>

                <label class="photo-edit"
                       title="Change photo">

                    <span id="camera-icon"></span>

                    <input type="file"
                           id="photo-input"
                           accept="image/*"
                           aria-label="Change plant photo"
                           disabled>

                </label>

            </div>

        </div>

        <!-- =====================================
             STREAK CALENDAR
             ===================================== -->

        <section class="section">

            <div class="section-head">

                <span class="pill">Streak</span>

                <span class="streak-count"
                      id="streak-count">
                    Coming soon
                </span>

            </div>

            <div class="cal-nav">

                <button type="button"
                        id="cal-prev"
                        aria-label="Previous month"
                        disabled>
                    &#8592;
                </button>

                <strong id="cal-title">
                    Care Calendar
                </strong>

                <button type="button"
                        id="cal-next"
                        aria-label="Next month"
                        disabled>
                    &#8594;
                </button>

            </div>

            <div class="calendar" id="calendar">
                <!-- Will use care_tasks later -->
            </div>

            <div class="legend" id="legend"></div>

        </section>

        <!-- =====================================
             PLANT DETAILS
             ===================================== -->

        <section class="section">

            <span class="pill">Plant Details</span>

            <!-- Sunshine -->
            <div class="detail">

                <p class="detail-label">Sunshine</p>

                <span class="detail-pill sun">
                    <%= escapeHtml(
                        plant.getSpecies().getLightRequirement()
                    ) %>
                </span>

            </div>

            <!-- Watering -->
            <div class="detail">

                <p class="detail-label">Water</p>

                <span class="detail-pill water">

                    <%
                        Integer interval =
                            plant.getWateringIntervalDays();

                        if (interval != null && interval > 0) {
                    %>

                        Every <%= interval %> days

                    <%
                        } else {
                    %>

                        <%= escapeHtml(
                            plant.getSpecies().getWaterRequirement()
                        ) %>

                    <%
                        }
                    %>

                </span>

            </div>

        </section>

        <!-- =====================================
             CARE INSTRUCTIONS
             ===================================== -->

        <section class="section">

            <span class="pill">Care Instructions</span>

            <div class="detail">

                <p>
                    <%= escapeHtml(
                        plant.getSpecies().getCareInstructions()
                    ) %>
                </p>

            </div>

        </section>

        <!-- =====================================
             PERSONAL PLANT INFORMATION
             ===================================== -->

        <section class="section">

            <div class="section-head">

                <span class="pill">My Plant</span>

                <button
                    type="button"
                    class="edit-btn"
                    id="edit-details"
                    aria-label="Edit plant details"
                    aria-controls="details-form"
                    aria-expanded="false"
                    title="Edit location and notes">
                    &#9998;
                </button>

            </div>

            <!-- Saved location and notes -->
            <div id="details-display">

                <div class="detail">

                    <p class="detail-label">
                        Location
                    </p>

                    <p>
                        <%= escapeHtml(plant.getLocation()) %>
                    </p>

                </div>

                <div class="detail">

                    <p class="detail-label">
                        Personal Notes
                    </p>

                    <p class="plant-notes"><%= escapeHtml(
                        plant.getNotes()
                    ) %></p>

                </div>

            </div>

            <!-- EDIT LOCATION AND NOTES FORM -->
            <form
                id="details-form"
                action="<%= request.getContextPath() %>/plant-profile"
                method="post"
                class="sheet-form"
                hidden>

                <input type="hidden"
                       name="id"
                       value="<%= plant.getPlantId() %>">

                <input type="hidden"
                       name="action"
                       value="updateDetails">

                <div class="field">

                    <label for="plant-location">
                        Location
                    </label>

                    <input
                        type="text"
                        id="plant-location"
                        name="location"
                        maxlength="100"
                        value="<%= escapeHtml(plant.getLocation()) %>">

                </div>

                <div class="field">

                    <label for="plant-notes">
                        Personal Notes
                    </label>

                    <textarea
                        id="plant-notes"
                        name="notes"
                        maxlength="5000"
                        rows="4"><%= escapeHtml(
                            plant.getNotes()
                        ) %></textarea>

                </div>

                <div class="form-actions">

                    <button
                        type="button"
                        class="btn"
                        id="cancel-details">
                        Cancel
                    </button>

                    <button
                        type="submit"
                        class="btn">
                        Save Changes
                    </button>

                </div>

            </form>

        </section>

    </div>

</main>

<!-- =====================================
     JAVASCRIPT: EDITING INTERACTIONS
     ===================================== -->

<script>
document.addEventListener("DOMContentLoaded", function () {

    // =====================================
    // Rename plant
    // =====================================

    const editButton =
        document.getElementById("edit-name");

    const renameForm =
        document.getElementById("rename-form");

    const cancelButton =
        document.getElementById("cancel-rename");

    const nicknameInput =
        document.getElementById("nickname");

    const plantName =
        document.getElementById("plant-name");

    editButton.addEventListener("click", function () {

        renameForm.hidden = false;

        editButton.setAttribute(
            "aria-expanded", "true");

        nicknameInput.focus();
        nicknameInput.select();
    });

    cancelButton.addEventListener("click", function () {

        renameForm.reset();
        renameForm.hidden = true;

        editButton.setAttribute(
            "aria-expanded", "false");

        editButton.focus();
    });

    // =====================================
    // Edit location and personal notes
    // =====================================

    const detailsButton =
        document.getElementById("edit-details");

    const detailsForm =
        document.getElementById("details-form");

    const detailsDisplay =
        document.getElementById("details-display");

    const cancelDetails =
        document.getElementById("cancel-details");

    detailsButton.addEventListener("click", function () {

        detailsForm.hidden = false;
        detailsDisplay.hidden = true;

        detailsButton.setAttribute(
            "aria-expanded", "true");

        document.getElementById(
            "plant-location").focus();
    });

    cancelDetails.addEventListener("click", function () {

        detailsForm.reset();

        detailsForm.hidden = true;
        detailsDisplay.hidden = false;

        detailsButton.setAttribute(
            "aria-expanded", "false");

        detailsButton.focus();
    });

    // Forms submit normally to PlantProfileServlet.
    // The servlet updates MySQL and redirects
    // back to the refreshed profile.

});
</script>

</body>
</html>
