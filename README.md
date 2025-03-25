# Quiz Application - README

## Overview
This Java-based Quiz Application allows users to take quizzes on various subjects, while administrators can manage questions, subjects, and user results. The application features:

- User registration and authentication
- Quiz taking with timed questions
- Result tracking and reporting
- Admin panel for content management
- Export functionality (CSV and PDF)

## Features

### User Features
- Registration: Users can create accounts with email, username, and password
- Login: Secure authentication for returning users
- Quiz Taking: 
  - Select from available subjects
  - Answer multiple-choice questions
  - Automatic question timer (1 minute per question)
- Results: 
  - Immediate score display
  - Export results as PDF or CSV

### Admin Features
- User Management: View and manage user accounts
- Subject Management: Add/edit/delete quiz subjects
- Question Management: 
  - Add/edit/delete questions
  - Set correct answers
- Result Analysis: View user performance statistics

## Technical Requirements

### Prerequisites
- Java JDK 11 or higher
- PostgreSQL 12 or higher
- Apache Maven (for dependency management)
- Apache PDFBox (for PDF export functionality)
- OpenCSV (for CSV export functionality)

## Installation and Setup

1. Clone the repository:

   git clone [https://github.com/shivanejivaji/quiz-app.git](https://github.com/shivanejivaji/Quiz-Application.git)
   cd quiz-app

2. Configure database connection:
   Update the DatabaseConnection.java file with your PostgreSQL credentials:

   private static final String URL = "jdbc:postgresql://localhost:5432/quizdb";
   private static final String USER = "postgres";
   private static final String PASSWORD = "root";
   

3. Build the project:
   mvn clean install

4. Run the application:
   
   java -jar target/quiz-app-1.0.jar

5. jar Files Download Link

   postgresql : https://jdbc.postgresql.org/download/

   Apache PDFBox: https://pdfbox.apache.org/download.html

## Usage

### For Users
1. Register a new account or log in
2. Select a subject from the available options
3. Answer the quiz questions (1 minute per question)
4. View your results and optionally export them

### For Admins
1. Log in with admin credentials (role='admin')
2. Access the admin panel to:
   - Manage users
   - Add/edit subjects
   - Create/modify questions
   - View user results

## Dependencies

The project uses the following main dependencies:

- **PostgreSQL JDBC Driver** (for database connectivity)
- **Apache PDFBox** (for PDF export)
- **OpenCSV** (for CSV export)
- **Java Swing** (for GUI)

## Troubleshooting

### Common Issues

1. **Database Connection Problems**:
   - Verify PostgreSQL is running
   - Check connection credentials in `DatabaseConnection.java`
   - Ensure the database schema is properly set up

2. **PDF Export Issues**:
   - Verify PDFBox dependency is included
   - Check file write permissions in the target directory

3. **UI Rendering Problems**:
   - Ensure Java version is compatible (JDK 11+)
   - Verify all Swing components are properly initialized


## Contact

For support or questions, please contact: [shivanejivaji@gmail.com](mailto:shivanejivaji@gmail.com)
