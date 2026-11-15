
package sproutbuddy.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sproutbuddy.dao.UserDAO;
import sproutbuddy.model.User;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        String expectedToken =
                (String) session.getAttribute("csrfToken");

        String submittedToken =
                request.getParameter("csrfToken");

        if (expectedToken == null ||
                submittedToken == null ||
                !MessageDigest.isEqual(
                    expectedToken.getBytes(StandardCharsets.UTF_8),
                    submittedToken.getBytes(StandardCharsets.UTF_8))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid form token.");
            return;
        }

        String username = request.getParameter("username");
        String email = request.getParameter("email");

        username = username == null ? "" : username.trim();
        email = email == null ? "" : email.trim();

        if (username.isEmpty() || username.length() > 50) {
            redirectWithMessage(request, response,
                    "error", "Invalid username.");
            return;
        }

        if (email.length() > 100 ||
                !email.matches(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            redirectWithMessage(request, response,
                    "error", "Invalid email address.");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            boolean updated = userDAO.updateAccount(
                    user.getId(), username, email);

            if (!updated) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Account not found.");
                return;
            }

            // Refresh session username after successful update.
            session.setAttribute(
                    "user", new User(user.getId(), username));

            redirectWithMessage(request, response,
                    "success", "Account updated successfully.");

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                redirectWithMessage(request, response,
                        "error", "Username or email already in use.");
            } else {
                log("Database error updating account", e);
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to update account.");
            }

        } catch (Exception e) {
            log("Unable to update account", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update account.");
        }
    }

    private void redirectWithMessage(
            HttpServletRequest request,
            HttpServletResponse response,
            String type,
            String message) throws IOException {

        String encoded = URLEncoder.encode(
                message, StandardCharsets.UTF_8.name());

        response.sendRedirect(
                request.getContextPath()
                + "/garden?account=" + type
                + "&message=" + encoded
                + "#garden");
    }
}
