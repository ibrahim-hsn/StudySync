import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TimeTablePanel extends JPanel {
    private TimeTable timeTable;
    private Settings settings;

    private JTable gridTable;
    private DefaultTableModel gridModel;

    private JTable customTable;
    private DefaultTableModel customModel;

    private JComboBox<String> lectureCombo;
    private JComboBox<String> dayCombo;
    private JComboBox<String> periodCombo;
    private JCheckBox customLectureCheck;
    private JTextField customTimeField;

    private JButton addButton, editButton, deleteButton;

    private String selectedLectureId = null;

    public TimeTablePanel(TimeTable timeTable, Settings settings) {
        this.timeTable = timeTable;
        this.settings = settings;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // ----- Input form -----
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        lectureCombo = new JComboBox<>();
        refreshLectureNames();

        dayCombo = new JComboBox<>(timeTable.getDays().toArray(new String[0]));
        periodCombo = new JComboBox<>(timeTable.getPeriods().toArray(new String[0]));
        // Do not call refreshPeriods here!

        customLectureCheck = new JCheckBox("Custom Time");
        customTimeField = new JTextField(10);
        customTimeField.setEnabled(false);

        customLectureCheck.addActionListener(e -> {
            boolean custom = customLectureCheck.isSelected();
            periodCombo.setEnabled(!custom);
            customTimeField.setEnabled(custom);
        });

        formPanel.add(new JLabel("Lecture:"));
        formPanel.add(lectureCombo);
        formPanel.add(new JLabel("Day:"));
        formPanel.add(dayCombo);
        formPanel.add(new JLabel("Period:"));
        formPanel.add(periodCombo);
        formPanel.add(customLectureCheck);
        formPanel.add(new JLabel("Custom:"));
        formPanel.add(customTimeField);

        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        formPanel.add(addButton);
        formPanel.add(editButton);
        formPanel.add(deleteButton);

        // ----- Timetable grid -----
        List<String> days = timeTable.getDays();
        List<String> periods = timeTable.getPeriods();

        String[] gridColNames = new String[days.size()+1];
        gridColNames[0] = "Period";
        for (int i = 0; i < days.size(); i++) gridColNames[i+1] = days.get(i);

        gridModel = new DefaultTableModel(gridColNames, periods.size()) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        gridTable = new JTable(gridModel);
        gridTable.setRowSelectionAllowed(false);
        gridTable.setPreferredScrollableViewportSize(new Dimension(900, 200));

        // Set row height for better visibility
        gridTable.setRowHeight(40);

        // Set columns wider and headings bold
        Font headerFont = gridTable.getTableHeader().getFont().deriveFont(Font.BOLD, 16f);
        gridTable.getTableHeader().setFont(headerFont);
        for (int c = 0; c < gridTable.getColumnCount(); c++) {
            gridTable.getColumnModel().getColumn(c).setPreferredWidth(130);
        }

        // Center headings and cells, wrap period column left
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 1; c < gridTable.getColumnCount(); c++) {
            gridTable.getColumnModel().getColumn(c).setCellRenderer(centerRenderer);
        }
        // Period column left aligned, bold
        DefaultTableCellRenderer periodRenderer = new DefaultTableCellRenderer();
        periodRenderer.setFont(gridTable.getTableHeader().getFont());
        periodRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        gridTable.getColumnModel().getColumn(0).setCellRenderer(periodRenderer);

        // ----- Custom lectures table -----
        String[] customCols = {"Lecture", "Day", "Custom Time", "ID"};
        customModel = new DefaultTableModel(customCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        customTable = new JTable(customModel);
        customTable.removeColumn(customTable.getColumnModel().getColumn(3)); // hide the ID
        customTable.setPreferredScrollableViewportSize(new Dimension(900, 60));
        customTable.setRowHeight(30);
        Font customHeaderFont = customTable.getTableHeader().getFont().deriveFont(Font.BOLD, 15f);
        customTable.getTableHeader().setFont(customHeaderFont);

        // Center cells in custom table
        DefaultTableCellRenderer customCenterRenderer = new DefaultTableCellRenderer();
        customCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 0; c < customTable.getColumnCount(); c++) {
            customTable.getColumnModel().getColumn(c).setCellRenderer(customCenterRenderer);
        }

        // ----- Table selection (Edit/Delete) -----
        gridTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = gridTable.rowAtPoint(e.getPoint());
                int col = gridTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col > 0) { // Ignore Period label column
                    String day = days.get(col-1);
                    Lecture lec = findLectureByDayPeriod(day, row+1);
                    if (lec != null) {
                        selectedLectureId = lec.getId();
                        fillFormFromLecture(lec);
                    }
                }
            }
        });
        customTable.getSelectionModel().addListSelectionListener(e -> {
            int row = customTable.getSelectedRow();
            if (row >= 0 && row < customModel.getRowCount()) {
                selectedLectureId = (String) customModel.getValueAt(row, 3);
                Lecture lec = findLectureById(selectedLectureId);
                if (lec != null) fillFormFromLecture(lec);
            }
        });

        // ----- Button listeners -----
        addButton.addActionListener(e -> onAddLecture());
        editButton.addActionListener(e -> onEditLecture());
        deleteButton.addActionListener(e -> onDeleteLecture());

        // ----- Layout -----
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);

        JScrollPane gridScroll = new JScrollPane(gridTable);
        gridScroll.setBorder(BorderFactory.createTitledBorder("Timetable"));

        JScrollPane customScroll = new JScrollPane(customTable);
        customScroll.setBorder(BorderFactory.createTitledBorder("Custom Lectures"));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(gridScroll);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(customScroll);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        refreshGrid();
        refreshCustomTable();
    }

    /** Call this when lecture names are changed in settings */
    public void refreshLectureNames() {
        lectureCombo.removeAllItems();
        for (String name : settings.getLectureNames()) {
            lectureCombo.addItem(name);
        }
    }

    /** Call this when periods or timing settings are changed */
    public void refreshPeriods() {
        timeTable.generatePeriods();
        periodCombo.setModel(new DefaultComboBoxModel<>(timeTable.getPeriods().toArray(new String[0])));
        // Re-create grid model with new periods/column structure
        List<String> days = timeTable.getDays();
        List<String> periods = timeTable.getPeriods();
        String[] gridColNames = new String[days.size() + 1];
        gridColNames[0] = "Period";
        for (int i = 0; i < days.size(); i++) gridColNames[i + 1] = days.get(i);
        gridModel.setColumnIdentifiers(gridColNames);
        gridModel.setRowCount(periods.size());
        refreshGrid();
    }

    private void onAddLecture() {
        String name = (String) lectureCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        boolean isCustom = customLectureCheck.isSelected();
        Integer period = null;
        String customTime = null;

        if (name == null || name.trim().isEmpty()) {
            showMsg("Lecture name required.");
            return;
        }

        if (isCustom) {
            customTime = customTimeField.getText().trim();
            if (customTime.isEmpty()) {
                showMsg("Custom time required for custom lecture.");
                return;
            }
        } else {
            period = periodCombo.getSelectedIndex() + 1;
            if (period < 1) {
                showMsg("Select a period.");
                return;
            }
            // prevent duplicate in grid
            if (findLectureByDayPeriod(day, period) != null) {
                showMsg("A lecture already exists in this slot.");
                return;
            }
        }

        timeTable.addLecture(name, day, period, customTime, isCustom);
        refreshGrid();
        refreshCustomTable();
        clearForm();
    }

    private void onEditLecture() {
        if (selectedLectureId == null) {
            showMsg("Select a lecture to edit.");
            return;
        }

        String name = (String) lectureCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        boolean isCustom = customLectureCheck.isSelected();
        Integer period = null;
        String customTime = null;

        if (name == null || name.trim().isEmpty()) {
            showMsg("Lecture name required.");
            return;
        }

        if (isCustom) {
            customTime = customTimeField.getText().trim();
            if (customTime.isEmpty()) {
                showMsg("Custom time required for custom lecture.");
                return;
            }
        } else {
            period = periodCombo.getSelectedIndex() + 1;
            if (period < 1) {
                showMsg("Select a period.");
                return;
            }
            // prevent duplicate in grid (except itself)
            Lecture existing = findLectureByDayPeriod(day, period);
            if (existing != null && !existing.getId().equals(selectedLectureId)) {
                showMsg("A lecture already exists in this slot.");
                return;
            }
        }

        boolean ok = timeTable.editLecture(selectedLectureId, name, day, period, customTime, isCustom);
        if (!ok) {
            showMsg("Could not edit lecture.");
        }
        refreshGrid();
        refreshCustomTable();
        clearForm();
    }

    private void onDeleteLecture() {
        if (selectedLectureId == null) {
            showMsg("Select a lecture to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected lecture?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            timeTable.deleteLecture(selectedLectureId);
            refreshGrid();
            refreshCustomTable();
            clearForm();
        }
    }

    private void refreshGrid() {
        // Set period names
        List<String> days = timeTable.getDays();
        List<String> periods = timeTable.getPeriods();
        for (int r = 0; r < periods.size(); r++) {
            gridModel.setValueAt(periods.get(r), r, 0);
            for (int c = 1; c <= days.size(); c++) {
                Lecture lec = findLectureByDayPeriod(days.get(c-1), r+1);
                if (lec != null) {
                    gridModel.setValueAt(lec.getName(), r, c);
                } else {
                    gridModel.setValueAt("", r, c);
                }
            }
        }
    }

    private void refreshCustomTable() {
        customModel.setRowCount(0);
        List<Lecture> lectures = timeTable.getAllLectures();
        for (Lecture lec : lectures) {
            if (lec.isCustom()) {
                customModel.addRow(new Object[]{
                        lec.getName(),
                        lec.getDay(),
                        lec.getCustomTime(),
                        lec.getId()
                });
            }
        }
    }

    private void clearForm() {
        lectureCombo.setSelectedIndex(lectureCombo.getItemCount() > 0 ? 0 : -1);
        dayCombo.setSelectedIndex(0);
        periodCombo.setSelectedIndex(0);
        customLectureCheck.setSelected(false);
        customTimeField.setText("");
        customTimeField.setEnabled(false);
        periodCombo.setEnabled(true);
        gridTable.clearSelection();
        customTable.clearSelection();
        selectedLectureId = null;
    }

    private void fillFormFromLecture(Lecture lec) {
        lectureCombo.setSelectedItem(lec.getName());
        dayCombo.setSelectedItem(lec.getDay());
        customLectureCheck.setSelected(lec.isCustom());
        if (lec.isCustom()) {
            periodCombo.setEnabled(false);
            customTimeField.setEnabled(true);
            customTimeField.setText(lec.getCustomTime() == null ? "" : lec.getCustomTime());
        } else {
            periodCombo.setEnabled(true);
            customTimeField.setEnabled(false);
            customTimeField.setText("");
            if (lec.getPeriod() != null && lec.getPeriod() > 0 && lec.getPeriod() <= periodCombo.getItemCount()) {
                periodCombo.setSelectedIndex(lec.getPeriod() - 1);
            }
        }
    }

    // Helper: find a lecture scheduled for a given day/period
    private Lecture findLectureByDayPeriod(String day, int period) {
        List<Lecture> lectures = timeTable.getAllLectures();
        for (Lecture lec : lectures) {
            if (!lec.isCustom() && lec.getDay().equals(day) && lec.getPeriod() != null && lec.getPeriod() == period) {
                return lec;
            }
        }
        return null;
    }

    private Lecture findLectureById(String id) {
        List<Lecture> lectures = timeTable.getAllLectures();
        for (Lecture lec : lectures) {
            if (lec.getId().equals(id)) return lec;
        }
        return null;
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}