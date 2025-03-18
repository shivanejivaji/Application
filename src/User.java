import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class User {
	private String email;
	private String username;
	private String password;
	private static int currentUserId;

	public User(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public boolean register() {

		if (email == null || email.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Email cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		String checkQuery = "SELECT email FROM users WHERE email = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
			checkStmt.setString(1, email);
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next()) {
				JOptionPane.showMessageDialog(null, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		String insertQuery = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
			insertStmt.setString(1, email);
			insertStmt.setString(2, username);
			insertStmt.setString(3, password);
			insertStmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static User login(String username, String password) {
		String query = "SELECT * FROM users WHERE username = ? AND password = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String role = rs.getString("role");
				if ("admin".equals(role)) {
					new AdminPanel().setVisible(true);
				} else {
					new QuizManager().setVisible(true);
				}
				return new User(rs.getString("email"), rs.getString("username"), rs.getString("password"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveResult(int userId, int subjectId, int score, int totalQuestions) {
		String query = "INSERT INTO results (user_id, subject_id, score, total_questions) VALUES (?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, subjectId);
			stmt.setInt(3, score);
			stmt.setInt(4, totalQuestions);
			stmt.executeUpdate();
			System.out.println("Result saved to the database.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getCurrentUserId() {
		return currentUserId;
	}

	public static void setCurrentUserId(int userId) {
		currentUserId = userId;
	}
}