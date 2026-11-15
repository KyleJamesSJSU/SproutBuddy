
package sproutbuddy.servlet;

import java.io.IOException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sproutbuddy.dao.PlantSpeciesDAO;
import sproutbuddy.dao.UserPlantDAO;
import sproutbuddy.model.PlantSpecies;
import sproutbuddy.model.User;
import sproutbuddy.model.UserPlant;

@WebServlet("/add-plant")
public class AddPlantServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlantSpeciesDAO speciesDAO;
    private UserPlantDAO userPlantDAO;

    @Override
    public void init() {
        speciesDAO = new PlantSpeciesDAO();
        userPlantDAO = new UserPlantDAO();
    }

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

        try {
            request.setAttribute(
                    "speciesList",
                    speciesDAO.getAllSpecies());

            request.getRequestDispatcher("/addPlant.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            log("Unable to load Add Plant form", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
                    request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        String nickname = request.getParameter("nickname");
        String location = request.getParameter("location");
        String speciesValue = request.getParameter("speciesId");

        if (nickname == null || nickname.trim().isEmpty()
                || nickname.trim().length() > 100) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid plant nickname.");
            return;
        }

        location = location == null ? "" : location.trim();

        if (location.length() > 100) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Location is too long.");
            return;
        }

        int speciesId;

        try {
            speciesId = Integer.parseInt(speciesValue);

            if (speciesId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid plant species.");
            return;
        }

        try {
            PlantSpecies species = speciesDAO.getSpeciesById(speciesId);

            if (species == null ||
                    (species.isCustom() &&
                     !java.util.Objects.equals(
                         species.getCreatedByUserId(),
                         user.getId()))) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Plant species not available.");
                return;
            }

            UserPlant plant = new UserPlant(
                    0,
                    user.getId(),
                    speciesId,
                    nickname.trim(),
                    LocalDate.now(),
                    location,
                    null,
                    null,
                    null,
                    null
            );

            boolean added = userPlantDAO.addPlant(plant);

            if (!added) {
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to add plant.");
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/garden#garden");

        } catch (Exception e) {
            log("Unable to add plant", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to add plant.");
        }
    }
}
