package sproutbuddy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}