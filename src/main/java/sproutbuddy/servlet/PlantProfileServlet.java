
package sproutbuddy.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sproutbuddy.dao.UserPlantDAO;
import sproutbuddy.model.User;
import sproutbuddy.model.UserPlant;

@WebServlet("/plant-profile")
public class PlantProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserPlantDAO userPlantDAO;

    @Override
    public void init() {
        userPlantDAO = new UserPlantDAO();
    }

    // =====================================
    // GET: Display an individual plant
    // =====================================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {

            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        int plantId;

        try {
            plantId = Integer.parseInt(
                    request.getParameter("id"));

            if (plantId <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid plant ID.");
                return;
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "A valid plant ID is required.");
            return;
        }

        try {
            UserPlant plant =
                    userPlantDAO.getPlantById(plantId, userId);

            if (plant == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Plant not found.");
                return;
            }

            request.setAttribute("plant", plant);

            request.getRequestDispatcher("/plantProfile.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            log("Error retrieving plant profile", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load plant profile.");
        }
    }

    // =====================================
    // POST: Update plant information
    // =====================================
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
                    request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        int plantId;

        try {
            plantId = Integer.parseInt(
                    request.getParameter("id"));

            if (plantId <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid plant ID.");
                return;
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "A valid plant ID is required.");
            return;
        }

        // Determine which field the user is updating
        String action = request.getParameter("action");

        try {
            boolean updated;

            // =====================================
            // UPDATE NICKNAME
            // =====================================
            if ("rename".equals(action)) {

                String nickname =
                        request.getParameter("nickname");

                if (nickname == null ||
                        nickname.trim().isEmpty() ||
                        nickname.trim().length() > 100) {

                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Invalid plant nickname.");
                    return;
                }

                updated = userPlantDAO.updatePlantNickname(
                        plantId,
                        userId,
                        nickname.trim());

            // =====================================
            // UPDATE LOCATION AND NOTES
            // =====================================
            } else if ("updateDetails".equals(action)) {

                String location =
                        request.getParameter("location");

                String notes =
                        request.getParameter("notes");

                location = location == null
                        ? "" : location.trim();

                notes = notes == null
                        ? "" : notes.trim();

                if (location.length() > 100 ||
                        notes.length() > 5000) {

                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Plant details exceed allowed length.");
                    return;
                }

                updated = userPlantDAO.updatePlantDetails(
                        plantId,
                        userId,
                        location,
                        notes);

            } else {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Unknown update action.");
                return;
            }

            if (!updated) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Plant not found or access denied.");
                return;
            }

            // Redirect back to the updated plant profile
            response.sendRedirect(
                    request.getContextPath()
                    + "/plant-profile?id=" + plantId);

        } catch (Exception e) {
            log("Error updating plant profile", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update plant profile.");
        }
    }
}
