import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Holds and manages ScheduleEntry rows.
 */
public class Timetable implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<ScheduleEntry> entries = new ArrayList<>();

    public static Timetable load() {
        List<ScheduleEntry> list = SerializationUtil.load("timetable.ser");
        Timetable tt = new Timetable();
        if (list != null) tt.entries = list;
        return tt;
    }

    public void save() {
        SerializationUtil.save(entries, "timetable.ser");
    }

    public void manage(Scanner scanner) {
        System.out.println("\n=== Timetable Manager ===");
        loop:
        while (true) {
            System.out.println("[add, remove, list, done]");
            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "add":
                    addInteractive(scanner);
                    break;
                case "remove":
                    removeInteractive(scanner);
                    break;
                case "list":
                    list();
                    break;
                case "done":
                    break loop;
                default:
                    System.out.println("Invalid.");
            }
        }
        save();
    }

    private void addInteractive(Scanner sc) {
        try {
            System.out.print("Day (e.g. Monday): ");
            String day = sc.nextLine().trim();
            System.out.print("Start (HH:mm): ");
            String s1 = sc.nextLine().trim();
            System.out.print("End   (HH:mm): ");
            String s2 = sc.nextLine().trim();
            System.out.print("Subject ID: ");
            String sub = sc.nextLine().trim();
            System.out.print("Room: ");
            String room = sc.nextLine().trim();
            System.out.print("Desc (opt): ");
            String desc = sc.nextLine().trim();
            entries.add(new ScheduleEntry(
                    day,
                    java.time.LocalTime.parse(s1),
                    java.time.LocalTime.parse(s2),
                    sub, room,
                    desc.isEmpty() ? null : desc
            ));
            System.out.println("Added.");
        } catch (Exception e) {
            System.out.println("Bad input, aborting add.");
        }
    }

    private void removeInteractive(Scanner sc) {
        list();
        System.out.print("Index to remove: ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx >= 0 && idx < entries.size()) {
                System.out.println("Removed: " + entries.remove(idx));
            } else {
                System.out.println("Invalid index.");
            }
        } catch (Exception e) {
            System.out.println("Invalid.");
        }
    }

    public void list() {
        if (entries.isEmpty()) {
            System.out.println("Timetable is empty.");
            return;
        }
        System.out.println("=== Timetable Entries ===");
        for (int i = 0; i < entries.size(); i++) {
            System.out.printf("%d) %s%n", i+1, entries.get(i));
        }
    }

    public List<ScheduleEntry> getEntries() {
        return entries;
    }
}

