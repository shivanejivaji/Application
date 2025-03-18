import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageUsersWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea usersTextArea;
	private JButton refreshButton, deleteButton;

	public ManageUsersWindow() {
		setTitle("Manage Users");
		setSize(500, 400);
		setLayout(new BorderLayout());

		usersTextArea = new JTextArea();
		refreshButton = new JButton("Refresh");
		deleteButton = new JButton("Delete Selected User");

		add(new JScrollPane(usersTextArea), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(refreshButton);
		buttonPanel.add(deleteButton);
		add(buttonPanel, BorderLayout.SOUTH);

		refreshButton.addActionListener(e -> loadUsers());
		deleteButton.addActionListener(e -> deleteUser());

		loadUsers();
	}

	private void loadUsers() {
		String query = "SELECT user_id, username, email, role FROM users";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			StringBuilder sb = new StringBuilder();
			while (rs.next()) {
				sb.append("ID: ").append(rs.getInt("user_id")).append(", Username: ").append(rs.getString("username"))
						.append(", Email: ").append(rs.getString("email")).append(", Role: ")
						.append(rs.getString("role")).append("\n");
			}
			usersTextArea.setText(sb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteUser() {
		String input = JOptionPane.showInputDialog(this, "Enter User ID to Delete:");
		if (input != null && !input.isEmpty()) {
			int userId = Integer.parseInt(input);
			String query = "DELETE FROM users WHERE user_id = ?";
			try (Connection conn = DatabaseConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setInt(1, userId);
				stmt.executeUpdate();
				loadUsers();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}