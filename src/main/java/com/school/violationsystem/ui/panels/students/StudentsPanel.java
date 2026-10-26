package com.school.violationsystem.ui.panels.students;

import com.school.violationsystem.model.GradeLevel;
import com.school.violationsystem.model.Section;
import com.school.violationsystem.model.Student;
import com.school.violationsystem.model.User;
import com.school.violationsystem.service.StudentService;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class StudentsPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(StudentsPanel.class.getName());
    private static final String GRADE_LEVELS = "grade-levels";
    private static final String SECTIONS = "sections";
    private static final String STUDENTS = "students";
    private static final String STUDENT_DETAILS = "student-details";
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC");

    private final StudentService studentService = new StudentService();
    private final CardLayout cardLayout = new CardLayout();
    private final SelectionPanel<GradeLevel> gradeLevelPanel;
    private final SelectionPanel<Section> sectionPanel;
    private final StudentListPanel studentListPanel;
    private final User user;
    private StudentDetailsPanel studentDetailsPanel;
    private String currentCard = GRADE_LEVELS;
    private SwingWorker<?, ?> loadingWorker;

    public StudentsPanel(User user) {
        super();
        this.user = user;
        setLayout(cardLayout);
        setBackground(COLOR_SURFACE);

        gradeLevelPanel = new SelectionPanel<>("Select a Grade Level", null);
        sectionPanel = new SelectionPanel<>("Select a Section", this::showGradeLevels);
        studentListPanel = new StudentListPanel(this::showSections, this::showStudentDetails);

        add(gradeLevelPanel, GRADE_LEVELS);
        add(sectionPanel, SECTIONS);
        add(studentListPanel, STUDENTS);
        cardLayout.show(this, GRADE_LEVELS);
    }

    public void onShown() {
        if (currentCard.equals(GRADE_LEVELS)) {
            loadGradeLevels();
        } else if (currentCard.equals(STUDENT_DETAILS) && studentDetailsPanel != null) {
            studentDetailsPanel.onShown();
        }
    }

    public void onHidden() {
        cancelLoading();
        if (studentDetailsPanel != null) {
            studentDetailsPanel.onHidden();
        }
    }

    private void loadGradeLevels() {
        currentCard = GRADE_LEVELS;
        cardLayout.show(this, GRADE_LEVELS);
        gradeLevelPanel.showLoading("Loading available grade levels...");
        loadAsync(
                "Could not load grade levels",
                studentService::getAvailableGradeLevels,
                gradeLevelPanel::showError,
                gradeLevels -> gradeLevelPanel.showOptions(
                        gradeLevels,
                        GradeLevel::name,
                        this::loadSections,
                        "No grade levels with sections are available in the active academic year."));
    }

    private void loadSections(GradeLevel gradeLevel) {
        currentCard = SECTIONS;
        sectionPanel.setTitle("Select a Section in " + gradeLevel.name());
        sectionPanel.showLoading("Loading sections...");
        cardLayout.show(this, SECTIONS);
        loadAsync(
                "Could not load sections",
                () -> studentService.getSectionsByGradeLevel(gradeLevel.id()),
                sectionPanel::showError,
                sections -> sectionPanel.showOptions(
                        sections,
                        section -> section.name() + " (" + section.academicYear() + ")",
                        this::loadStudents,
                        "No sections are available for this grade level in the active academic year."));
    }

    private void loadStudents(Section section) {
        currentCard = STUDENTS;
        studentListPanel.setTitle(section.name() + " — " + section.academicYear());
        studentListPanel.showLoading();
        cardLayout.show(this, STUDENTS);
        loadAsync(
                "Could not load students",
                () -> studentService.getStudentsBySection(section.id()),
                studentListPanel::showError,
                studentListPanel::showStudents);
    }

    private void showGradeLevels() {
        cancelLoading();
        currentCard = GRADE_LEVELS;
        cardLayout.show(this, GRADE_LEVELS);
    }

    private void showSections() {
        cancelLoading();
        currentCard = SECTIONS;
        cardLayout.show(this, SECTIONS);
    }

    private void showStudentDetails(Student student) {
        if (studentDetailsPanel != null) {
            studentDetailsPanel.onHidden();
            remove(studentDetailsPanel);
        }
        studentDetailsPanel = new StudentDetailsPanel(student, user, () -> {
            studentDetailsPanel.onHidden();
            currentCard = STUDENTS;
            cardLayout.show(this, STUDENTS);
        });
        add(studentDetailsPanel, STUDENT_DETAILS);
        currentCard = STUDENT_DETAILS;
        cardLayout.show(this, STUDENT_DETAILS);
        revalidate();
        repaint();
    }

    private <T> void loadAsync(
            String errorContext,
            SqlQuery<T> query,
            Consumer<String> onError,
            Consumer<List<T>> onSuccess) {
        cancelLoading();
        SwingWorker<List<T>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<T> doInBackground() throws Exception {
                return query.execute();
            }

            @Override
            protected void done() {
                if (isCancelled() || loadingWorker != this) {
                    return;
                }

                try {
                    onSuccess.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, errorContext, e);
                    onError.accept(errorContext + ": loading was interrupted.");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    LOGGER.log(Level.SEVERE, errorContext, cause);
                    String detail = cause.getMessage();
                    onError.accept(errorContext + (detail == null || detail.isBlank() ? "." : ": " + detail));
                } finally {
                    if (loadingWorker == this) {
                        loadingWorker = null;
                    }
                }
            }
        };
        loadingWorker = worker;
        worker.execute();
    }

    private void cancelLoading() {
        if (loadingWorker != null) {
            loadingWorker.cancel(true);
            loadingWorker = null;
        }
    }

    @FunctionalInterface
    private interface SqlQuery<T> {
        List<T> execute() throws Exception;
    }
}