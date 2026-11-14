
package sproutbuddy.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sproutbuddy.dao.UserDAO;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    // =====================================
    // GET: Display registration page
    // =====================================

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(
                request.getContextPath() + "/register.html");
    }

    // =====================================
    // POST: Process registration
    // =====================================

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (username != null) {
            username = username.trim();
        }

        if (email != null) {
            email = email.trim();
        }

        // Validate required fields
        if (username == null || username.isEmpty()
                || email == null || email.isEmpty()
                || password == null || password.isEmpty()) {

            redirectWithError(
                    request, response,
                    "All fields are required.");
            return;
        }

        // Validate username length
        if (username.length() > 50) {

            redirectWithError(
                    request, response,
                    "Username must be 50 characters or fewer.");
            return;
        }

        // Validate email
        if (email.length() > 100
                || !email.matches(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            redirectWithError(
                    request, response,
                    "Please enter a valid email address.");
            return;
        }

        // Validate password length
        if (password.length() < 8) {

            redirectWithError(
                    request, response,
                    "Password must contain at least 8 characters.");
            return;
        }

        try {

            // Check duplicate username
            if (userDAO.usernameExists(username)) {

                redirectWithError(
                        request, response,
                        "Username is already taken.");
                return;
            }

            // Check duplicate email
            if (userDAO.emailExists(email)) {

                redirectWithError(
                        request, response,
                        "Email is already registered.");
                return;
            }

            // Insert the new user
            boolean registered =
                    userDAO.registerUser(
                            username, email, password);

            if (registered) {

                // Redirect to the existing working login page
                response.sendRedirect(
                        request.getContextPath() + "/login");

            } else {

                redirectWithError(
                        request, response,
                        "Registration failed. Please try again.");
            }

        } catch (SQLException e) {

            // MySQL duplicate-key error
            if (e.getErrorCode() == 1062) {

                redirectWithError(
                        request, response,
                        "Username or email is already registered.");

            } else {

                log("Database error during registration", e);

                redirectWithError(
                        request, response,
                        "Registration is temporarily unavailable.");
            }

        } catch (Exception e) {

            log("Error during registration", e);

            redirectWithError(
                    request, response,
                    "Registration is temporarily unavailable.");
        }
    }

    // =====================================
    // Redirect with a user-friendly error
    // =====================================

    private void redirectWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message) throws IOException {

        String encodedMessage = URLEncoder.encode(
                message, StandardCharsets.UTF_8.name());

        response.sendRedirect(
                request.getContextPath()
                + "/register.html?error=" + encodedMessage);
    }
}
