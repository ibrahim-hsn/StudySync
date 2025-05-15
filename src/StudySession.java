import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents one planned study session for a given subject.
 */
public class StudySession implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subjectId;       // Which subject to study
    private LocalDateTime start;    // When the session starts
    private int durationMinutes;    // How long, in minutes
    private String notes;           // Optional notes

    public StudySession(String subjectId,
                        LocalDateTime start,
                        int durationMinutes,
                        String notes) {
        this.subjectId = subjectId;
        this.start = start;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }

    public String getSubjectId() {
        return subjectId;
    }
    public LocalDateTime getStart() {
        return start;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        String fmt = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return String.format(
                "%s | %s for %d min%s",
                fmt, subjectId, durationMinutes,
                (notes != null && !notes.isEmpty() ? " – " + notes : "")
        );
    }
}
