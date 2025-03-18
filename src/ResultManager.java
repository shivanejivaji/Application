import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ResultManager extends JFrame {
	private static final long serialVersionUID = 1L;
	private int userId;
	private int score;
	private int totalQuestions;

	public ResultManager(int userId, int score, int totalQuestions) {
		this.userId = userId;

		this.score = score;
		this.totalQuestions = totalQuestions;

		setTitle("Quiz Result");
		setSize(400, 300);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JLabel resultLabel = new JLabel("Your Score: " + score + " / " + totalQuestions, SwingConstants.CENTER);
		add(resultLabel, BorderLayout.CENTER);

		JButton exportCsvButton = new JButton("Export as CSV");
		JButton exportPdfButton = new JButton("Export as PDF");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(exportCsvButton);
		buttonPanel.add(exportPdfButton);
		add(buttonPanel, BorderLayout.SOUTH);

		exportCsvButton.addActionListener(e -> exportResultsToCsv());
		exportPdfButton.addActionListener(e -> exportResultsToPdf());
	}

	private void exportResultsToCsv() {
		String fileName = "user_" + userId + "_results.csv";
		try (FileWriter writer = new FileWriter(fileName)) {

			writer.append("User ID,Score,Total Questions\n");

			writer.append(String.valueOf(userId)).append(",");
			writer.append(String.valueOf(score)).append(",");
			writer.append(String.valueOf(totalQuestions)).append("\n");

			JOptionPane.showMessageDialog(this, "Results exported to " + fileName, "Export Successful",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to export results to CSV!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void exportResultsToPdf() {
		String fileName = "user_" + userId + "_results.pdf";
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage();
			document.addPage(page);

			PDPageContentStream contentStream = new PDPageContentStream(document, page);

			contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
			contentStream.beginText();
			contentStream.newLineAtOffset(100, 700);
			contentStream.showText("Quiz Results for User ID: " + userId);
			contentStream.newLineAtOffset(0, -20);
			contentStream.showText("Score: " + score + " / " + totalQuestions);
			contentStream.endText();
			contentStream.close();

			document.save(fileName);
			JOptionPane.showMessageDialog(this, "Results exported to " + fileName, "Export Successful",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to export results to PDF!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}