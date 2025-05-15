/* StudyPlanner.java */
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages a list of StudySession objects, with interactive
 * add/remove/list and serialization to study_sessions.ser.
 */
public class StudyPlanner implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<StudySession> sessions = new ArrayList<>();

    /** Load existing sessions from disk, or return a new planner. */
    @SuppressWarnings("unchecked")
    public static StudyPlanner load() {
        File f = new File("study_sessions.ser");
        if (!f.exists()) return new StudyPlanner();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (StudyPlanner) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Could not load study sessions: " + e.getMessage());
            return new StudyPlanner();
        }
    }

    /** Save all sessions back to disk. */
    public void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("study_sessions.ser"))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving study sessions: " + e.getMessage());
        }
    }

    /** Interactive loop: add, remove, list, or done. */
    public void manageSessions(Scanner scanner) {
        System.out.println("\n=== Study Session Planner ===");
        loop:
        while (true) {
            System.out.println("[add, remove, list, done]");
            System.out.print("Choice: ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "add":
                    addSessionInteractive(scanner);
                    break;
                case "remove":
                    removeSessionInteractive(scanner);
                    break;
                case "list":
                    listSessions();  // now public
                    break;
                case "done":
                    break loop;
                default:
                    System.out.println("Invalid option.");
            }
        }
        save();
    }

    private void addSessionInteractive(Scanner scanner) {
        System.out.print("Subject ID: ");
        String subjectId = scanner.nextLine().trim();

        LocalDateTime start;
        while (true) {
            System.out.print("Start (YYYY-MM-DD HH:mm): ");
            String in = scanner.nextLine().trim();
            try {
                start = LocalDateTime.parse(in,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Bad format, try again.");
            }
        }

        System.out.print("Duration in minutes: ");
        int duration = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Notes (optional, blank to skip): ");
        String notes = scanner.nextLine().trim();

        sessions.add(new StudySession(subjectId, start, duration, notes));
        System.out.println("Session added.");
    }

    private void removeSessionInteractive(Scanner scanner) {
        listSessions();
        System.out.print("Enter the index number to remove: ");
        int idx = Integer.parseInt(scanner.nextLine().trim());
        if (idx >= 1 && idx <= sessions.size()) {
            sessions.remove(idx - 1);
            System.out.println("Session removed.");
        } else {
            System.out.println("Invalid index.");
        }
    }

    /**
     * Public so other classes (e.g. Display) can call it.
     */
    public void listSessions() {
        if (sessions.isEmpty()) {
            System.out.println("No study sessions scheduled.");
            return;
        }
        System.out.println("Upcoming Study Sessions:");
        List<StudySession> sorted = sessions.stream()
                .sorted(Comparator.comparing(StudySession::getStart))
                .collect(Collectors.toList());
        int i = 1;
        for (StudySession ss : sorted) {
            System.out.printf("%d) %s%n", i++, ss);
        }
    }

    public List<StudySession> getSessions() {
        return sessions;
    }
}

