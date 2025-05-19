import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class Main {
    static JPanel dashboardPanel;
    static TimeTablePanel timeTablePanel;
    static SettingsPanel settingsPanel;
    static JPanel mainContentPanel;

    static TimeTable timeTable;
    static Settings settings;
    static final String SETTINGS_FILE = "settings.txt";
    static final String TIMETABLE_FILE = "timetable.txt";

    static JLabel currentPanelLabel;
    static JLabel dateLabel;
    static JLabel timeLabel;
    static JPanel upcomingClassesBox;

    public static void main(String[] args) {
        settings = new Settings();
        settings.loadFromTxt(SETTINGS_FILE);
        timeTable = new TimeTable(settings);
        timeTable.loadFromTxt(TIMETABLE_FILE);
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("StudySync");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(false);

        Color bg = new Color(245, 247, 250);
        Color accent = new Color(66, 133, 244);
        frame.getContentPane().setBackground(bg);
        frame.setLayout(new BorderLayout());

        // Sidebar
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setPreferredSize(new Dimension(170, 0));
        menuPanel.setBackground(Color.WHITE);

        String[] menuItems = {
                "DASHBOARD", "TIME TABLE", "ASSIGNMENTS", "SHELF", "QUIZ",
                "REPORTS", "EXAMS", "MARKS/GPA", "SETTINGS"
        };

        mainContentPanel = new JPanel(new CardLayout());

        settingsPanel = new SettingsPanel(settings);
        timeTablePanel = new TimeTablePanel(timeTable, settings);
        dashboardPanel = createDashboardPanel();

        mainContentPanel.add(dashboardPanel, "dashboard");
        mainContentPanel.add(timeTablePanel, "timetable");
        mainContentPanel.add(settingsPanel, "settings");

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            button.setFocusPainted(false);
            button.setBackground(bg);
            button.setFont(new Font("Segoe UI", Font.BOLD, 16));
            button.setForeground(new Color(80, 80, 80));
            button.setBorder(new EmptyBorder(10, 30, 10, 10));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(accent);
                    button.setForeground(Color.WHITE);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(bg);
                    button.setForeground(new Color(80, 80, 80));
                }
            });

            if (item.equals("DASHBOARD")) {
                button.addActionListener(e -> showPanel("dashboard", "DASHBOARD"));
            } else if (item.equals("TIME TABLE")) {
                button.addActionListener(e -> showPanel("timetable", "TIME TABLE"));
            } else if (item.equals("SETTINGS")) {
                button.addActionListener(e -> showPanel("settings", "SETTINGS"));
            } else {
                button.setEnabled(false);
                button.setForeground(new Color(180, 180, 180));
            }
            menuPanel.add(button);
            menuPanel.add(Box.createVerticalStrut(6));
        }
        frame.add(menuPanel, BorderLayout.WEST);

        // Top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 70));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new CompoundBorder(new LineBorder(new Color(230, 230, 230), 1), new EmptyBorder(0, 0, 0, 0)));

        currentPanelLabel = new JLabel("DASHBOARD", SwingConstants.LEFT);
        currentPanelLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        currentPanelLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        currentPanelLabel.setPreferredSize(new Dimension(220, 70));
        currentPanelLabel.setForeground(accent);
        topPanel.add(currentPanelLabel, BorderLayout.WEST);

        dateLabel = new JLabel(getCurrentDateLabel(), SwingConstants.CENTER);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateLabel.setForeground(new Color(60, 60, 60));
        topPanel.add(dateLabel, BorderLayout.CENTER);

        timeLabel = new JLabel(getCurrentTimeLabel(), SwingConstants.RIGHT);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        timeLabel.setPreferredSize(new Dimension(140, 70));
        timeLabel.setForeground(accent);
        topPanel.add(timeLabel, BorderLayout.EAST);

        // ---- Use javax.swing.Timer explicitly ----
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            dateLabel.setText(getCurrentDateLabel());
            timeLabel.setText(getCurrentTimeLabel());
            updateUpcomingClassesBox();
        });
        timer.start();

        frame.add(topPanel, BorderLayout.NORTH);

        // Right info panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(220, 0));
        rightPanel.setBackground(bg);
        rightPanel.setBorder(new EmptyBorder(12, 8, 12, 8));

        rightPanel.add(createInfoBox("PENDING TASKS", accent));
        rightPanel.add(Box.createVerticalStrut(14));
        rightPanel.add(createInfoBox("UPCOMING EXAMS", accent));
        rightPanel.add(Box.createVerticalStrut(14));
        rightPanel.add(createInfoBox("POSSIBLE QUIZZES", accent));
        rightPanel.add(Box.createVerticalStrut(14));

        // Upcoming Classes section with dynamic content
        upcomingClassesBox = createUpcomingClassesBox(accent);
        rightPanel.add(upcomingClassesBox);

        frame.add(rightPanel, BorderLayout.EAST);

        frame.add(mainContentPanel, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                settings.saveToTxt(SETTINGS_FILE);
                timeTable.saveToTxt(TIMETABLE_FILE);
            }
        });

        frame.setVisible(true);
        updateUpcomingClassesBox();
    }

    private static String getCurrentDateLabel() {
        LocalDateTime now = LocalDateTime.now();
        String dayName = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String monthName = now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return String.format("(%s, %s %d)", dayName, monthName, now.getDayOfMonth());
    }

    private static String getCurrentTimeLabel() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        return String.format("(%d:%02d)", hour, minute);
    }

    private static JPanel createDashboardPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel addTaskBox = new JPanel();
        addTaskBox.setLayout(new BoxLayout(addTaskBox, BoxLayout.Y_AXIS));
        addTaskBox.setBackground(Color.WHITE);
        addTaskBox.setBorder(new CompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true), new EmptyBorder(14, 14, 14, 14)));

        JLabel addTaskLabel = new JLabel("Add Task");
        addTaskLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        addTaskLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addTaskBox.add(addTaskLabel);

        JPanel addTaskFields = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addTaskFields.setOpaque(false);

        JTextField subjectField = new JTextField(10);
        JTextField taskField = new JTextField(10);
        JTextField dueDateField = new JTextField("YYYY-MM-DD", 8);
        JButton addButton = new JButton("Add");
        addButton.setBackground(new Color(66, 133, 244));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(5, 16, 5, 16));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addTaskFields.add(new JLabel("Subject:"));
        addTaskFields.add(subjectField);
        addTaskFields.add(new JLabel("Task:"));
        addTaskFields.add(taskField);
        addTaskFields.add(new JLabel("Due Date:"));
        addTaskFields.add(dueDateField);
        addTaskFields.add(addButton);

        addTaskBox.add(Box.createVerticalStrut(10));
        addTaskBox.add(addTaskFields);

        addButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Task added!", "Notification", JOptionPane.INFORMATION_MESSAGE);
        });

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(addTaskBox);

        JPanel reportsBox = new JPanel(new BorderLayout());
        reportsBox.setBackground(Color.WHITE);
        reportsBox.setBorder(new CompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true), new EmptyBorder(12, 12, 12, 12)));
        reportsBox.setPreferredSize(new Dimension(400, 180));
        JLabel reportsLabel = new JLabel("REPORTS");
        reportsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        reportsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        reportsBox.add(reportsLabel, BorderLayout.NORTH);

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(reportsBox);

        return centerPanel;
    }

    private static void showPanel(String name, String displayName) {
        CardLayout cl = (CardLayout) (mainContentPanel.getLayout());
        if ("timetable".equals(name) && timeTablePanel != null) {
            timeTablePanel.refreshLectureNames();
            timeTablePanel.refreshPeriods();
        }
        cl.show(mainContentPanel, name);
        if (currentPanelLabel != null) {
            currentPanelLabel.setText(displayName);
        }
    }

    private static JPanel createInfoBox(String title, Color accent) {
        JPanel box = new JPanel();
        box.setBackground(Color.WHITE);
        box.setBorder(new CompoundBorder(
                new LineBorder(accent, 2, true),
                new EmptyBorder(18, 10, 18, 10)
        ));
        box.setMaximumSize(new Dimension(220, 64));
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(accent);
        box.setLayout(new GridBagLayout());
        box.add(label);
        return box;
    }

    private static JPanel createUpcomingClassesBox(Color accent) {
        JPanel box = new JPanel();
        box.setBackground(Color.WHITE);
        box.setBorder(new CompoundBorder(
                new LineBorder(accent, 2, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        box.setMaximumSize(new Dimension(220, 110));
        box.setLayout(new BorderLayout());

        JLabel title = new JLabel("UPCOMING CLASSES", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(accent);
        box.add(title, BorderLayout.NORTH);

        JPanel classesPanel = new JPanel();
        classesPanel.setLayout(new BoxLayout(classesPanel, BoxLayout.Y_AXIS));
        classesPanel.setBackground(Color.WHITE);
        classesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < 3; i++) {
            JLabel l = new JLabel(" ");
            l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            l.setForeground(new Color(70, 70, 70));
            classesPanel.add(l);
        }

        box.add(classesPanel, BorderLayout.CENTER);
        return box;
    }

    private static void updateUpcomingClassesBox() {
        if (upcomingClassesBox == null || timeTable == null) return;
        JPanel classesPanel = (JPanel) ((BorderLayout) upcomingClassesBox.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (classesPanel == null) return;

        // Only today's classes (including ongoing)
        List<TimeTable.UpcomingClassInfo> upcoming = timeTable.getNextUpcomingClassesWithOngoingToday(3);

        classesPanel.removeAll();
        if (upcoming.isEmpty()) {
            JLabel l = new JLabel("No upcoming classes.", SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            l.setForeground(new Color(120, 120, 120));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            classesPanel.add(l);
        } else {
            LocalDateTime now = LocalDateTime.now();
            for (int i = 0; i < upcoming.size(); i++) {
                TimeTable.UpcomingClassInfo info = upcoming.get(i);

                boolean isOngoing = !now.isBefore(info.dateTime) && now.isBefore(info.dateTime.plusMinutes(10));
                JLabel l = new JLabel(String.format("%s  %s", info.name, info.time), SwingConstants.LEFT);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                if (i == 0) {
                    // Make the top class green and bold
                    l.setForeground(new Color(0, 153, 51));
                    l.setFont(l.getFont().deriveFont(Font.BOLD));
                } else {
                    l.setForeground(new Color(70, 70, 70));
                }
                l.setAlignmentX(Component.LEFT_ALIGNMENT);
                classesPanel.add(l);
                classesPanel.add(Box.createVerticalStrut(3));
            }
        }
        classesPanel.revalidate();
        classesPanel.repaint();
    }
}