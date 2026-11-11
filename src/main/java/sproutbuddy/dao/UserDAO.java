package sproutbuddy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sproutbuddy.model.User;
import sproutbuddy.util.DBConnection;
import sproutbuddy.util.PasswordUtil;

public class UserDAO {


	public User authenticate(String username, String password) throws Exception {
	    String sql = "SELECT user_id, username, password_hash "
	               + "FROM users WHERE username = ?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, username);   

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()
	                    && PasswordUtil.verifyPassword(
	                            password, rs.getString("password_hash"))) {
	                return new User(rs.getInt("user_id"),
	                                rs.getString("username"));
	            }
	        }
	    }
	    return null;
	}
	
	// =====================================
    // REGISTRATION: Create a new user
    // =====================================

    public boolean registerUser(
            String username,
            String email,
            String password) throws Exception {

        // Hash the password before storing it
        String passwordHash =
                PasswordUtil.hashPassword(password);

        String sql = "INSERT INTO users "
                   + "(username, email, password_hash) "
                   + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, passwordHash);

            return ps.executeUpdate() == 1;
        }
    }

    // =====================================
    // REGISTRATION: Check for existing username
    // =====================================

    public boolean usernameExists(String username)
            throws Exception {

        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // =====================================
    // REGISTRATION: Check for existing email
    // =====================================

    public boolean emailExists(String email)
            throws Exception {

        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}