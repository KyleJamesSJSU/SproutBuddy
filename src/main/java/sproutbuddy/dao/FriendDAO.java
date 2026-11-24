package sproutbuddy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import sproutbuddy.model.User;
import sproutbuddy.util.DBConnection;

public class FriendDAO {

    public List<User> searchUsers(int userId, String query)
            throws Exception {
        String sql =
                "SELECT u.user_id, u.username FROM users u "
              + "WHERE u.user_id <> ? AND u.username LIKE ? "
              + "AND NOT EXISTS (SELECT 1 FROM friend_connections fc "
              + "WHERE (fc.user_id = ? AND fc.friend_id = u.user_id) "
              + "OR (fc.friend_id = ? AND fc.user_id = u.user_id)) "
              + "ORDER BY u.username LIMIT 20";

        List<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String pattern = "%" + query.trim() + "%";
            statement.setInt(1, userId);
            statement.setString(2, pattern);
            statement.setInt(3, userId);
            statement.setInt(4, userId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    users.add(new User(
                            results.getInt("user_id"),
                            results.getString("username")));
                }
            }
        }
        return users;
    }

    public List<User> getFriends(int userId) throws Exception {
        String sql =
                "SELECT u.user_id, u.username FROM friend_connections fc "
              + "JOIN users u ON u.user_id = "
              + "CASE WHEN fc.user_id = ? THEN fc.friend_id ELSE fc.user_id END "
              + "WHERE fc.user_id = ? OR fc.friend_id = ? "
              + "ORDER BY u.username";

        List<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, userId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    users.add(new User(
                            results.getInt("user_id"),
                            results.getString("username")));
                }
            }
        }
        return users;
    }

    public User getUserById(int userId) throws Exception {
        String sql = "SELECT user_id, username FROM users WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return new User(
                            results.getInt("user_id"),
                            results.getString("username"));
                }
            }
        }
        return null;
    }

    public boolean addFriend(int userId, int friendId) throws Exception {
        if (userId == friendId) {
            throw new IllegalArgumentException("You cannot add yourself.");
        }

        int firstUserId = Math.min(userId, friendId);
        int secondUserId = Math.max(userId, friendId);
        String sql =
                "INSERT INTO friend_connections (user_id, friend_id) "
              + "VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, firstUserId);
            statement.setInt(2, secondUserId);
            return statement.executeUpdate() == 1;
        }
    }
}
