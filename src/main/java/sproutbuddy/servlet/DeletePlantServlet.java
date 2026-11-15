
package sproutbuddy.servlet;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sproutbuddy.dao.UserPlantDAO;
import sproutbuddy.model.User;

@WebServlet("/delete-plant")
public class DeletePlantServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserPlantDAO userPlantDAO;

    @Override
    public void init() {
        userPlantDAO = new UserPlantDAO();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Require an authenticated user.
        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in.");
            return;
        }

        // Verify the CSRF token.
        String expectedToken =
                (String) session.getAttribute("csrfToken");

        String submittedToken =
                request.getParameter("csrfToken");

        if (expectedToken == null ||
                submittedToken == null ||
                !java.security.MessageDigest.isEqual(
                    expectedToken.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8),
                    submittedToken.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid form token.");
            return;
        }

        // Validate the plant ID.
        int plantId;

        try {
            plantId = Integer.parseInt(
                    request.getParameter("id"));

            if (plantId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid plant ID.");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            boolean deleted = userPlantDAO.deletePlant(
                    plantId, user.getId());

            if (!deleted) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Plant not found or access denied.");
                return;
            }

            // Return to the Garden after deletion.
            response.sendRedirect(
                    request.getContextPath() + "/garden#garden");

        } catch (Exception e) {
            log("Unable to delete plant", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to delete plant.");
        }
    }
}
