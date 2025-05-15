import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- 1) Load / init all data lists ---
        List<Semester> semesters = SerializationUtil.load("semester.ser");
        if (semesters == null) semesters = new ArrayList<>();
        List<Assignment> assignments = SerializationUtil.load("assignment.ser");
        if (assignments == null) assignments = new ArrayList<>();
        List<Quiz> quizzes = SerializationUtil.load("quiz.ser");
        if (quizzes == null) quizzes = new ArrayList<>();
        List<Marks> marks = SerializationUtil.load("marks.ser");
        if (marks == null) marks = new ArrayList<>();
        List<Exam> exams = SerializationUtil.load("exam.ser");
        if (exams == null) exams = new ArrayList<>();

        // --- 2) Load managers/models ---
        StudyPlanner studyPlanner = StudyPlanner.load();
        Timetable timetable = Timetable.load();
        TopicManager topicMgr = TopicManager.load();
        List<Task> tasks = SerializationUtil.load("tasks.ser");
        if (tasks == null) tasks = new ArrayList<>();
        List<Grade> grades = SerializationUtil.load("grades.ser");
        if (grades == null) grades = new ArrayList<>();
        List<UserPreferences> prefsList = SerializationUtil.load("prefs.ser");
        UserPreferences prefs = (prefsList != null && !prefsList.isEmpty())
                ? prefsList.get(0)
                : new UserPreferences();

        // --- 3) Helpers & notifications ---
        NotificationManager nm = new NotificationManager();
        ReportGenerator rg = new ReportGenerator();
        PerformanceAnalyzer perf = new PerformanceAnalyzer();
        nm.dispatchDueNotifications();

        // --- 4) Reattach child items to subjects ---
        for (Semester sem : semesters) {
            for (Subject sub : sem.getSubjects()) {
                String sid = sub.getSubjectId();
                assignments.stream()
                        .filter(a -> sid.equalsIgnoreCase(a.getSubjectId()))
                        .forEach(sub.getAssignments()::add);
                marks.stream()
                        .filter(m -> sid.equalsIgnoreCase(m.getSubjectId()))
                        .forEach(sub.getMarks()::add);
                exams.stream()
                        .filter(e -> sid.equalsIgnoreCase(e.getSubjectId()))
                        .forEach(sub.getExams()::add);
                // quizzes are added during manageQuizzes()
            }
        }

        // --- 5) Ensure at least one semester ---
        if (semesters.isEmpty()) {
            System.out.println("No semesters—please add one.");
            while (true) {
                System.out.print("Enter semester number (or 'done'): ");
                String in = scanner.nextLine().trim();
                if (in.equalsIgnoreCase("done")) break;
                try {
                    int num = Integer.parseInt(in);
                    Semester sem = new Semester(num, new ArrayList<>());
                    sem.addSubjectsFromUserInput();
                    semesters.add(sem);
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid number.");
                }
            }
        }

        // --- 6) Manage academic data ---
        for (Semester sem : semesters) {
            sem.manageSubjects();
            for (Subject sub : sem.getSubjects()) {
                sub.manageAssignments();
                sub.manageQuizzes();
                sub.manageMarks();
                sub.manageExams();
                sub.getExams().forEach(e ->
                        nm.notifyBeforeExam(e, prefs.getDefaultReminderDays())
                );
            }
        }

        // --- 7) Study sessions, timetable, topics, tasks, grades, prefs ---
        studyPlanner.manageSessions(scanner);
        timetable.manage(scanner);
        topicMgr.manage(scanner);
        // ... your inline CRUD loops for tasks & grades with date‐validation ...
        System.out.println("\n=== Preferences ===");
        System.out.println(prefs);
        System.out.print("Dark mode? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            prefs.setDarkMode(true);
        }

        // --- 8) Schedule topic‐revision & quiz reminders ---
        for (Topic t : topicMgr.getTopics()) {
            LocalDate next = t.getLastRevised().plusDays(2);
            nm.schedule(
                    NotificationManager.NotificationType.CUSTOM,
                    t.getSubjectId(),
                    next,
                    "Revise topic " + t.getName()
            );
        }
        for (Semester sem : semesters) {
            for (Subject sub : sem.getSubjects()) {
                String sId = sub.getSubjectId();
                for (Quiz q : sub.getQuizzes()) {
                    LocalDate date = q.getQuizDate();
                    if (date != null) {  // ← guard against null
                        LocalDate remindOn = date.minusDays(2);
                        nm.schedule(
                                NotificationManager.NotificationType.CUSTOM,
                                sId,
                                remindOn,
                                "Quiz " + q.getQuizId() + " on " + date
                        );
                    }
                }
            }
        }

        // --- 9) GPA lookup & cumulative ---
        GpaCalculator gpaCalc = new GpaCalculator();
        System.out.println("\n=== GPA Lookup ===");
        while (true) {
            System.out.print("Semester # (or done): ");
            String in = scanner.nextLine().trim();
            if (in.equalsIgnoreCase("done")) break;
            try {
                int s = Integer.parseInt(in);
                System.out.printf("GPA: %.2f%n", gpaCalc.calculateSemesterGpa(semesters, s));
            } catch (NumberFormatException ex) {
                System.out.println("Invalid.");
            }
        }
        double pts = 0; int creds = 0;
        for (Semester sem : semesters) {
            for (Subject sub : sem.getSubjects()) {
                double pct = sub.getMarks().stream()
                        .mapToDouble(Marks::computeContribution).sum();
                double gp = gpaCalc.convertPercentageToGpa(pct);
                pts += gp * sub.getCreditHours();
                creds += sub.getCreditHours();
            }
        }
        System.out.printf("Cumulative GPA: %.2f%n", creds == 0 ? 0 : pts/creds);

        // --- 10) Calendar export ---
        CalendarScheduler cal = new CalendarScheduler();
        List<CalendarEvent> events = cal.generateEvents(semesters, studyPlanner);
        try {
            cal.exportToIcs(events, "student_schedule.ics");
            System.out.println("Calendar exported.");
        } catch (IOException e) {
            System.err.println("Calendar export failed: " + e.getMessage());
        }

        // --- 11) Main menu ---
        Student student = new Student("24L-0019", "Musharraf Ali", semesters);
        Display.showMenu(
                student, nm, rg, studyPlanner, perf,
                timetable, topicMgr, tasks, grades, prefs
        );

        scanner.close();
    }
}

