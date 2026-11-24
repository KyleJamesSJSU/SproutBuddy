
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="sproutbuddy.model.User" %>
<%@ page import="sproutbuddy.model.UserPlant" %>

<%!
    // Escape database text before displaying it in HTML.
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
    // Retrieve the plants provided by GardenServlet.
    @SuppressWarnings("unchecked")
    List<UserPlant> plants =
        (List<UserPlant>) request.getAttribute("plants");
    @SuppressWarnings("unchecked")
    List<User> friends =
        (List<User>) request.getAttribute("friends");
    if (friends == null) {
        friends = Collections.emptyList();
    }

    // The page should only be accessed through GardenServlet.
    if (plants == null) {
        response.sendError(
            javax.servlet.http.HttpServletResponse.SC_NOT_FOUND,
            "Garden not found."
        );
        return;
    }

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1, viewport-fit=cover">

    <meta name="theme-color" content="#DDF7F0" data-seasonal>

    <title>Your Garden | SproutBuddy</title>

    <link rel="preconnect"
          href="https://fonts.googleapis.com">

    <link rel="preconnect"
          href="https://fonts.gstatic.com"
          crossorigin>

    <link
        href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap"
        rel="stylesheet">

    <link rel="stylesheet"
          href="<%= contextPath %>/css/auth.css">

    <link rel="stylesheet"
          href="<%= contextPath %>/css/app.css?v=friends-button-20261124">
    <link rel="stylesheet"
          href="<%= contextPath %>/css/seasons.css">

    <!-- Seasonal theme: loads in <head> so the colours are right before anything draws -->
    <script src="<%= contextPath %>/js/theme.js"></script>

    <style>
        /* Garden-specific additions */

        .garden-empty {
            text-align: center;
            padding: 32px 16px;
        }

        .garden-empty p {
            margin-bottom: 16px;
        }

        .garden-actions {
            display: flex;
            justify-content: center;
            margin-top: 18px;
        }

        .garden-plant-photo {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100%;
            min-height: 160px;
            font-size: 64px;
        }

        .garden-plant-photo img {
            max-width: 100%;
            max-height: 220px;
            object-fit: contain;
        }

        .garden-plant-species {
            font-size: 13px;
            opacity: 0.75;
        }

        .garden-plant-status {
            font-size: 13px;
        }

        .garden-tab-message {
            text-align: center;
            padding: 24px 16px;
        }

        [hidden] {
            display: none !important;
        }
    </style>
</head>

<body>

<main class="app-screen">

    <!-- =====================================
         TOP NAVIGATION
         ===================================== -->

    <header class="topbar">

        <button class="icon-btn"
                id="btn-settings"
                type="button"
                aria-label="Settings">
        </button>

        <button class="avatar-btn"
		        id="btn-account"
		        type="button"
		        aria-label="Account"
		        aria-controls="account-dialog"
		        aria-expanded="false">
		    <%= escapeHtml(
		        ((sproutbuddy.model.User)
		            request.getAttribute("currentUser"))
		            .getUsername().substring(0, 1).toUpperCase()
		    ) %>
		</button>

    </header>

    <!-- Default tab is Reminders -->
    <h1 class="page-title" id="page-title">
        Reminders
    </h1>

    <section class="card">

        <!-- =====================================
             GARDEN TAB
             ===================================== -->

        <div class="panel"
             id="panel-garden"
             role="tabpanel"
             aria-labelledby="tab-garden"
             hidden>

            <% if (plants.isEmpty()) { %>

                <!-- Empty garden -->
                <div class="garden-empty">

                    <p>
                        You don't have any plants yet!
                    </p>

                    <p>
                        Start growing your garden by
                        adding your first plant.
                    </p>

                    <div class="garden-actions">

                        <a class="btn"
                           href="<%= contextPath %>/add-plant">
                            Browse Plant Catalog
                        </a>

                    </div>

                </div>

            <% } else { %>

                <!-- Plant carousel -->
                <div class="carousel-wrap">

                    <button class="arrow"
                            id="prev"
                            type="button"
                            aria-label="Previous plant">
                        &#8592;
                    </button>

                    <div class="carousel" id="carousel">

                        <% for (UserPlant plant : plants) { %>

                            <a class="slide"
                               href="<%= contextPath %>/plant-profile?id=<%= plant.getPlantId() %>">

                                <!-- Plant photo placeholder -->
                                <div class="garden-plant-photo">
                                    &#127793;
                                </div>

                                <!-- Plant nickname -->
                                <span class="plant-name">
                                    <%= escapeHtml(
                                        plant.getNickname()
                                    ) %>
                                </span>

                                <!-- Species -->
                                <span class="garden-plant-species">
                                    <%= escapeHtml(
                                        plant.getSpecies() == null
                                            ? ""
                                            : plant.getSpecies()
                                                   .getScientificName()
                                    ) %>
                                </span>

                                <!-- Care status placeholder -->
                                <span class="plant-status ok">
                                    View plant details
                                </span>

                            </a>

                        <% } %>

                    </div>

                    <button class="arrow"
                            id="next"
                            type="button"
                            aria-label="Next plant">
                        &#8594;
                    </button>

                </div>

                <!-- Carousel indicators -->
                <div class="dots" id="dots">

                    <% for (int i = 0; i < plants.size(); i++) { %>
                        <span class="dot"></span>
                    <% } %>

                </div>

                <div class="garden-actions">

                    <a class="btn"
                       href="<%= contextPath %>/add-plant">
                        Add Another Plant
                    </a>

                </div>

            <% } %>

        </div>

        <!-- =====================================
             REMINDERS TAB
             ===================================== -->

        <div class="panel"
             id="panel-reminders"
             role="tabpanel"
             aria-labelledby="tab-reminders">

            <div class="garden-tab-message">

                <h2>Care Reminders</h2>

                <p>
                    View your upcoming plant care tasks.
                </p>

                <a class="btn"
                   href="<%= contextPath %>/reminders">
                    View Reminders
                </a>

            </div>

        </div>

        <!-- =====================================
             FRIENDS TAB
             ===================================== -->

        <div class="panel"
             id="panel-friends"
             role="tabpanel"
             aria-labelledby="tab-friends"
             hidden>

            <div class="garden-tab-message">

                <div class="friends-heading">
                    <h2>Friends’ streaks</h2>
                </div>

                <p>Plant care over the last 7 days</p>
                <div class="friend-action-row">
                    <button class="friend-add-main"
                            id="friend-add-button"
                            type="button">
                        + Add friends
                    </button>
                </div>
                <div class="friend-search-panel"
                     id="friend-search-panel"
                     data-friends-url="<%= escapeHtml(contextPath + "/friends") %>"
                     data-csrf-token="<%= escapeHtml(
                         (String) request.getAttribute("csrfToken")
                     ) %>"
                     hidden="hidden">
                    <input class="friend-search"
                           id="friend-search"
                           type="search"
                           placeholder="Search people"
                           aria-label="Search people">
                    <div class="friend-matches" id="friend-matches"></div>
                    <p class="friend-search-status" id="friend-search-status"
                       role="status" aria-live="polite"></p>
                </div>
                <ul class="friend-list" id="friend-list">
                    <% for (User friend : friends) { %>
                        <li class="friend" data-user-id="<%= friend.getId() %>">
                            <div class="friend-avatar">
                                <%= escapeHtml(friend.getUsername().substring(0, 1).toUpperCase()) %>
                            </div>
                            <div class="friend-main">
                                <div class="friend-name"><%= escapeHtml(friend.getUsername()) %></div>
                                <div class="friend-last">Connected SproutBuddy user</div>
                            </div>
                            <span class="streak-badge">Connected</span>
                        </li>
                    <% } %>
                </ul>

            </div>

        </div>

        <!-- =====================================
             BOTTOM NAVIGATION TABS
             ===================================== -->

        <nav class="tabs"
             role="tablist"
             aria-label="Sections">

            <button type="button"
                    role="tab"
                    id="tab-garden"
                    data-tab="garden"
                    aria-selected="false">
                garden
            </button>

            <button type="button"
                    role="tab"
                    id="tab-reminders"
                    data-tab="reminders"
                    aria-selected="true">
                reminders
            </button>

            <button type="button"
                    role="tab"
                    id="tab-friends"
                    data-tab="friends"
                    aria-selected="false">
                friends
            </button>

        </nav>

    </section>


	<!-- =====================================
	     ACCOUNT POPUP
	     ===================================== -->
	
	<div id="account-dialog"
	     class="sheet-backdrop"
	     hidden>
	
	    <div class="sheet"
	         role="dialog"
	         aria-modal="true"
	         aria-labelledby="account-title">
	
	        <div class="sheet-handle"></div>
	
	        <h2 class="sheet-title" id="account-title">
	            Account
	        </h2>
	
	        <div class="sheet-form">
	
	            
				<!-- Edit Account Form -->
				<form action="<%= contextPath %>/account"
				      method="post"
				      class="sheet-form">
				
				    <input type="hidden"
				           name="csrfToken"
				           value="<%= escapeHtml(
				               (String) request.getAttribute("csrfToken")
				           ) %>">
				
				    <div class="field">
				
				        <label for="account-username">
				            Username
				        </label>
				
				        <input type="text"
				               id="account-username"
				               name="username"
				               maxlength="50"
				               required
				               value="<%= escapeHtml(
				                   (String) request.getAttribute("accountUsername")
				               ) %>">
				
				    </div>
				
				    <div class="field">
				
				        <label for="account-email">
				            Email
				        </label>
				
				        <input type="email"
				               id="account-email"
				               name="email"
				               maxlength="100"
				               required
				               value="<%= escapeHtml(
				                   (String) request.getAttribute("accountEmail")
				               ) %>">
				
				    </div>
				
				    <button type="submit"
				            class="btn btn-block">
				        Save Changes
				    </button>
				
				</form>

	
	            <form action="<%= contextPath %>/logout"
	                  method="post">
	
	                <input type="hidden"
	                       name="csrfToken"
	                       value="<%= escapeHtml(
	                           (String) request.getAttribute("csrfToken")
	                       ) %>">
	
	                <button type="submit"
	                        class="btn btn-block btn-outline">
	                    Log Out
	                </button>
	
	            </form>
	
	            <button type="button"
	                    class="btn btn-block"
	                    id="close-account">
	                Close
	            </button>
	
	        </div>
	    </div>
	</div>

<%@ include file="/WEB-INF/jspf/settings-dialog.jspf" %>

</main>

<!-- =====================================
     JAVASCRIPT: TABS AND CAROUSEL
     ===================================== -->

<script>
document.addEventListener("DOMContentLoaded", function () {

    // =====================================
    // Tab navigation
    // =====================================

    const titles = {
        garden: "Your Garden",
        reminders: "Reminders",
        friends: "Friends"
    };

    const tabs = document.querySelectorAll(".tabs button");
    const addFriendButton = document.getElementById("friend-add-button");
    const friendList = document.getElementById("friend-list");
    const friendSearchPanel = document.getElementById("friend-search-panel");
    const friendSearch = document.getElementById("friend-search");
    const friendMatches = document.getElementById("friend-matches");
    const friendSearchStatus = document.getElementById("friend-search-status");
    const friendsUrl = friendSearchPanel.dataset.friendsUrl;
    const csrfToken = friendSearchPanel.dataset.csrfToken;
    let searchRequest = 0;

    const panels = {
        garden: document.getElementById("panel-garden"),
        reminders: document.getElementById("panel-reminders"),
        friends: document.getElementById("panel-friends")
    };

    function showTab(name) {

        if (!panels[name]) {
            name = "reminders";
        }

        tabs.forEach(function (tab) {
            const selected = tab.dataset.tab === name;
            tab.setAttribute("aria-selected", String(selected));
        });

        Object.keys(panels).forEach(function (key) {
            panels[key].hidden = key !== name;
        });

        document.getElementById("page-title").textContent =
            titles[name];

        document.title = titles[name] + " | SproutBuddy";

        history.replaceState(null, "", "#" + name);

        if (name === "garden") {
            updateCarousel();
        }
    }

    tabs.forEach(function (tab) {
        tab.addEventListener("click", function () {
            showTab(tab.dataset.tab);
        });
    });

    if (addFriendButton) {
        addFriendButton.addEventListener("click", function () {
            friendSearchPanel.hidden = !friendSearchPanel.hidden;
            if (!friendSearchPanel.hidden) {
                friendSearch.value = "";
                renderFriendMatches();
                friendSearch.focus();
            }
        });
    }

    function showFriendError(message) {
        friendSearchStatus.textContent = message;
    }

    function addFriendToList(user) {
        if (friendList.querySelector('[data-user-id="' + user.id + '"]')) return;

        const item = document.createElement("li");
        item.className = "friend";
        item.dataset.userId = user.id;

        const avatar = document.createElement("div");
        avatar.className = "friend-avatar";
        avatar.textContent = user.username.charAt(0).toUpperCase();

        const main = document.createElement("div");
        main.className = "friend-main";
        const name = document.createElement("div");
        name.className = "friend-name";
        name.textContent = user.username;
        const description = document.createElement("div");
        description.className = "friend-last";
        description.textContent = "Connected SproutBuddy user";
        main.append(name, description);

        const badge = document.createElement("span");
        badge.className = "streak-badge";
        badge.textContent = "Connected";

        item.append(avatar, main, badge);
        friendList.append(item);
    }

    async function renderFriendMatches() {
        if (!friendMatches || !friendSearch) return;

        const requestId = ++searchRequest;
        friendSearchStatus.textContent = "Searching users…";
        friendMatches.replaceChildren();
        try {
            const response = await fetch(friendsUrl + "?query="
                    + encodeURIComponent(friendSearch.value.trim()), {
                headers: { Accept: "application/json" }
            });
            const users = await response.json();
            if (requestId !== searchRequest) return;
            if (!response.ok) {
                throw new Error(users.error || "Unable to search users.");
            }

            friendSearchStatus.textContent = users.length
                ? users.length + " users found"
                : "No users found.";

            users.forEach(function (user) {
                const row = document.createElement("div");
                row.className = "friend-match";
                row.dataset.userId = user.id;

                const username = document.createElement("strong");
                username.textContent = user.username;

                const addButton = document.createElement("button");
                addButton.type = "button";
                addButton.className = "friend-add";
                addButton.textContent = "Add";
                addButton.addEventListener("click", async function () {
                    addButton.disabled = true;
                    try {
                        const addResponse = await fetch(friendsUrl, {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/x-www-form-urlencoded",
                                Accept: "application/json"
                            },
                            body: new URLSearchParams({
                                userId: String(user.id),
                                csrfToken: csrfToken
                            })
                        });
                        const added = await addResponse.json();
                        if (!addResponse.ok) {
                            throw new Error(added.error || "Unable to connect user.");
                        }

                        addFriendToList(added);
                        addButton.replaceWith(document.createTextNode("Added"));
                    } catch (error) {
                        showFriendError(error.message);
                        addButton.disabled = false;
                    }
                });

                row.append(username, addButton);
                friendMatches.append(row);
            });
        } catch (error) {
            if (requestId === searchRequest) {
                showFriendError(error.message);
            }
        }
    }

    if (friendSearch) {
        friendSearch.addEventListener("input", renderFriendMatches);
    }

    // =====================================
    // Garden carousel
    // =====================================

    const carousel = document.getElementById("carousel");
    const prev = document.getElementById("prev");
    const next = document.getElementById("next");
    const dots = document.querySelectorAll("#dots .dot");

    function updateCarousel() {

        if (!carousel) return;

        const width = carousel.clientWidth;

        if (width === 0) return;

        const index = Math.round(
            carousel.scrollLeft / width
        );

        if (prev) {
            prev.disabled = index <= 0;
        }

        if (next) {
            next.disabled = index >= dots.length - 1;
        }

        dots.forEach(function (dot, i) {
            dot.classList.toggle("on", i === index);
        });
    }

    if (carousel) {

        carousel.addEventListener(
            "scroll",
            updateCarousel,
            { passive: true }
        );

        if (prev) {
            prev.addEventListener("click", function () {
                carousel.scrollBy({
                    left: -carousel.clientWidth,
                    behavior: "smooth"
                });
            });
        }

        if (next) {
            next.addEventListener("click", function () {
                carousel.scrollBy({
                    left: carousel.clientWidth,
                    behavior: "smooth"
                });
            });
        }
    }

    // =====================================
    // Initialize dashboard
    // =====================================

    showTab(location.hash.slice(1));

    window.addEventListener("hashchange", function () {
        showTab(location.hash.slice(1));
    });
    
	
	 // =====================================
	 // Account popup
	 // =====================================
	
	 const accountButton =
	     document.getElementById("btn-account");
	
	 const accountDialog =
	     document.getElementById("account-dialog");
	
	 const closeAccount =
	     document.getElementById("close-account");
	
	 accountButton.addEventListener("click", function () {
	
	     accountDialog.hidden = false;
	     accountDialog.classList.add("open");
	
	     accountButton.setAttribute("aria-expanded", "true");
	 });
	
	 function hideAccount() {
	
	     accountDialog.classList.remove("open");
	     accountDialog.hidden = true;
	
	     accountButton.setAttribute("aria-expanded", "false");
	     accountButton.focus();
	 }
	
	 closeAccount.addEventListener("click", hideAccount);
	
	 accountDialog.addEventListener("click", function (event) {
	
	     if (event.target === accountDialog) {
	         hideAccount();
	     }
	 });
	
	 document.addEventListener("keydown", function (event) {
	
	     if (event.key === "Escape" && !accountDialog.hidden) {
	         hideAccount();
	     }
	 });

	// Show account update results.
	const params = new URLSearchParams(window.location.search);

	const accountStatus = params.get("account");
	const accountMessage = params.get("message");

	if (accountStatus && accountMessage) {

	    accountDialog.hidden = false;
	    accountDialog.classList.add("open");

	    accountButton.setAttribute("aria-expanded", "true");

	    const message = document.createElement("p");
	    message.setAttribute("role", "status");
	    message.textContent = accountMessage;

	    const form = accountDialog.querySelector("form");
	    form.parentNode.insertBefore(message, form);
	};


});
</script>

</body>
</html>
