import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationPanel extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField emailField, usernameField;
	private JPasswordField passwordField;
	private JButton registerButton;

	public RegistrationPanel() {
		setTitle("User Registration");
		setSize(400, 300);
		setLayout(new GridLayout(4, 2));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		emailField = new JTextField();
		usernameField = new JTextField();
		passwordField = new JPasswordField();
		registerButton = new JButton("Register");

		add(new JLabel("Email:"));
		add(emailField);
		add(new JLabel("Username:"));
		add(usernameField);
		add(new JLabel("Password:"));
		add(passwordField);
		add(new JLabel(""));
		add(registerButton);

		registerButton.addActionListener(e -> registerUser());
	}

	private void registerUser() {
		String email = emailField.getText();
		String username = usernameField.getText();
		String password = new String(passwordField.getPassword());

		if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String query = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, email);
			stmt.setString(2, username);
			stmt.setString(3, password);
			stmt.executeUpdate();
			JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
			dispose();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Registration failed! Email or username already exists.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}