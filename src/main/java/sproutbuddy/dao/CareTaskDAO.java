package sproutbuddy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import sproutbuddy.model.CareTask;
import sproutbuddy.util.DBConnection;

public class CareTaskDAO {
    public List<CareTask> getOpenTasksForUser(int userId) throws Exception {
        List<CareTask> tasks = new ArrayList<>();
        String sql = "SELECT t.task_id, p.nickname, s.common_name, t.task_type, "
                + "t.due_date, ct.interval_days FROM care_tasks t "
                + "JOIN user_plants p ON p.plant_id = t.plant_id "
                + "JOIN plant_species s ON s.species_id = p.species_id "
                + "JOIN care_templates ct ON ct.species_id = p.species_id "
                + "AND ct.task_type = t.task_type "
                + "WHERE p.user_id = ? AND t.completed_at IS NULL "
                + "ORDER BY t.due_date, p.nickname, t.task_type";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(new CareTask(resultSet.getInt("task_id"),
                            resultSet.getString("nickname"),
                            resultSet.getString("common_name"),
                            resultSet.getString("task_type"),
                            resultSet.getDate("due_date"),
                            resultSet.getInt("interval_days")));
                }
            }
        }
        return tasks;
    }

    public void completeTask(int taskId, int userId) throws Exception {
        String sql = "UPDATE care_tasks t JOIN user_plants p ON p.plant_id = t.plant_id "
                + "SET t.completed_at = CURRENT_TIMESTAMP "
                + "WHERE t.task_id = ? AND p.user_id = ? AND t.completed_at IS NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setInt(2, userId);
            if (statement.executeUpdate() != 1) {
                throw new IllegalArgumentException("Care task was not found or is already complete.");
            }
        }
    }
}
