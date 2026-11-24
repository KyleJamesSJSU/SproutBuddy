package sproutbuddy.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sproutbuddy.dao.FriendDAO;
import sproutbuddy.model.User;

public class FriendsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private FriendDAO friendDAO;

    @Override
    public void init() {
        friendDAO = new FriendDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String query = request.getParameter("query");
        query = query == null ? "" : query.trim();
        if (query.length() > 50) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Search text must be 50 characters or fewer.");
            return;
        }

        try {
            writeUsers(response, friendDAO.searchUsers(user.getId(), query));
        } catch (Exception e) {
            log("Unable to search for friends", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to search for users.");
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        HttpSession session = request.getSession(false);
        String expectedToken = (String) session.getAttribute("csrfToken");
        String submittedToken = request.getParameter("csrfToken");
        if (expectedToken == null || submittedToken == null
                || !MessageDigest.isEqual(
                        expectedToken.getBytes(StandardCharsets.UTF_8),
                        submittedToken.getBytes(StandardCharsets.UTF_8))) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Invalid form token.");
            return;
        }

        int friendId;
        try {
            friendId = Integer.parseInt(request.getParameter("userId"));
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid user.");
            return;
        }

        try {
            User friend = friendDAO.getUserById(friendId);
            if (friend == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND,
                        "User not found.");
                return;
            }
            if (!friendDAO.addFriend(user.getId(), friendId)) {
                sendError(response, HttpServletResponse.SC_CONFLICT,
                        "This user is already connected.");
                return;
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.getWriter().write(
                    "{\"id\":" + friendId + ",\"username\":\""
                    + jsonEscape(friend.getUsername())
                    + "\"}");
        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                sendError(response, HttpServletResponse.SC_CONFLICT,
                        "This user is already connected.");
            } else if (e.getErrorCode() == 1452) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND,
                        "User not found.");
            } else {
                log("Unable to connect users", e);
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to connect to this user.");
            }
        } catch (Exception e) {
            log("Unable to connect users", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to connect to this user.");
        }
    }

    private User getAuthenticatedUser(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof User)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in to use friends.");
            return null;
        }
        return (User) session.getAttribute("user");
    }

    private void writeUsers(HttpServletResponse response, List<User> users)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            User user = users.get(i);
            json.append("{\"id\":").append(user.getId())
                    .append(",\"username\":\"")
                    .append(jsonEscape(user.getUsername()))
                    .append("\"}");
        }
        response.getWriter().write(json.append(']').toString());
    }

    private void sendError(
            HttpServletResponse response,
            int status,
            String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(
                "{\"error\":\"" + jsonEscape(message) + "\"}");
    }

    private String jsonEscape(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
            case '"':
                escaped.append("\\\"");
                break;
            case '\\':
                escaped.append("\\\\");
                break;
            case '\b':
                escaped.append("\\b");
                break;
            case '\f':
                escaped.append("\\f");
                break;
            case '\n':
                escaped.append("\\n");
                break;
            case '\r':
                escaped.append("\\r");
                break;
            case '\t':
                escaped.append("\\t");
                break;
            default:
                if (character < 0x20) {
                    escaped.append(String.format("\\u%04x", (int) character));
                } else {
                    escaped.append(character);
                }
            }
        }
        return escaped.toString();
    }
}
