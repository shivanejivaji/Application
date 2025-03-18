import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageSubjectsWindow extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea subjectsTextArea;
    private JButton refreshButton, addButton, deleteButton;

    public ManageSubjectsWindow() {
        setTitle("Manage Subjects");
        setSize(500, 400);
        setLayout(new BorderLayout());

        subjectsTextArea = new JTextArea();
        refreshButton = new JButton("Refresh");
        addButton = new JButton("Add Subject");
        deleteButton = new JButton("Delete Subject");

        add(new JScrollPane(subjectsTextArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadSubjects());
        addButton.addActionListener(e -> addSubject());
        deleteButton.addActionListener(e -> deleteSubject());

        loadSubjects();
    }

    private void loadSubjects() {
        String query = "SELECT * FROM subjects";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("subject_id"))
                  .append(", Name: ").append(rs.getString("subject_name"))
                  .append("\n");
            }
            subjectsTextArea.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSubject() {
        String subjectName = JOptionPane.showInputDialog(this, "Enter Subject Name:");
        if (subjectName != null && !subjectName.isEmpty()) {
            String query = "INSERT INTO subjects (subject_name) VALUES (?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, subjectName);
                stmt.executeUpdate();
                loadSubjects();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteSubject() {
        String input = JOptionPane.showInputDialog(this, "Enter Subject ID to Delete:");
        if (input != null && !input.isEmpty()) {
            int subjectId = Integer.parseInt(input);
            String query = "DELETE FROM subjects WHERE subject_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, subjectId);
                stmt.executeUpdate();
                loadSubjects();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}