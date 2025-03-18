import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageQuestionsWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea questionsTextArea;
	private JButton refreshButton, addButton, updateButton, deleteButton;

	public ManageQuestionsWindow() {
		setTitle("Manage Questions");
		setSize(600, 500);
		setLayout(new BorderLayout());

		questionsTextArea = new JTextArea();
		refreshButton = new JButton("Refresh");
		addButton = new JButton("Add Question");
		updateButton = new JButton("Update Question");
		deleteButton = new JButton("Delete Question");

		add(new JScrollPane(questionsTextArea), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(refreshButton);
		buttonPanel.add(addButton);
		buttonPanel.add(updateButton);
		buttonPanel.add(deleteButton);
		add(buttonPanel, BorderLayout.SOUTH);

		refreshButton.addActionListener(e -> loadQuestions());
		addButton.addActionListener(e -> addQuestion());
		updateButton.addActionListener(e -> updateQuestion());
		deleteButton.addActionListener(e -> deleteQuestion());

		loadQuestions();
	}

	private void loadQuestions() {
		String query = "SELECT * FROM questions";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			StringBuilder sb = new StringBuilder();
			while (rs.next()) {
				sb.append("ID: ").append(rs.getInt("question_id")).append(", Subject ID: ")
						.append(rs.getInt("subject_id")).append(", Question: ").append(rs.getString("question_text"))
						.append(", Correct Answer: ").append(rs.getString("correct_answer")).append("\n");
			}
			questionsTextArea.setText(sb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addQuestion() {

		JDialog addQuestionDialog = new JDialog(this, "Add Question", true);
		addQuestionDialog.setLayout(new GridLayout(0, 2));
		addQuestionDialog.setSize(400, 300);

		JTextField subjectIdField = new JTextField();
		JTextArea questionTextArea = new JTextArea();
		JTextField option1Field = new JTextField();
		JTextField option2Field = new JTextField();
		JTextField option3Field = new JTextField();
		JTextField option4Field = new JTextField();
		JTextField correctAnswerField = new JTextField();

		addQuestionDialog.add(new JLabel("Subject ID:"));
		addQuestionDialog.add(subjectIdField);
		addQuestionDialog.add(new JLabel("Question Text:"));
		addQuestionDialog.add(new JScrollPane(questionTextArea));
		addQuestionDialog.add(new JLabel("Option 1:"));
		addQuestionDialog.add(option1Field);
		addQuestionDialog.add(new JLabel("Option 2:"));
		addQuestionDialog.add(option2Field);
		addQuestionDialog.add(new JLabel("Option 3:"));
		addQuestionDialog.add(option3Field);
		addQuestionDialog.add(new JLabel("Option 4:"));
		addQuestionDialog.add(option4Field);
		addQuestionDialog.add(new JLabel("Correct Answer:"));
		addQuestionDialog.add(correctAnswerField);

		JButton saveButton = new JButton("Save");
		JButton cancelButton = new JButton("Cancel");

		saveButton.addActionListener(e -> {

			if (subjectIdField.getText().isEmpty() || questionTextArea.getText().isEmpty()
					|| option1Field.getText().isEmpty() || option2Field.getText().isEmpty()
					|| option3Field.getText().isEmpty() || option4Field.getText().isEmpty()
					|| correctAnswerField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(addQuestionDialog, "All fields are required!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			int subjectId = Integer.parseInt(subjectIdField.getText());
			String questionText = questionTextArea.getText();
			String option1 = option1Field.getText();
			String option2 = option2Field.getText();
			String option3 = option3Field.getText();
			String option4 = option4Field.getText();
			String correctAnswer = correctAnswerField.getText();

			String query = "INSERT INTO questions (subject_id, question_text, option1, option2, option3, option4, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
			try (Connection conn = DatabaseConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setInt(1, subjectId);
				stmt.setString(2, questionText);
				stmt.setString(3, option1);
				stmt.setString(4, option2);
				stmt.setString(5, option3);
				stmt.setString(6, option4);
				stmt.setString(7, correctAnswer);
				stmt.executeUpdate();
				JOptionPane.showMessageDialog(addQuestionDialog, "Question added successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				addQuestionDialog.dispose();
				loadQuestions();
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(addQuestionDialog, "Failed to add question!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		cancelButton.addActionListener(e -> addQuestionDialog.dispose());

		addQuestionDialog.add(saveButton);
		addQuestionDialog.add(cancelButton);

		addQuestionDialog.setVisible(true);
	}

	private void updateQuestion() {

		String input = JOptionPane.showInputDialog(this, "Enter Question ID to Update:");
		if (input != null && !input.isEmpty()) {
			try {
				int questionId = Integer.parseInt(input);

				String query = "SELECT * FROM questions WHERE question_id = ?";
				try (Connection conn = DatabaseConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(query)) {
					stmt.setInt(1, questionId);
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {

						String questionText = rs.getString("question_text");
						String option1 = rs.getString("option1");
						String option2 = rs.getString("option2");
						String option3 = rs.getString("option3");
						String option4 = rs.getString("option4");
						String correctAnswer = rs.getString("correct_answer");

						JTextField questionField = new JTextField(questionText);
						JTextField option1Field = new JTextField(option1);
						JTextField option2Field = new JTextField(option2);
						JTextField option3Field = new JTextField(option3);
						JTextField option4Field = new JTextField(option4);
						JTextField correctAnswerField = new JTextField(correctAnswer);

						JPanel panel = new JPanel(new GridLayout(0, 1));
						panel.add(new JLabel("Question Text:"));
						panel.add(questionField);
						panel.add(new JLabel("Option 1:"));
						panel.add(option1Field);
						panel.add(new JLabel("Option 2:"));
						panel.add(option2Field);
						panel.add(new JLabel("Option 3:"));
						panel.add(option3Field);
						panel.add(new JLabel("Option 4:"));
						panel.add(option4Field);
						panel.add(new JLabel("Correct Answer:"));
						panel.add(correctAnswerField);

						int result = JOptionPane.showConfirmDialog(this, panel, "Update Question",
								JOptionPane.OK_CANCEL_OPTION);
						if (result == JOptionPane.OK_OPTION) {

							String updateQuery = "UPDATE questions SET question_text = ?, option1 = ?, option2 = ?, option3 = ?, option4 = ?, correct_answer = ? WHERE question_id = ?";
							try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
								updateStmt.setString(1, questionField.getText());
								updateStmt.setString(2, option1Field.getText());
								updateStmt.setString(3, option2Field.getText());
								updateStmt.setString(4, option3Field.getText());
								updateStmt.setString(5, option4Field.getText());
								updateStmt.setString(6, correctAnswerField.getText());
								updateStmt.setInt(7, questionId);
								updateStmt.executeUpdate();
								JOptionPane.showMessageDialog(this, "Question updated successfully!", "Success",
										JOptionPane.INFORMATION_MESSAGE);
								loadQuestions();
							}
						}
					} else {
						JOptionPane.showMessageDialog(this, "Question ID not found!", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Invalid Question ID! Please enter a number.", "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Failed to update question!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void deleteQuestion() {

		String input = JOptionPane.showInputDialog(this, "Enter Question ID to Delete:");
		if (input != null && !input.isEmpty()) {
			try {
				int questionId = Integer.parseInt(input);

				int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this question?",
						"Confirm Deletion", JOptionPane.YES_NO_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {

					String query = "DELETE FROM questions WHERE question_id = ?";
					try (Connection conn = DatabaseConnection.getConnection();
							PreparedStatement stmt = conn.prepareStatement(query)) {
						stmt.setInt(1, questionId);
						int rowsDeleted = stmt.executeUpdate();

						if (rowsDeleted > 0) {
							JOptionPane.showMessageDialog(this, "Question deleted successfully!", "Success",
									JOptionPane.INFORMATION_MESSAGE);
							loadQuestions();
						} else {
							JOptionPane.showMessageDialog(this, "Question ID not found!", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (SQLException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(this, "Failed to delete question!", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Invalid Question ID! Please enter a number.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}