
package sproutbuddy.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sproutbuddy.dao.UserDAO;
import sproutbuddy.dao.UserPlantDAO;
import sproutbuddy.model.User;
import sproutbuddy.model.UserPlant;

@WebServlet("/garden")
public class GardenServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserPlantDAO userPlantDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        userPlantDAO = new UserPlantDAO();
        userDAO = new UserDAO();
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
        

	     // Generate a CSRF token if the session doesn't have one.
	     String csrfToken =
	             (String) session.getAttribute("csrfToken");
	
	     if (csrfToken == null) {
	
	         byte[] randomBytes = new byte[32];
	
	         new java.security.SecureRandom()
	             .nextBytes(randomBytes);
	
	         csrfToken = java.util.Base64.getUrlEncoder()
	             .withoutPadding()
	             .encodeToString(randomBytes);
	
	         session.setAttribute("csrfToken", csrfToken);
	     }
	
	     request.setAttribute("csrfToken", csrfToken);
	     request.setAttribute("currentUser", user);


        try {

            // Retrieve plants belonging to this user
            List<UserPlant> plants =
                    userPlantDAO.getPlantsByUserId(user.getId());


	         // Retrieve the authenticated user's account details.
	         String[] account = userDAO.getAccountById(user.getId());
	
	         if (account == null) {
	             response.sendError(
	                 HttpServletResponse.SC_NOT_FOUND,
	                 "Account not found.");
	             return;
	         }
	
	         request.setAttribute("accountUsername", account[0]);
	         request.setAttribute("accountEmail", account[1]);

            
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
