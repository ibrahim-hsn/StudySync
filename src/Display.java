import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Display {
    public static void showMenu(
            Student student,
            NotificationManager nm,
            ReportGenerator rg,
            StudyPlanner studyPlanner,
            PerformanceAnalyzer perf,
            Timetable timetable,
            TopicManager topicMgr,
            List<Task> tasks,
            List<Grade> grades,
            UserPreferences prefs
    ) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(
                    "\nView: [semesters, add_semester, subjects, assignments, quizzes, marks, exams, " +
                            "notifications, reports, study, calendar, performance, timetable, topics, tasks, grades, prefs, exit]"
            );
            System.out.print("Choice: ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            switch (cmd) {
                case "semesters":
                    student.getSemesters().forEach(
                            s -> System.out.println("Semester " + s.getSemesterNumber())
                    );
                    break;

                case "add_semester":
                    System.out.print("New semester #: ");
                    String in = scanner.nextLine().trim();
                    try {
                        int num = Integer.parseInt(in);
                        boolean exists = student.getSemesters().stream()
                                .anyMatch(s -> s.getSemesterNumber() == num);
                        if (exists) {
                            System.out.println("Exists.");
                        } else {
                            Semester sem = new Semester(num, new ArrayList<>());
                            sem.addSubjectsFromUserInput();
                            student.getSemesters().add(sem);
                            System.out.println("Added.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Bad number.");
                    }
                    break;

                case "subjects":
                    student.getSemesters().forEach(sem ->
                            sem.getSubjects().forEach(sub ->
                                    System.out.println(sub.getSubjectId() + " - " + sub.getName())
                            )
                    );
                    break;

                case "assignments":
                    student.getSemesters().forEach(sem ->
                            sem.getSubjects().forEach(sub -> {
                                System.out.println("Assignments for " + sub.getSubjectId() + ":");
                                sub.getAssignments().forEach(a -> System.out.println("  " + a));
                            })
                    );
                    break;

                case "quizzes":
                    student.getSemesters().forEach(sem ->
                            sem.getSubjects().forEach(sub -> {
                                System.out.println("Quizzes for " + sub.getSubjectId() + ":");
                                sub.getQuizzes().forEach(q -> System.out.println("  " + q));
                            })
                    );
                    break;

                case "marks":
                    System.out.print("Subject ID: ");
                    String mid = scanner.nextLine().trim();
                    student.getSemesters().stream()
                            .flatMap(s -> s.getSubjects().stream())
                            .filter(sub -> sub.getSubjectId().equalsIgnoreCase(mid))
                            .findFirst()
                            .ifPresentOrElse(
                                    sub -> sub.getMarks().forEach(m -> System.out.println("  " + m)),
                                    ()  -> System.out.println("Not found.")
                            );
                    break;

                case "exams":
                    System.out.print("Subject ID: ");
                    String eid = scanner.nextLine().trim();
                    student.getSemesters().stream()
                            .flatMap(s -> s.getSubjects().stream())
                            .filter(sub -> sub.getSubjectId().equalsIgnoreCase(eid))
                            .findFirst()
                            .ifPresentOrElse(
                                    sub -> sub.getExams().forEach(e -> System.out.println("  " + e)),
                                    ()  -> System.out.println("Not found.")
                            );
                    break;

                case "notifications":
                    nm.listAll();
                    break;

                case "reports":
                    // reuse your reports logic
                    break;

                case "study":
                    studyPlanner.listSessions();
                    break;

                case "calendar":
                    System.out.print("ICS path: ");
                    String path = scanner.nextLine().trim();
                    if (path.isEmpty()) path = "student_schedule.ics";
                    try {
                        CalendarScheduler cs = new CalendarScheduler();
                        cs.exportToIcs(
                                cs.generateEvents(student.getSemesters(), studyPlanner),
                                path
                        );
                        System.out.println("Saved " + path);
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                    break;

                case "performance":
                    perf.reportBySubjectPerformance(student.getSemesters());
                    break;

                case "timetable":
                    timetable.list();
                    break;

                case "topics":
                    topicMgr.list();
                    break;

                case "tasks":
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.printf("%d) %s%n", i+1, tasks.get(i));
                    }
                    break;

                case "grades":
                    grades.forEach(g -> System.out.println("  " + g));
                    break;

                case "prefs":
                    System.out.println(prefs);
                    break;

                case "exit":
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
