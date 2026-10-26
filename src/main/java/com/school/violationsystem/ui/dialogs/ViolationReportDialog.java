package com.school.violationsystem.ui.dialogs;

import com.school.violationsystem.model.Student;
import com.school.violationsystem.model.HandbookSection;
import com.school.violationsystem.model.StudentViolationReport;
import com.school.violationsystem.model.User;
import com.school.violationsystem.model.ViolationCategory;
import com.school.violationsystem.model.ViolationType;
import com.school.violationsystem.service.ViolationService;
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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class ViolationReportDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(ViolationReportDialog.class.getName());
    private static final Color COLOR_PRIMARY = Color.decode("#282385");
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC");
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_TEXT = Color.decode("#141238");
    private static final Color COLOR_MUTED = Color.decode("#5A587A");
    private static final Color COLOR_BORDER = Color.decode("#E2E5EE");
    private static final Color COLOR_BUTTON_GREY = Color.decode("#E4E6F0");

    private final Student student;
    private final User user;
    private final Runnable onSubmitted;
    private final Integer existingCaseId;
    private final String witnessContext;
    private final ViolationService violationService = new ViolationService();
    private final JComboBox<ViolationCategory> categoryBox = new JComboBox<>();
    private final JComboBox<HandbookSection> handbookSectionBox = new JComboBox<>();
    private JLabel categoryLabel;
    private JLabel handbookSectionLabel;
    private JLabel violationTypeLabel;
    private JLabel incidentDateLabel;
    private JLabel incidentTimeLabel;
    private JLabel locationLabel;
    private JLabel descriptionLabel;
    private final JComboBox<ViolationType> violationTypeBox = new JComboBox<>();
    private final JSpinner incidentDateSpinner = new JSpinner(new SpinnerDateModel());
    private final JCheckBox incidentTimeKnown = new JCheckBox("Known");
    private final JSpinner incidentTimeSpinner = new JSpinner(new SpinnerDateModel());
    private final JPanel incidentTimePanel = new JPanel(
            new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
    private final JTextField locationField = new JTextField(24);
    private final JTextArea descriptionArea = new JTextArea(7, 24);
    private final JLabel statusLabel = new JLabel("Loading violation types...");
    private final JLabel workflowHelpLabel = new JLabel();
    private final JButton submitButton = createRoundedButton("Submit Report", COLOR_PRIMARY, COLOR_WHITE, true);
    private SwingWorker<ReportFormData, Void> typeWorker;
    private SwingWorker<Void, Void> submitWorker;
    private boolean violationTypesLoaded;
    private boolean updatingCatalog;
    private List<ViolationType> activeViolationTypes = List.of();

    public ViolationReportDialog() {
        super((Window) null, "Report a Violation", ModalityType.APPLICATION_MODAL);
        student = null;
        user = null;
        onSubmitted = () -> { };
        existingCaseId = null;
        witnessContext = "";
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 360);
        setLocationRelativeTo(null);
        JLabel message = new JLabel(
                "Select a student from View Students to create a violation report.",
                SwingConstants.CENTER);
        message.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        add(message);
    }

    public ViolationReportDialog(Window owner, Student student, User user, Runnable onSubmitted) {
        this(owner, student, user, null, "", onSubmitted);
    }

    public ViolationReportDialog(
            Window owner, Student student, User user, StudentViolationReport witnessReport, Runnable onSubmitted) {
        this(owner, student, user, witnessReport.caseId(),
                witnessReport.violationName() + " | " + witnessReport.incidentDate(), onSubmitted);
    }

    private ViolationReportDialog(
            Window owner,
            Student student,
            User user,
            Integer existingCaseId,
            String witnessContext,
            Runnable onSubmitted) {
        super(owner, "Report a Violation", ModalityType.APPLICATION_MODAL);
        this.student = student;
        this.user = user;
        this.onSubmitted = onSubmitted;
        this.existingCaseId = existingCaseId;
        this.witnessContext = witnessContext;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(620, 600));
        setSize(720, 700);
        setLocationRelativeTo(owner);

        RoundedPanel root = new RoundedPanel(new BorderLayout(16, 16), 0, COLOR_SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header Section
        JLabel studentLabel = new JLabel("Student: " + fullName(student));
        studentLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        studentLabel.setForeground(COLOR_PRIMARY);
        workflowHelpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        workflowHelpLabel.setForeground(COLOR_MUTED);
        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.setOpaque(false);
        header.add(studentLabel, BorderLayout.NORTH);
        header.add(workflowHelpLabel, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // Form Container Card
        RoundedPanel formCard = new RoundedPanel(new GridBagLayout(), 16, COLOR_WHITE);
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 8, 10, 8);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        styleTextField(locationField);
        styleComboBox(violationTypeBox);
        styleComboBox(categoryBox);
        styleComboBox(handbookSectionBox);
        styleSpinner(incidentDateSpinner);
        styleSpinner(incidentTimeSpinner);

        categoryLabel = addFormRow(formCard, constraints, 0, "Severity category", categoryBox);
        handbookSectionLabel = addFormRow(formCard, constraints, 1, "Handbook section", handbookSectionBox);
        violationTypeLabel = addFormRow(formCard, constraints, 2, "Specific violation", violationTypeBox);
        incidentDateSpinner.setEditor(new JSpinner.DateEditor(incidentDateSpinner, "yyyy-MM-dd"));
        incidentDateLabel = addFormRow(formCard, constraints, 3, "Incident date", incidentDateSpinner);
        incidentTimeSpinner.setEditor(new JSpinner.DateEditor(incidentTimeSpinner, "HH:mm"));
        incidentTimePanel.setOpaque(false);
        incidentTimeKnown.setOpaque(false);
        incidentTimeKnown.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        incidentTimeKnown.addActionListener(event ->
                incidentTimeSpinner.setEnabled(incidentTimeKnown.isSelected()));
        incidentTimeSpinner.setEnabled(false);
        incidentTimePanel.add(incidentTimeKnown);
        incidentTimePanel.add(incidentTimeSpinner);
        incidentTimeLabel = addFormRow(formCard, constraints, 4, "Incident time (optional)", incidentTimePanel);
        locationLabel = addFormRow(formCard, constraints, 5, "Location (optional)", locationField);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setForeground(COLOR_TEXT);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.weightx = 0;
        constraints.weighty = 0;
        descriptionLabel = label("Description");
        formCard.add(descriptionLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        formCard.add(descriptionScrollPane, constraints);

        root.add(formCard, BorderLayout.CENTER);
        categoryBox.addActionListener(event -> updateHandbookSections());
        handbookSectionBox.addActionListener(event -> updateViolationTypes());
        updateReportMode();

        // Footer Actions
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(COLOR_MUTED);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JButton cancelButton = createRoundedButton("Cancel", COLOR_BUTTON_GREY, COLOR_TEXT, false);
        cancelButton.addActionListener(event -> dispose());

        submitButton.setEnabled(false);
        submitButton.addActionListener(event -> submit());

        JPanel buttons = new JPanel(new BorderLayout(12, 0));
        buttons.setOpaque(false);
        buttons.add(statusLabel, BorderLayout.CENTER);

        JPanel buttonGroup = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        buttonGroup.setOpaque(false);
        buttonGroup.add(cancelButton);
        buttonGroup.add(submitButton);
        buttons.add(buttonGroup, BorderLayout.EAST);

        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        loadViolationTypes();
    }

    private JLabel addFormRow(
            JPanel form, GridBagConstraints constraints, int row, String labelText, java.awt.Component field) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel rowLabel = label(labelText);
        form.add(rowLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        form.add(field, constraints);
        return rowLabel;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(COLOR_TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setForeground(COLOR_TEXT);
        box.setBackground(COLOR_WHITE);
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));
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
                if (!isEnabled()) {
                    g2.setColor(new Color(220, 220, 225));
                } else if (getModel().isPressed()) {
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

    private void loadViolationTypes() {
        if (existingCaseId != null) {
            violationTypesLoaded = true;
            submitButton.setEnabled(true);
            statusLabel.setText(" ");
            return;
        }
        typeWorker = new SwingWorker<>() {
            @Override
            protected ReportFormData doInBackground() throws Exception {
                return new ReportFormData(violationService.getActiveViolationTypes());
            }

            @Override
            protected void done() {
                try {
                    ReportFormData data = get();
                    activeViolationTypes = data.types();
                    populateSeverityCategories();
                    violationTypeBox.setRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(
                                javax.swing.JList<?> list, Object value, int index,
                                boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof ViolationType type) {
                                setText(type.getViolationName());
                            }
                            return this;
                        }
                    });
                    violationTypesLoaded = true;
                    statusLabel.setText(data.types().isEmpty() ? "No active violation types are available." : " ");
                    submitButton.setEnabled(!data.types().isEmpty());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showLoadError("Loading violation types was interrupted.", e);
                } catch (ExecutionException e) {
                    showLoadError("Could not load violation types.", e.getCause());
                }
            }
        };
        typeWorker.execute();
    }

    private void showLoadError(String message, Throwable error) {
        LOGGER.log(Level.SEVERE, message, error);
        statusLabel.setText(message);
    }

    private void submit() {
        if (!violationTypesLoaded || submitWorker != null) {
            return;
        }

        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
            statusLabel.setText("Enter a description of the incident.");
            descriptionArea.requestFocusInWindow();
            return;
        }

        boolean existingIncident = existingCaseId != null;
        ViolationType selectedType = (ViolationType) violationTypeBox.getSelectedItem();
        ViolationCategory selectedCategory = (ViolationCategory) categoryBox.getSelectedItem();
        HandbookSection selectedSection = (HandbookSection) handbookSectionBox.getSelectedItem();
        LocalDate incidentDate = null;
        LocalTime incidentTime = null;
        if (!existingIncident) {
            if (selectedType == null || selectedCategory == null || selectedSection == null
                    || selectedType.getSeverityCategoryId() != selectedCategory.id()
                    || selectedType.getHandbookSectionId() != selectedSection.id()) {
                statusLabel.setText("Select a severity category, handbook section, and specific violation.");
                return;
            }
            try {
                Date selectedDate = (Date) incidentDateSpinner.getValue();
                incidentDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (incidentTimeKnown.isSelected()) {
                    Date selectedTime = (Date) incidentTimeSpinner.getValue();
                    incidentTime = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                }
            } catch (RuntimeException e) {
                statusLabel.setText("Enter a valid incident date and time.");
                return;
            }
        }

        int violationTypeId = selectedType == null ? 0 : selectedType.getViolationTypeId();
        int caseId = existingCaseId == null ? 0 : existingCaseId;
        LocalDate reportDate = incidentDate;
        LocalTime reportTime = incidentTime;
        String location = locationField.getText();
        submitButton.setEnabled(false);
        statusLabel.setText("Submitting report...");

        submitWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (existingIncident) {
                    violationService.submitAdditionalReport(
                            student.getStudentId(), caseId, user, description);
                } else {
                    violationService.submitReport(
                            student.getStudentId(), violationTypeId, user,
                            reportDate, reportTime, location, description);
                }
                return null;
            }

            @Override
            protected void done() {
                submitWorker = null;
                try {
                    get();
                    onSubmitted.run();
                    dispose();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, "Submitting violation report was interrupted.", e);
                    submitButton.setEnabled(true);
                    statusLabel.setText("Submitting the report was interrupted.");
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    LOGGER.log(Level.SEVERE, "Could not submit violation report.", cause);
                    submitButton.setEnabled(true);
                    JOptionPane.showMessageDialog(
                            ViolationReportDialog.this,
                            cause.getMessage() == null ? "Could not submit the report." : cause.getMessage(),
                            "Report Submission Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        submitWorker.execute();
    }

    private void updateReportMode() {
        boolean witnessReport = existingCaseId != null;
        categoryLabel.setVisible(!witnessReport);
        handbookSectionLabel.setVisible(!witnessReport);
        violationTypeLabel.setVisible(!witnessReport);
        incidentDateLabel.setVisible(!witnessReport);
        incidentTimeLabel.setVisible(!witnessReport);
        incidentTimePanel.setVisible(!witnessReport);
        locationLabel.setVisible(!witnessReport);
        descriptionLabel.setText(witnessReport ? "Witness account" : "Description");
        categoryBox.setVisible(!witnessReport);
        handbookSectionBox.setVisible(!witnessReport);
        violationTypeBox.setVisible(!witnessReport);
        incidentDateSpinner.setVisible(!witnessReport);
        incidentTimeKnown.setVisible(!witnessReport);
        incidentTimeSpinner.setVisible(!witnessReport && incidentTimeKnown.isSelected());
        locationField.setVisible(!witnessReport);
        workflowHelpLabel.setText(witnessReport
                ? "<html>Add your first-hand witness account to Case #" + existingCaseId + ": "
                        + escapeHtml(witnessContext) + ". This does not confirm the allegation.</html>"
                : "<html>Create a new case only for a distinct incident. Choose its severity category, handbook "
                        + "section, and specific violation. Submission creates a pending case and your report; "
                        + "it is an allegation, not a finding.</html>");
        workflowHelpLabel.setToolTipText(witnessReport ? witnessContext : null);
        if (witnessReport) {
            setTitle("Add Witness Report");
            submitButton.setText("Submit Witness Report");
        }
    }

    private void populateSeverityCategories() {
        updatingCatalog = true;
        categoryBox.removeAllItems();
        Map<Integer, ViolationCategory> categories = new LinkedHashMap<>();
        activeViolationTypes.stream()
                .sorted(Comparator.comparing(ViolationType::getSeverityCategoryName)
                        .thenComparing(ViolationType::getHandbookSectionName)
                        .thenComparing(ViolationType::getViolationName))
                .forEach(type -> categories.putIfAbsent(
                        type.getSeverityCategoryId(),
                        new ViolationCategory(type.getSeverityCategoryId(), type.getSeverityCategoryName())));
        categories.values().forEach(categoryBox::addItem);
        updatingCatalog = false;
        updateHandbookSections();
    }

    private void updateHandbookSections() {
        if (updatingCatalog) {
            return;
        }
        ViolationCategory selectedCategory = (ViolationCategory) categoryBox.getSelectedItem();
        updatingCatalog = true;
        handbookSectionBox.removeAllItems();
        if (selectedCategory != null) {
            Map<Integer, HandbookSection> sections = new LinkedHashMap<>();
            activeViolationTypes.stream()
                    .filter(type -> type.getSeverityCategoryId() == selectedCategory.id())
                    .sorted(Comparator.comparing(ViolationType::getHandbookSectionName)
                            .thenComparing(ViolationType::getViolationName))
                    .forEach(type -> sections.putIfAbsent(
                            type.getHandbookSectionId(),
                            new HandbookSection(type.getHandbookSectionId(), type.getHandbookSectionName())));
            sections.values().forEach(handbookSectionBox::addItem);
        }
        updatingCatalog = false;
        updateViolationTypes();
    }

    private void updateViolationTypes() {
        if (updatingCatalog) {
            return;
        }
        ViolationCategory selectedCategory = (ViolationCategory) categoryBox.getSelectedItem();
        HandbookSection selectedSection = (HandbookSection) handbookSectionBox.getSelectedItem();
        updatingCatalog = true;
        violationTypeBox.removeAllItems();
        if (selectedCategory != null && selectedSection != null) {
            activeViolationTypes.stream()
                    .filter(type -> type.getSeverityCategoryId() == selectedCategory.id())
                    .filter(type -> type.getHandbookSectionId() == selectedSection.id())
                    .sorted(Comparator.comparing(ViolationType::getViolationName))
                    .forEach(violationTypeBox::addItem);
        }
        updatingCatalog = false;
    }

    private static String fullName(Student student) {
        String middleName = student.getMiddleName();
        return student.getFirstName()
                + (middleName == null || middleName.isBlank()
                        ? ""
                        : " " + middleName.trim().substring(0, 1).toUpperCase(Locale.ROOT) + ".")
                + " " + student.getLastName();
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private record ReportFormData(List<ViolationType> types) {
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
}