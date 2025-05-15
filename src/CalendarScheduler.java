import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarScheduler {
    private static final DateTimeFormatter ICS_DT_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    /**
     * Generate events for:
     *  - Assignment due dates (1-hour block starting 09:00)
     *  - Exam dates (2-hour block starting 10:00)
     *  - Study sessions (exact start + duration)
     */
    public List<CalendarEvent> generateEvents(
            List<Semester> semesters,
            StudyPlanner studyPlanner
    ) {
        List<CalendarEvent> events = new ArrayList<>();

        // Assignments
        for (Semester sem : semesters) {
            for (Subject sub : sem.getSubjects()) {
                sub.getAssignments().forEach(a -> {
                    // 09:00–10:00 on dueDate
                    var dt = a.getDueDate().atTime(9, 0);
                    events.add(new CalendarEvent(
                            dt,
                            dt.plusHours(1),
                            "Assignment Due: " + a.getAssignmentId(),
                            sub.getSubjectId() + " – " + a.toString()
                    ));
                });
            }
        }

        // Exams
        for (Semester sem : semesters) {
            for (Subject sub : sem.getSubjects()) {
                sub.getExams().forEach(e -> {
                    if (e.getMidTermDate() != null) {
                        var dt = e.getMidTermDate().atTime(10, 0);
                        events.add(new CalendarEvent(
                                dt,
                                dt.plusHours(2),
                                "Midterm Exam: " + sub.getSubjectId(),
                                "Midterm for " + sub.getSubjectId()
                        ));
                    }
                    if (e.getFinalExamDate() != null) {
                        var dt = e.getFinalExamDate().atTime(10, 0);
                        events.add(new CalendarEvent(
                                dt,
                                dt.plusHours(2),
                                "Final Exam: " + sub.getSubjectId(),
                                "Final for " + sub.getSubjectId()
                        ));
                    }
                });
            }
        }

        // Study sessions
        studyPlanner.getSessions().forEach(ss -> {
            events.add(new CalendarEvent(
                    ss.getStart(),
                    ss.getStart().plusMinutes(ss.getDurationMinutes()),
                    "Study: " + ss.getSubjectId(),
                    ss.getNotes() == null ? "" : ss.getNotes()
            ));
        });

        return events;
    }

    /**
     * Write a list of events to an .ics file (RFC-5545).
     */
    public void exportToIcs(List<CalendarEvent> events, String filePath) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filePath))) {
            out.write("BEGIN:VCALENDAR\n");
            out.write("VERSION:2.0\n");
            out.write("PRODID:-//YourApp//StudyPlanner//EN\n");
            for (CalendarEvent ev : events) {
                out.write("BEGIN:VEVENT\n");
                out.write("DTSTAMP:" + ICS_DT_FMT.format(java.time.LocalDateTime.now()) + "\n");
                out.write("DTSTART:" + ICS_DT_FMT.format(ev.getStart()) + "\n");
                out.write("DTEND:"   + ICS_DT_FMT.format(ev.getEnd())   + "\n");
                out.write("SUMMARY:" + ev.getTitle() + "\n");
                out.write("DESCRIPTION:" + ev.getDescription() + "\n");
                out.write("END:VEVENT\n");
            }
            out.write("END:VCALENDAR\n");
        }
    }
}

