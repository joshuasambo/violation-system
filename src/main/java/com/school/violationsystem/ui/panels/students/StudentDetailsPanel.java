package com.school.violationsystem.ui.panels.students;

import com.school.violationsystem.model.Guardian;
import com.school.violationsystem.model.IncidentCaseSummary;
import com.school.violationsystem.model.Student;
import com.school.violationsystem.model.StudentPlacement;
import com.school.violationsystem.model.StudentViolationReport;
import com.school.violationsystem.model.User;
import com.school.violationsystem.service.StudentService;
import com.school.violationsystem.service.ViolationService;
import com.school.violationsystem.ui.components.RoundedScrollBarUI;
import com.school.violationsystem.ui.dialogs.ViolationDetailsDialog;
import com.school.violationsystem.ui.dialogs.ViolationReportDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class StudentDetailsPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(StudentDetailsPanel.class.getName());
    private static final Color COLOR_PRIMARY = Color.decode("#282385");
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC");
    private static final Color COLOR_TEXT = Color.decode("#141238");
    private static final Color COLOR_MUTED = Color.decode("#5A587A");
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_BORDER = Color.decode("#E2E5EE");
    private static final Color COLOR_BUTTON_GREY = Color.decode("#E4E6F0");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private final Student student;
    private final User user;
    private final StudentService studentService = new StudentService();
    private final ViolationService violationService = new ViolationService();

    // 2-column grid layout with tight vertical gaps for guardians
    private final JPanel guardiansPanel = new JPanel(new GridLayout(0, 2, 12, 6));
    private final JLabel guardianStatus = new JLabel("Loading guardian information...");
    private final JLabel reportStatus = new JLabel("Loading violation reports...");
    private final JLabel placementLabel = new JLabel("Grade and section: Loading...");
    
    private final DefaultTableModel reportTableModel = new DefaultTableModel(
            new Object[] {"Case", "Incident Date", "Violation", "Submitted", "Actions"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4; // Only action button column is interactive
        }
    };
    private final JTable reportTable = new JTable(reportTableModel);
    private final JButton witnessReportButton =
            createRoundedButton("Add Witness Report", COLOR_BUTTON_GREY, COLOR_TEXT, false);
    private final Runnable onBack;
    private List<StudentViolationReport> displayedReports = List.of();
    private Set<Integer> reportableCaseIds = Set.of();
    private SwingWorker<DetailsData, Void> detailsWorker;

    public StudentDetailsPanel(Student student, User user, Runnable onBack) {
        super(new BorderLayout(16, 16));
        this.student = student;
        this.user = user;
        this.onBack = onBack;

        RoundedPanel root = new RoundedPanel(new BorderLayout(16, 16), 0, COLOR_SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header Section
        JLabel nameLabel = new JLabel(fullName(student));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(COLOR_TEXT);

        JLabel studentInfoLabel = new JLabel(
                "Student No. " + student.getSchoolStudentNumber()
                        + "   |   Enrollment: " + student.getEnrollmentStatus());
        studentInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentInfoLabel.setForeground(COLOR_MUTED);

        placementLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        placementLabel.setForeground(COLOR_MUTED);

        JPanel header = new JPanel(new BorderLayout(16, 4));
        header.setOpaque(false);
        
        JPanel studentInfo = new JPanel();
        studentInfo.setLayout(new BoxLayout(studentInfo, BoxLayout.Y_AXIS));
        studentInfo.setOpaque(false);
        studentInfo.add(nameLabel);
        studentInfo.add(Box.createVerticalStrut(4));
        studentInfo.add(studentInfoLabel);
        studentInfo.add(Box.createVerticalStrut(2));
        studentInfo.add(placementLabel);
        
        header.add(studentInfo, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // Guardians Section
        guardiansPanel.setOpaque(false);
        guardianStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        guardianStatus.setForeground(COLOR_MUTED);

        JScrollPane guardianScrollPane = new JScrollPane(guardiansPanel);
        guardianScrollPane.setBorder(null);
        guardianScrollPane.setOpaque(false);
        guardianScrollPane.getViewport().setOpaque(false);
        RoundedScrollBarUI.install(guardianScrollPane);

        JPanel guardianSection = createSection("Guardian Information", guardianScrollPane, guardianStatus);

        // Violation Reports Section
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportTable.setForeground(COLOR_TEXT);
        reportTable.setRowHeight(40);
        reportTable.setFillsViewportHeight(true);
        reportTable.setAutoCreateRowSorter(true);
        reportTable.setShowVerticalLines(false);
        reportTable.setShowHorizontalLines(true);
        reportTable.setGridColor(COLOR_BORDER);
        reportTable.setSelectionBackground(new Color(40, 35, 133, 20));
        reportTable.setSelectionForeground(COLOR_TEXT);
        reportTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        reportTable.getSelectionModel().addListSelectionListener(event -> updateWitnessReportButton());

        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        reportTable.getTableHeader().setBackground(COLOR_PRIMARY);
        reportTable.getTableHeader().setForeground(COLOR_WHITE);
        reportTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int column = 0; column < reportTable.getColumnCount() - 1; column++) {
            reportTable.getColumnModel().getColumn(column).setCellRenderer(centeredRenderer);
        }

        // Configure Actions column with tiny pill button renderer/editor and fixed width
        reportTable.getColumnModel().getColumn(4).setCellRenderer(new PillButtonRenderer());
        reportTable.getColumnModel().getColumn(4).setCellEditor(new PillButtonEditor(new JCheckBox()));
        reportTable.getColumnModel().getColumn(4).setPreferredWidth(85);
        reportTable.getColumnModel().getColumn(4).setMaxWidth(100);
        reportTable.getColumnModel().getColumn(4).setMinWidth(75);

        JScrollPane reportScrollPane = new JScrollPane(reportTable);
        reportScrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));
        RoundedScrollBarUI.install(reportScrollPane);

        reportStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportStatus.setForeground(COLOR_MUTED);
        reportStatus.setText("Select an open incident to add a separate first-hand witness account.");

        JPanel reportSection = createSection("Violation Reports", reportScrollPane, reportStatus);

        // Main Center Content Split
        JPanel sections = new JPanel(new GridLayout(2, 1, 0, 12));
        sections.setOpaque(false);
        sections.add(guardianSection);
        sections.add(reportSection);
        root.add(sections, BorderLayout.CENTER);

        // Footer Actions
        JButton reportButton = createRoundedButton("Report a Violation", COLOR_PRIMARY, COLOR_WHITE, true);
        reportButton.addActionListener(event -> new ViolationReportDialog(
                SwingUtilities.getWindowAncestor(this), student, user, this::loadDetails).setVisible(true));
        witnessReportButton.setEnabled(false);
        witnessReportButton.addActionListener(event -> openWitnessReport());

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        JPanel reportActions = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        reportActions.setOpaque(false);
        reportActions.add(reportButton);
        reportActions.add(witnessReportButton);
        footer.add(reportActions, BorderLayout.WEST);
        JButton backButton = createRoundedButton("Back to students", COLOR_BUTTON_GREY, COLOR_TEXT, false);
        backButton.addActionListener(event -> onBack.run());
        footer.add(backButton, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);
        loadDetails();
    }

    public void onShown() {
        loadDetails();
    }

    public void onHidden() {
        if (detailsWorker != null) {
            detailsWorker.cancel(true);
            detailsWorker = null;
        }
    }

    private JPanel createSection(String title, JScrollPane content, JLabel statusLabel) {
        RoundedPanel section = new RoundedPanel(new BorderLayout(0, 6), 16, COLOR_WHITE);
        section.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(COLOR_TEXT);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 4));
        centerPanel.setOpaque(false);
        centerPanel.add(content, BorderLayout.CENTER);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        section.add(headerPanel, BorderLayout.NORTH);
        section.add(centerPanel, BorderLayout.CENTER);
        return section;
    }

    private JButton createRoundedButton(String text, Color bg, Color fg, boolean isPrimary) {
        return new JButton(text) {
            {
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setForeground(fg);
                setBackground(bg);
                setFocusPainted(false);
                setBorderPainted(false);
                setContentAreaFilled(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(isPrimary ? bg.brighter() : bg.darker());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    // Tiny, pill-shaped round button component for table action cells
    private static class TinyPillButton extends JButton {
        public TinyPillButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(COLOR_TEXT);
            setBackground(COLOR_BUTTON_GREY);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Keep padding small so the button stays tiny
            setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed()) {
                g2.setColor(COLOR_BUTTON_GREY.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(COLOR_BUTTON_GREY.darker());
            } else {
                g2.setColor(COLOR_BUTTON_GREY);
            }
            // Use getHeight() for corner arc diameter to achieve full round pill shape
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Helper panel to center-align the tiny button within the table cell
    private static class PillButtonPanel extends JPanel {
        private final TinyPillButton button;

        public PillButtonPanel(String text) {
            super(new GridBagLayout());
            setOpaque(false);
            button = new TinyPillButton(text);
            add(button, new GridBagConstraints());
        }

        public void setText(String text) {
            button.setText(text);
        }
    }

    private static class PillButtonRenderer extends PillButtonPanel implements TableCellRenderer {
        public PillButtonRenderer() {
            super("View");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "View" : value.toString());
            return this;
        }
    }

    private class PillButtonEditor extends DefaultCellEditor {
        private final PillButtonPanel panel;
        private String label;
        private boolean clicked;
        private int row;

        public PillButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new PillButtonPanel("View");
            // Add listener to the internal button directly via action handling if needed, 
            // but DefaultCellEditor triggers on cell interaction through editor value.
            panel.button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "View" : value.toString();
            panel.setText(label);
            clicked = true;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int modelRow = reportTable.convertRowIndexToModel(row);
                if (modelRow >= 0 && modelRow < displayedReports.size()) {
                    StudentViolationReport report = displayedReports.get(modelRow);
                    SwingUtilities.invokeLater(() -> new ViolationDetailsDialog(
                            (java.awt.Frame) SwingUtilities.getWindowAncestor(StudentDetailsPanel.this),
                            report
                    ).setVisible(true));
                }
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void loadDetails() {
        if (detailsWorker != null) {
            detailsWorker.cancel(true);
        }
        reportableCaseIds = Set.of();
        witnessReportButton.setEnabled(false);
        reportTable.clearSelection();
        guardianStatus.setText("Loading guardian information...");
        reportStatus.setText("Loading violation reports...");
        detailsWorker = new SwingWorker<>() {
            @Override
            protected DetailsData doInBackground() throws Exception {
                return new DetailsData(
                        studentService.getActivePlacementByStudent(student.getStudentId()),
                        studentService.getGuardiansByStudent(student.getStudentId()),
                        violationService.getReportsByStudent(student.getStudentId()),
                        violationService.getReportableCasesByStudent(student.getStudentId(), user));
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    return;
                }
                try {
                    DetailsData data = get();
                    showPlacement(data.placement());
                    showGuardians(data.guardians());
                    reportableCaseIds = data.reportableCases().stream()
                            .map(IncidentCaseSummary::caseId)
                            .collect(Collectors.toSet());
                    showReports(data.reports());
                    updateWitnessReportButton();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, "Loading student details was interrupted.", e);
                    placementLabel.setText("Grade and section could not be loaded.");
                    guardianStatus.setText("Could not load guardian information.");
                    reportStatus.setText("Could not load violation reports.");
                } catch (ExecutionException e) {
                    LOGGER.log(Level.SEVERE, "Could not load student details.", e.getCause());
                    placementLabel.setText("Grade and section could not be loaded.");
                    guardianStatus.setText("Could not load guardian information.");
                    reportStatus.setText("Could not load violation reports.");
                }
            }
        };
        detailsWorker.execute();
    }

    private void showPlacement(Optional<StudentPlacement> placement) {
        placementLabel.setText(placement
                .map(value -> "Grade: " + removeLeadingLabel(value.gradeName(), "Grade")
                        + "   |   Section: " + removeLeadingLabel(value.sectionName(), "Section"))
                .orElse("No active grade or section enrollment."));
    }

    private void showGuardians(List<Guardian> guardians) {
        guardiansPanel.removeAll();
        for (Guardian guardian : guardians) {
            RoundedPanel card = new RoundedPanel(new BorderLayout(2, 2), 10, COLOR_SURFACE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));

            JLabel name = new JLabel(formatGuardianName(guardian.getFullName()));
            name.setFont(new Font("Segoe UI", Font.BOLD, 13));
            name.setForeground(COLOR_TEXT);

            String relationship = valueOrFallback(guardian.getRelationship(), "Guardian");
            String contact = valueOrFallback(guardian.getContactNumber(), "No contact number");
            String email = valueOrFallback(guardian.getEmail(), "No email");

            JLabel details = new JLabel("<html>" + relationship + "<br>" + contact + "<br>" + email + "</html>");
            details.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            details.setForeground(COLOR_MUTED);

            card.add(name, BorderLayout.NORTH);
            card.add(details, BorderLayout.CENTER);
            guardiansPanel.add(card);
        }
        guardianStatus.setText(guardians.isEmpty() ? "No guardians are linked to this student." : " ");
        guardiansPanel.revalidate();
        guardiansPanel.repaint();
    }

    private void showReports(List<StudentViolationReport> reports) {
        displayedReports = List.copyOf(reports);
        reportTableModel.setRowCount(0);
        for (StudentViolationReport report : reports) {
            reportTableModel.addRow(new Object[] {
                    "#" + report.caseId(),
                    DATE_FORMAT.format(report.incidentDate()),
                    report.violationName(),
                    DATE_TIME_FORMAT.format(report.submittedAt()),
                    "View"
            });
        }
        reportStatus.setText(reports.isEmpty()
                ? "No violation reports for this student."
                : "Select an open incident to add a separate first-hand witness account.");
    }

    private void updateWitnessReportButton() {
        int selectedRow = reportTable.getSelectedRow();
        boolean selectedCaseCanAcceptReport = selectedRow >= 0
                && displayedReports.size() > reportTable.convertRowIndexToModel(selectedRow)
                && reportableCaseIds.contains(
                        displayedReports.get(reportTable.convertRowIndexToModel(selectedRow)).caseId());
        witnessReportButton.setEnabled(selectedCaseCanAcceptReport);
    }

    private void openWitnessReport() {
        int selectedRow = reportTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        int modelRow = reportTable.convertRowIndexToModel(selectedRow);
        if (modelRow >= displayedReports.size()) {
            return;
        }
        StudentViolationReport selectedReport = displayedReports.get(modelRow);
        if (!reportableCaseIds.contains(selectedReport.caseId())) {
            return;
        }
        new ViolationReportDialog(
                SwingUtilities.getWindowAncestor(this), student, user, selectedReport, this::loadDetails)
                .setVisible(true);
    }

    private static String valueOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String removeLeadingLabel(String value, String label) {
        if (value == null || value.isBlank()) {
            return "—";
        }
        String trimmedValue = value.trim();
        if (trimmedValue.regionMatches(true, 0, label, 0, label.length())
                && trimmedValue.length() > label.length()
                && Character.isWhitespace(trimmedValue.charAt(label.length()))) {
            return trimmedValue.substring(label.length()).trim();
        }
        return trimmedValue;
    }

    private static String formatGuardianName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "Guardian";
        }
        String[] nameParts = fullName.trim().split("\\s+");
        if (nameParts.length < 3) {
            return fullName;
        }
        return nameParts[0] + " " + nameParts[1].substring(0, 1).toUpperCase(Locale.ROOT)
                + ". " + String.join(" ", java.util.Arrays.copyOfRange(nameParts, 2, nameParts.length));
    }

    private static String fullName(Student student) {
        String middleName = student.getMiddleName();
        return student.getFirstName()
                + (middleName == null || middleName.isBlank()
                        ? ""
                        : " " + middleName.trim().substring(0, 1).toUpperCase(Locale.ROOT) + ".")
                + " " + student.getLastName();
    }

    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;

        public RoundedPanel(java.awt.LayoutManager layout, int radius, Color bgColor) {
            super(layout);
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            if (cornerRadius > 0) {
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            } else {
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.dispose();
        }
    }

    private record DetailsData(
            Optional<StudentPlacement> placement,
            List<Guardian> guardians,
            List<StudentViolationReport> reports,
            List<IncidentCaseSummary> reportableCases) {
    }
}