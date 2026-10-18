package sproutbuddy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import sproutbuddy.model.PlantSpecies;
import sproutbuddy.util.DBConnection;

public class PlantSpeciesDAO {

    public List<PlantSpecies> getAllSpecies() {

        List<PlantSpecies> speciesList = new ArrayList<>();

        String sql = "SELECT * FROM plant_species";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
        ) {

            while (rs.next()) {

                Integer createdByUserId =
                        (Integer) rs.getObject("created_by_user_id");

                PlantSpecies species = new PlantSpecies(
                        rs.getInt("species_id"),
                        rs.getString("common_name"),
                        rs.getString("scientific_name"),
                        rs.getString("light_requirement"),
                        rs.getString("water_requirement"),
                        rs.getString("care_instructions"),
                        rs.getBoolean("is_custom"),
                        createdByUserId
                );

                speciesList.add(species);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return speciesList;
    }


    public PlantSpecies getSpeciesById(int speciesId) {

        String sql =
                "SELECT * FROM plant_species WHERE species_id = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {

            stmt.setInt(1, speciesId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Integer createdByUserId =
                            (Integer) rs.getObject("created_by_user_id");

                    return new PlantSpecies(
                            rs.getInt("species_id"),
                            rs.getString("common_name"),
                            rs.getString("scientific_name"),
                            rs.getString("light_requirement"),
                            rs.getString("water_requirement"),
                            rs.getString("care_instructions"),
                            rs.getBoolean("is_custom"),
                            createdByUserId
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}