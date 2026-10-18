package sproutbuddy.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sproutbuddy.dao.PlantSpeciesDAO;
import sproutbuddy.model.PlantSpecies;

@WebServlet("/plant-catalog")
public class PlantCatalogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlantSpeciesDAO plantSpeciesDAO;

    @Override
    public void init() {
        plantSpeciesDAO = new PlantSpeciesDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        List<PlantSpecies> speciesList =
                plantSpeciesDAO.getAllSpecies();

        request.setAttribute("speciesList", speciesList);

        request.getRequestDispatcher("/plantCatalog.jsp")
               .forward(request, response);
    }
}