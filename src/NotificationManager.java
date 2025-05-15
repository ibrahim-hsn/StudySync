/* NotificationManager.java */
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    public enum NotificationType {
        EXAM,
        ASSIGNMENT,
        CUSTOM
    }

    public static class Notification {
        public NotificationType type;
        public String subjectId;
        public LocalDate triggerDate;
        public String message;
        public boolean sent = false;

        public Notification(NotificationType type, String subjectId, LocalDate triggerDate, String message) {
            this.type = type;
            this.subjectId = subjectId;
            this.triggerDate = triggerDate;
            this.message = message;
        }
    }

    private List<Notification> notifications = new ArrayList<>();

    /** Schedule a notification on a specific date. */
    public void schedule(NotificationType type, String subjectId, LocalDate triggerDate, String message) {
        notifications.add(new Notification(type, subjectId, triggerDate, message));
    }

    /** Check all pending notifications and print those due today or overdue. */
    public void dispatchDueNotifications() {
        LocalDate today = LocalDate.now();
        for (Notification n : notifications) {
            if (!n.sent && !n.triggerDate.isAfter(today)) {
                System.out.println("[Reminder] " + n.triggerDate + ": " + n.message);
                n.sent = true;
            }
        }
    }

    /** Convenience: notify X days before exam dates. */
    public void notifyBeforeExam(Exam exam, int daysBefore) {
        if (exam.getFinalExamDate() != null) {
            LocalDate d = exam.getFinalExamDate().minus(daysBefore, ChronoUnit.DAYS);
            schedule(NotificationType.EXAM,
                    exam.getSubjectId(),
                    d,
                    "Final exam for " + exam.getSubjectId() + " in " + daysBefore + " days!");
        }
        if (exam.getMidTermDate() != null) {
            LocalDate d = exam.getMidTermDate().minus(daysBefore, ChronoUnit.DAYS);
            schedule(NotificationType.EXAM,
                    exam.getSubjectId(),
                    d,
                    "Mid-term exam for " + exam.getSubjectId() + " in " + daysBefore + " days!");
        }
    }

    /** List all scheduled notifications. */
    public void listAll() {
        for (Notification n : notifications) {
            System.out.printf("%s | %s | %s | %s | sent=%s%n",
                    n.type,
                    n.subjectId,
                    n.triggerDate,
                    n.message,
                    n.sent);
        }
    }
}
