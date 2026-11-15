
package sproutbuddy.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sproutbuddy.dao.UserPlantDAO;
import sproutbuddy.model.User;
import sproutbuddy.model.UserPlant;

@WebServlet("/garden")
public class GardenServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserPlantDAO userPlantDAO;

    @Override
    public void init() {
        userPlantDAO = new UserPlantDAO();
    }

    // =====================================
    // GET: Display the user's garden
    // =====================================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve the existing session
        HttpSession session = request.getSession(false);

        // Require an authenticated user
        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {

            // Retrieve plants belonging to this user
            List<UserPlant> plants =
                    userPlantDAO.getPlantsByUserId(user.getId());

            // Pass the plants to the JSP
            request.setAttribute("plants", plants);

            request.getRequestDispatcher("/garden.jsp")
                   .forward(request, response);

        } catch (Exception e) {

            log("Error retrieving user's garden", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load your garden.");
        }
    }
}
