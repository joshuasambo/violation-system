package com.school.violationsystem.ui;

import com.school.violationsystem.model.User;
import com.school.violationsystem.ui.dialogs.ViolationReportDialog;
import com.school.violationsystem.ui.panels.DashboardPanel;
import com.school.violationsystem.ui.panels.ViolationReportsPanel;
import com.school.violationsystem.ui.panels.students.StudentsPanel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DashboardFrame extends JFrame {

    // Color Palette Constants matching LoginFrame
    private static final Color COLOR_PRIMARY = Color.decode("#282385"); // Royal Blue
    private static final Color COLOR_ACCENT = Color.decode("#F5A623");  // Flame Gold
    private static final Color COLOR_SURFACE = Color.decode("#F8F9FC"); // Off-White / Slate
    private static final Color COLOR_TEXT = Color.decode("#141238");    // Deep Navy
    private static final Color COLOR_MUTED = Color.decode("#5A587A");   // Muted Text
    private static final Color COLOR_WHITE = Color.white;

    private final StudentsPanel studentsPanel = new StudentsPanel();
    private JButton activeSidebarButton;

    public DashboardFrame(User user) {
        URL iconUrl = getClass().getResource("/images/school-logo.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }

        Objects.requireNonNull(user, "user");
        setTitle("Violation System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        // Root Layout Container
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(COLOR_SURFACE);

        // --- Top Header Bar ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 72)); // Slightly taller header for the gap
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E2E5EE")),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)
        ));

        // System Title / Branding in Header (Vertically centered)
        JPanel brandWrapper = new JPanel(new GridBagLayout());
        brandWrapper.setBackground(COLOR_WHITE);
        GridBagConstraints gbcBrand = new GridBagConstraints();
        gbcBrand.anchor = GridBagConstraints.WEST;
        gbcBrand.insets = new Insets(12, 0, 0, 0); // Adds top gap to brand title as well if needed
        JLabel brandLabel = new JLabel("Violation Management System");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandLabel.setForeground(COLOR_PRIMARY);
        brandWrapper.add(brandLabel, gbcBrand);
        headerPanel.add(brandWrapper, BorderLayout.WEST);

        // Right side of Header: User Info & Actions with a Top Gap
        JPanel headerRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerRightPanel.setBackground(COLOR_WHITE);
        // Added top padding (12px) to push buttons and user label down from the top edge
        headerRightPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JLabel userLabel = new JLabel("Welcome, " + user.getFullName() + " (" + user.getRoleName() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(COLOR_TEXT);
        headerRightPanel.add(userLabel);

        if ("Administrator".equals(user.getRoleName())) {
            JButton createTeacherButton = new JButton("Create Teacher Account");
            createTeacherButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            createTeacherButton.setForeground(COLOR_PRIMARY);
            createTeacherButton.setBackground(COLOR_SURFACE);
            createTeacherButton.setFocusPainted(false);
            createTeacherButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            createTeacherButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            createTeacherButton.addActionListener(
                event -> new TeacherRegistrationDialog(this, user).setVisible(true));
            headerRightPanel.add(createTeacherButton);
        }

        // Logout Button
        JButton logoutButton = new JButton("Sign Out");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setForeground(COLOR_WHITE);
        logoutButton.setBackground(Color.decode("#D93838")); // Accent red for sign out
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        logoutButton.addActionListener(event -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        headerRightPanel.add(logoutButton);

        headerPanel.add(headerRightPanel, BorderLayout.EAST);
        mainContent.add(headerPanel, BorderLayout.NORTH);

        // --- Sidebar Navigation ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_PRIMARY);
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel navTitle = new JLabel("NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navTitle.setForeground(COLOR_ACCENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        sidebarPanel.add(navTitle);

        CardLayout cardLayout = new CardLayout();
        JPanel contentWorkspace = new JPanel(cardLayout);
        contentWorkspace.setBackground(COLOR_SURFACE);
        contentWorkspace.add(new DashboardPanel(), "dashboard");
        contentWorkspace.add(createRecordViolationPanel(), "record-violation");
        contentWorkspace.add(studentsPanel, "students");
        contentWorkspace.add(new ViolationReportsPanel(), "reports");

        JButton dashboardButton = createSidebarButton("Dashboard Overview");
        dashboardButton.addActionListener(event -> showPage(
                cardLayout, contentWorkspace, dashboardButton, "dashboard"));
        sidebarPanel.add(dashboardButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        JButton recordViolationButton = createSidebarButton("Record Violation");
        recordViolationButton.addActionListener(event -> showPage(
                cardLayout, contentWorkspace, recordViolationButton, "record-violation"));
        sidebarPanel.add(recordViolationButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        JButton studentsButton = createSidebarButton("View Students");
        studentsButton.addActionListener(event -> showPage(
                cardLayout, contentWorkspace, studentsButton, "students"));
        sidebarPanel.add(studentsButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        JButton reportsButton = createSidebarButton("Reports & Logs");
        reportsButton.addActionListener(event -> showPage(
                cardLayout, contentWorkspace, reportsButton, "reports"));
        sidebarPanel.add(reportsButton);

        setActiveSidebarButton(dashboardButton);

        mainContent.add(sidebarPanel, BorderLayout.WEST);
        mainContent.add(contentWorkspace, BorderLayout.CENTER);

        add(mainContent);
    }

    private JPanel createRecordViolationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("Record Violation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(COLOR_TEXT);
        panel.add(title, constraints);

        constraints.gridy = 1;
        constraints.insets = new Insets(8, 0, 16, 0);
        JLabel subtitle = new JLabel("Open the violation report form to record a new incident.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(COLOR_MUTED);
        panel.add(subtitle, constraints);

        constraints.gridy = 2;
        constraints.insets = new Insets(0, 0, 0, 0);
        JButton openReportButton = new JButton("Open Violation Report Form");
        openReportButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        openReportButton.setForeground(COLOR_WHITE);
        openReportButton.setBackground(COLOR_PRIMARY);
        openReportButton.setFocusPainted(false);
        openReportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openReportButton.addActionListener(event -> new ViolationReportDialog().setVisible(true));
        panel.add(openReportButton, constraints);

        return panel;
    }

    private void showPage(CardLayout cardLayout, JPanel contentWorkspace, JButton selectedButton, String page) {
        if ("students".equals(page)) {
            studentsPanel.onShown();
        } else {
            studentsPanel.onHidden();
        }
        cardLayout.show(contentWorkspace, page);
        setActiveSidebarButton(selectedButton);
    }

    private void setActiveSidebarButton(JButton selectedButton) {
        if (activeSidebarButton != null) {
            activeSidebarButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            activeSidebarButton.setForeground(Color.decode("#D0CDEE"));
            activeSidebarButton.setBackground(COLOR_PRIMARY);
        }

        activeSidebarButton = selectedButton;
        activeSidebarButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        activeSidebarButton.setForeground(COLOR_WHITE);
        activeSidebarButton.setBackground(Color.decode("#39339E"));
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(JButton.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(190, 38));
        button.setPreferredSize(new Dimension(190, 38));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(Color.decode("#D0CDEE"));
        button.setBackground(COLOR_PRIMARY);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}