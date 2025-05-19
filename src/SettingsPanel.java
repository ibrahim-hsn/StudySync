import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private Settings settings;
    private JComboBox<Double> durationCombo;
    private JTextField lectureNameField;
    private DefaultListModel<String> lecturesListModel;
    private JList<String> lecturesList;
    private JButton addLectureButton, removeLectureButton;

    private JTextField uniStartField;
    private JTextField uniEndField;

    public SettingsPanel(Settings settings) {
        this.settings = settings;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Top: Default lecture duration
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        durationPanel.add(new JLabel("Default Lecture Duration (hours):"));
        Double[] durations = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0};
        durationCombo = new JComboBox<>(durations);
        durationCombo.setSelectedItem(settings.getDefaultLectureDuration());
        durationCombo.addActionListener(e -> {
            settings.setDefaultLectureDuration((Double)durationCombo.getSelectedItem());
        });
        durationPanel.add(durationCombo);

        // University start/end time fields
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        timePanel.add(new JLabel("University Start Time (HH:mm):"));
        uniStartField = new JTextField(settings.getUniStartTime(), 6);
        timePanel.add(uniStartField);
        timePanel.add(new JLabel("End Time (HH:mm):"));
        uniEndField = new JTextField(settings.getUniEndTime(), 6);
        timePanel.add(uniEndField);

        uniStartField.addActionListener(e -> {
            String val = uniStartField.getText().trim();
            if (val.matches("\\d{2}:\\d{2}")) {
                settings.setUniStartTime(val);
            } else {
                JOptionPane.showMessageDialog(this, "Enter start time in HH:mm format");
                uniStartField.setText(settings.getUniStartTime());
            }
        });

        uniEndField.addActionListener(e -> {
            String val = uniEndField.getText().trim();
            if (val.matches("\\d{2}:\\d{2}")) {
                settings.setUniEndTime(val);
            } else {
                JOptionPane.showMessageDialog(this, "Enter end time in HH:mm format");
                uniEndField.setText(settings.getUniEndTime());
            }
        });

        // Center: Add/remove lectures
        JPanel lecturePanel = new JPanel(new BorderLayout(5, 5));
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lectureNameField = new JTextField(15);
        addLectureButton = new JButton("Add Lecture");
        addPanel.add(new JLabel("Lecture Name:"));
        addPanel.add(lectureNameField);
        addPanel.add(addLectureButton);

        lecturesListModel = new DefaultListModel<>();
        for (String name : settings.getLectureNames()) {
            lecturesListModel.addElement(name);
        }
        lecturesList = new JList<>(lecturesListModel);
        lecturesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(lecturesList);
        listScroll.setPreferredSize(new Dimension(250, 120));

        removeLectureButton = new JButton("Remove Selected");
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removePanel.add(removeLectureButton);

        lecturePanel.add(addPanel, BorderLayout.NORTH);
        lecturePanel.add(listScroll, BorderLayout.CENTER);
        lecturePanel.add(removePanel, BorderLayout.SOUTH);
        lecturePanel.setBorder(BorderFactory.createTitledBorder("Lecture Names"));

        // Add listeners for lectures
        addLectureButton.addActionListener(e -> {
            String name = lectureNameField.getText().trim();
            if (!name.isEmpty() && !lecturesListModel.contains(name)) {
                settings.addLectureName(name);
                lecturesListModel.addElement(name);
                lectureNameField.setText("");
            }
        });

        removeLectureButton.addActionListener(e -> {
            String selected = lecturesList.getSelectedValue();
            if (selected != null) {
                settings.removeLectureName(selected);
                lecturesListModel.removeElement(selected);
            }
        });

        // Add everything to main panel
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        topPanel.add(durationPanel);
        topPanel.add(timePanel);

        add(topPanel, BorderLayout.NORTH);
        add(lecturePanel, BorderLayout.CENTER);
    }
}