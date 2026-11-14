package sproutbuddy.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sproutbuddy.dao.CareTaskDAO;
import sproutbuddy.model.User;

@WebServlet("/reminders")
public class ReminderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CareTaskDAO careTaskDAO;

    @Override
    public void init() {
        careTaskDAO = new CareTaskDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getUser(request.getSession(false));
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }
        try {
            request.setAttribute("careTasks", careTaskDAO.getOpenTasksForUser(user.getId()));
            request.getRequestDispatcher("/reminders.jsp").forward(request, response);
        } catch (Exception exception) {
            throw new ServletException("Unable to load care reminders.", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getUser(request.getSession(false));
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }
        try {
            careTaskDAO.completeTask(Integer.parseInt(request.getParameter("taskId")), user.getId());
            response.sendRedirect(request.getContextPath() + "/reminders");
        } catch (NumberFormatException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid care task.");
        } catch (Exception exception) {
            throw new ServletException("Unable to complete care reminder.", exception);
        }
    }

    private User getUser(HttpSession session) {
        Object user = session == null ? null : session.getAttribute("user");
        return user instanceof User ? (User) user : null;
    }
}
