
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

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve the existing session without creating a new one.
        HttpSession session = request.getSession(false);

        // Require the user to be logged in.
        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        // Read and validate the plant ID from the URL.
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

        // Retrieve only a plant owned by this user.
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
}
