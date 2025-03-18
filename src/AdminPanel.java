import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPanel extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton manageUsersButton, manageSubjectsButton, manageQuestionsButton, updateUserButton,
			displayUserResultButton;

	public AdminPanel() {
		setTitle("Admin Panel");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new GridLayout(3, 1));

		manageUsersButton = new JButton("Manage Users");
		manageSubjectsButton = new JButton("Manage Subjects");
		manageQuestionsButton = new JButton("Manage Questions");
		updateUserButton = new JButton("Update User");
		displayUserResultButton = new JButton("Display User Result");

		add(manageUsersButton);
		add(manageSubjectsButton);
		add(manageQuestionsButton);
		add(updateUserButton);
		add(displayUserResultButton);

		manageUsersButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ManageUsersWindow().setVisible(true);
			}
		});

		manageSubjectsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ManageSubjectsWindow().setVisible(true);
			}
		});

		manageQuestionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ManageQuestionsWindow().setVisible(true);
			}
		});

		updateUserButton.addActionListener(e -> updateUser());
		displayUserResultButton.addActionListener(e -> displayUserResult());

	}

	private void displayUserResult() {

		String input = JOptionPane.showInputDialog(this, "Enter User ID to Display Results:");
		if (input != null && !input.isEmpty()) {
			try {
				int userId = Integer.parseInt(input);

				String query = "SELECT r.score, r.total_questions, s.subject_name, r.timestamp " + "FROM results r "
						+ "JOIN subjects s ON r.subject_id = s.subject_id " + "WHERE r.user_id = ?";
				try (Connection conn = DatabaseConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(query)) {
					stmt.setInt(1, userId);
					ResultSet rs = stmt.executeQuery();

					// Build the result message
					StringBuilder resultMessage = new StringBuilder();
					while (rs.next()) {
						int score = rs.getInt("score");
						int totalQuestions = rs.getInt("total_questions");
						String subjectName = rs.getString("subject_name");
						String timestamp = rs.getString("timestamp");

						resultMessage.append("Subject: ").append(subjectName).append("\n").append("Score: ")
								.append(score).append(" / ").append(totalQuestions).append("\n").append("Timestamp: ")
								.append(timestamp).append("\n\n");
					}

					if (resultMessage.length() > 0) {
						// Display the results
						JOptionPane.showMessageDialog(this, resultMessage.toString(), "User Results",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, "No results found for this user!", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Invalid User ID! Please enter a number.", "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Failed to fetch user results!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void updateUser() {

		String input = JOptionPane.showInputDialog(this, "Enter User ID to Update:");
		if (input != null && !input.isEmpty()) {
			try {
				int userId = Integer.parseInt(input);

				String query = "SELECT * FROM users WHERE user_id = ?";
				try (Connection conn = DatabaseConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(query)) {
					stmt.setInt(1, userId);
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						// Display a form to update the user
						String email = rs.getString("email");
						String username = rs.getString("username");
						String password = rs.getString("password");

						JTextField emailField = new JTextField(email);
						JTextField usernameField = new JTextField(username);
						JTextField passwordField = new JTextField(password);

						JPanel panel = new JPanel(new GridLayout(0, 1));
						panel.add(new JLabel("Email:"));
						panel.add(emailField);
						panel.add(new JLabel("Username:"));
						panel.add(usernameField);
						panel.add(new JLabel("Password:"));
						panel.add(passwordField);

						int result = JOptionPane.showConfirmDialog(this, panel, "Update User",
								JOptionPane.OK_CANCEL_OPTION);
						if (result == JOptionPane.OK_OPTION) {

							String updateQuery = "UPDATE users SET email = ?, username = ?, password = ? WHERE user_id = ?";
							try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
								updateStmt.setString(1, emailField.getText());
								updateStmt.setString(2, usernameField.getText());
								updateStmt.setString(3, passwordField.getText());
								updateStmt.setInt(4, userId);
								updateStmt.executeUpdate();
								JOptionPane.showMessageDialog(this, "User updated successfully!", "Success",
										JOptionPane.INFORMATION_MESSAGE);
							}
						}
					} else {
						JOptionPane.showMessageDialog(this, "User ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Invalid User ID! Please enter a number.", "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Failed to update user!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new AdminPanel().setVisible(true));
	}
}