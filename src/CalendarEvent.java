import java.time.LocalDateTime;

public class CalendarEvent {
    private LocalDateTime start;
    private LocalDateTime end;
    private String title;
    private String description;

    public CalendarEvent(
            LocalDateTime start,
            LocalDateTime end,
            String title,
            String description
    ) {
        this.start = start;
        this.end = end;
        this.title = title;
        this.description = description;
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd()   { return end;   }
    public String getTitle()        { return title; }
    public String getDescription()  { return description; }

    @Override
    public String toString() {
        return String.format(
                "%s → %s | %s: %s",
                start, end, title, description
        );
    }
}
