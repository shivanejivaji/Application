import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class QuizManager extends JFrame {
	private static final long serialVersionUID = 1L;
	private JComboBox<String> subjectComboBox;
	private JTextArea questionArea;
	private JRadioButton[] options;
	private JButton previousButton, nextButton, submitButton;
	private ArrayList<String[]> questions;
	private int currentQuestionIndex = 0;
	private int score = 0;
	private Map<Integer, String> userSelections;

	public QuizManager() {
		setTitle("Quiz Manager");
		setSize(500, 400);
		setLayout(new BorderLayout());

		subjectComboBox = new JComboBox<>();
		questionArea = new JTextArea();
		options = new JRadioButton[4];
		previousButton = new JButton("Previous");
		nextButton = new JButton("Next");
		submitButton = new JButton("Submit Test");

		userSelections = new HashMap<>();

		loadSubjects();

		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel("Select Subject:"));
		topPanel.add(subjectComboBox);
		add(topPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.add(questionArea);
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < 4; i++) {
			options[i] = new JRadioButton();
			group.add(options[i]);
			centerPanel.add(options[i]);
		}
		add(centerPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(previousButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(submitButton);
		add(buttonPanel, BorderLayout.SOUTH);

		subjectComboBox.addActionListener(e -> loadQuestions());
		previousButton.addActionListener(e -> previousQuestion());
		nextButton.addActionListener(e -> nextQuestion());
		submitButton.addActionListener(e -> submitTest());

		previousButton.setEnabled(false);
		nextButton.setEnabled(false);
		submitButton.setEnabled(false);
	}

	private void loadSubjects() {
		String query = "SELECT subject_name FROM subjects";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				subjectComboBox.addItem(rs.getString("subject_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadQuestions() {
		String subject = (String) subjectComboBox.getSelectedItem();
		String query = "SELECT * FROM questions WHERE subject_id = (SELECT subject_id FROM subjects WHERE subject_name = ?)";
		questions = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, subject);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] question = new String[6];
				question[0] = rs.getString("question_text");
				question[1] = rs.getString("option1");
				question[2] = rs.getString("option2");
				question[3] = rs.getString("option3");
				question[4] = rs.getString("option4");
				question[5] = rs.getString("correct_answer");
				questions.add(question);
			}
			System.out.println("Loaded " + questions.size() + " questions for subject: " + subject);
			currentQuestionIndex = 0;
			score = 0;
			userSelections.clear();
			displayQuestion();

			if (questions.size() > 0) {
				previousButton.setEnabled(false);
				nextButton.setEnabled(true);
				submitButton.setEnabled(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void displayQuestion() {
		if (currentQuestionIndex < questions.size()) {
			String[] question = questions.get(currentQuestionIndex);
			questionArea.setText(question[0]);

			String selectedOption = userSelections.get(currentQuestionIndex);
			for (int i = 0; i < 4; i++) {
				options[i].setText(question[i + 1]);
				options[i].setSelected(question[i + 1].equals(selectedOption));
			}

			System.out.println("Displaying question: " + question[0]);

			previousButton.setEnabled(currentQuestionIndex > 0);
			nextButton.setEnabled(currentQuestionIndex < questions.size() - 1);
		} else {
			submitTest();
		}
	}

	private void previousQuestion() {
		saveCurrentSelection();
		if (currentQuestionIndex > 0) {
			currentQuestionIndex--;
			displayQuestion();
		}
	}

	private void nextQuestion() {
		saveCurrentSelection();
		if (currentQuestionIndex < questions.size() - 1) {
			currentQuestionIndex++;
			displayQuestion();
		}
	}

	private void saveCurrentSelection() {

		for (int i = 0; i < 4; i++) {
			if (options[i].isSelected()) {
				userSelections.put(currentQuestionIndex, options[i].getText());
				break;
			}
		}
	}

	private void submitTest() {

		saveCurrentSelection();

		for (int i = 0; i < questions.size(); i++) {
			String selectedOption = userSelections.get(i);
			String correctAnswer = questions.get(i)[5];

			if (selectedOption != null && selectedOption.trim().equalsIgnoreCase(correctAnswer.trim())) {
				score++;
			}
		}

		int userId = getCurrentUserId();
		int subjectId = getCurrentSubjectId();
		User.saveResult(userId, subjectId, score, questions.size());

		new ResultManager(userId, score, questions.size()).setVisible(true);
		dispose();
	}

	private int getCurrentUserId() {
		return User.getCurrentUserId();
	}

	private int getCurrentSubjectId() {
		String subject = (String) subjectComboBox.getSelectedItem();
		String query = "SELECT subject_id FROM subjects WHERE subject_name = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, subject);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("subject_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
}