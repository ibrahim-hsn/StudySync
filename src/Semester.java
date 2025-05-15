import java.io.Serializable;
import java.util.List;
import java.util.Scanner;

public class Semester implements Serializable {
    private static final long serialVersionUID = 1L;

    private int semesterNumber;
    private List<Subject> subjects;

    public Semester(int semesterNumber, List<Subject> subjects) {
        this.semesterNumber = semesterNumber;
        this.subjects = subjects;
    }

    public void addSubjectsFromUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Adding Subjects to semester " + semesterNumber + " ===");
        boolean added = false;
        while (true) {
            System.out.print("Enter Subject ID (or 'done'): ");
            String id = scanner.nextLine().trim();
            if (id.equalsIgnoreCase("done")) {
                if (!added) { System.out.println("Add at least one subject."); continue; }
                break;
            }
            if (subjects.stream().anyMatch(s -> s.getSubjectId().equalsIgnoreCase(id))) {
                System.out.println("Duplicate ID."); continue;
            }
            System.out.print("Name: "); String name = scanner.nextLine().trim();
            System.out.print("Credits: "); int ch;
            try { ch = Integer.parseInt(scanner.nextLine().trim()); } catch (NumberFormatException e) {
                System.out.println("Invalid."); continue;
            }
            subjects.add(new Subject(id, name, ch)); added = true;
        }
    }

    public void manageSubjects() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Manage Subjects for semester " + semesterNumber + " ===");
        while (true) {
            System.out.println("Options: [add, remove, list, done]");
            System.out.print("Enter choice: ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "add": addSubjectsFromUserInput(); break;
                case "remove":
                    System.out.print("Enter Subject ID to remove: ");
                    String rid = scanner.nextLine().trim();
                    boolean rem = subjects.removeIf(s -> s.getSubjectId().equalsIgnoreCase(rid));
                    System.out.println(rem ? "Subject removed." : "Subject ID not found.");
                    break;
                case "list":
                    System.out.println("Subjects in semester " + semesterNumber + ":");
                    subjects.forEach(s -> System.out.println(s.getSubjectId() + " - " + s.getName()));
                    break;
                case "done": return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    public int getSemesterNumber() { return semesterNumber; }
    public List<Subject> getSubjects() { return subjects; }
}
