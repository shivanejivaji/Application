import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton registerButton, loginButton, startQuizButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public QuizApp() {
        setTitle("Quiz Application");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        registerButton = new JButton("Register");
        loginButton = new JButton("Login");
        startQuizButton = new JButton("Start Quiz");
        messageLabel = new JLabel("");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(registerButton);
        add(loginButton);
        add(startQuizButton);
        add(messageLabel);

        registerButton.addActionListener(e -> new RegistrationPanel().setVisible(true));

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = User.login(username, password);

            if (user != null) {
                int userId = getUserIdFromDatabase(username);
                if (userId != -1) {
                    User.setCurrentUserId(userId);
                    messageLabel.setText("Login Successful!");
                    startQuizButton.setEnabled(true);
                } else {
                    messageLabel.setText("Login Failed!");
                }
            } else {
                messageLabel.setText("Login Failed!");
            }
        });

        startQuizButton.addActionListener(e -> new QuizManager().setVisible(true));

        startQuizButton.setEnabled(false);
    }

    public static int getUserIdFromDatabase(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizApp().setVisible(true));
    }
}
