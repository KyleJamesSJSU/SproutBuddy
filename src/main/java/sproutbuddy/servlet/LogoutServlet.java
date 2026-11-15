
package sproutbuddy.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
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
                !java.security.MessageDigest.isEqual(
                    expectedToken.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8),
                    submittedToken.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid logout request.");
            return;
        }

        // Destroy the authenticated session.
        session.invalidate();

        // Prevent browsers from caching the response.
        response.setHeader("Cache-Control", "no-store");

        response.sendRedirect(
                request.getContextPath() + "/login.html");
    }
}
