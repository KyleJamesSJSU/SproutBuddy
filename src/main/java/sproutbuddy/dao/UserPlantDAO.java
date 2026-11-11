
package sproutbuddy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sproutbuddy.model.UserPlant;
import sproutbuddy.model.PlantSpecies;

import sproutbuddy.util.DBConnection;

public class UserPlantDAO {

    // CREATE: Add a new plant to a user's collection
    public boolean addPlant(UserPlant plant) throws Exception {

        String sql = "INSERT INTO user_plants "
                + "(user_id, species_id, nickname, location, "
                + "photo, watering_interval_days, last_watered, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, plant.getUserId());
            stmt.setInt(2, plant.getSpeciesId());
            stmt.setString(3, plant.getNickname());
            stmt.setString(4, plant.getLocation());
            stmt.setString(5, plant.getPhoto());

            if (plant.getWateringIntervalDays() != null) {
                stmt.setInt(6, plant.getWateringIntervalDays());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            if (plant.getLastWatered() != null) {
                stmt.setDate(7,
                    java.sql.Date.valueOf(plant.getLastWatered()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }

            stmt.setString(8, plant.getNotes());

            return stmt.executeUpdate() == 1;
        }
    }

    // READ: Retrieve all plants owned by a user
    public List<UserPlant> getPlantsByUserId(int userId) throws Exception {

        List<UserPlant> plants = new ArrayList<>();

        String sql = "SELECT * FROM user_plants "
                   + "WHERE user_id = ? ORDER BY date_added DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    plants.add(mapRowToPlant(rs));
                }
            }
        }

        return plants;
    }

    // READ: Retrieve a single plant owned by a user
 // READ: Retrieve a plant and its species information
    public UserPlant getPlantById(int plantId, int userId)
            throws Exception {

        String sql =
            "SELECT up.*, "
          + "ps.common_name, ps.scientific_name, "
          + "ps.light_requirement, ps.water_requirement, "
          + "ps.care_instructions, ps.is_custom, "
          + "ps.created_by_user_id "
          + "FROM user_plants up "
          + "JOIN plant_species ps "
          + "ON up.species_id = ps.species_id "
          + "WHERE up.plant_id = ? AND up.user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, plantId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    UserPlant plant = mapRowToPlant(rs);

                    PlantSpecies species = new PlantSpecies(
                        rs.getInt("species_id"),
                        rs.getString("common_name"),
                        rs.getString("scientific_name"),
                        rs.getString("light_requirement"),
                        rs.getString("water_requirement"),
                        rs.getString("care_instructions"),
                        rs.getBoolean("is_custom"),
                        (Integer) rs.getObject("created_by_user_id")
                    );

                    plant.setSpecies(species);

                    return plant;
                }
            }
        }

        return null;
    }

    // Convert a database row into a UserPlant object
    private UserPlant mapRowToPlant(ResultSet rs)
            throws SQLException {

        java.sql.Date dateAdded = rs.getDate("date_added");
        java.sql.Date lastWatered = rs.getDate("last_watered");

        return new UserPlant(
            rs.getInt("plant_id"),
            rs.getInt("user_id"),
            rs.getInt("species_id"),
            rs.getString("nickname"),
            dateAdded == null ? null : dateAdded.toLocalDate(),
            rs.getString("location"),
            rs.getString("photo"),
            (Integer) rs.getObject("watering_interval_days"),
            lastWatered == null ? null : lastWatered.toLocalDate(),
            rs.getString("notes")
        );
    }
    

	/**
	 * Updates the nickname of a plant belonging to a user.
	 *
	 * @param plantId ID of the plant being updated
	 * @param userId ID of the authenticated user
	 * @param nickname New plant nickname
	 * @return true if the plant was updated successfully
	 */
	public boolean updatePlantNickname(
	        int plantId, int userId, String nickname)
	        throws Exception {
	
	    String sql = "UPDATE user_plants "
	               + "SET nickname = ? "
	               + "WHERE plant_id = ? AND user_id = ?";
	
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql)) {
	
	        stmt.setString(1, nickname);
	        stmt.setInt(2, plantId);
	        stmt.setInt(3, userId);
	
	        return stmt.executeUpdate() == 1;
	    }
	}
	

	/**
	 * Updates the location and personal notes of a user's plant.
	 */
	public boolean updatePlantDetails(
	        int plantId,
	        int userId,
	        String location,
	        String notes) throws Exception {
	
	    String sql = "UPDATE user_plants "
	               + "SET location = ?, notes = ? "
	               + "WHERE plant_id = ? AND user_id = ?";
	
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql)) {
	
	        stmt.setString(1, location);
	        stmt.setString(2, notes);
	        stmt.setInt(3, plantId);
	        stmt.setInt(4, userId);
	
	        return stmt.executeUpdate() == 1;
	    }
	}


}
