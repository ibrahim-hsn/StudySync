import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * One row in the student timetable.
 */
public class ScheduleEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String day;                // e.g. "Monday"
    private LocalTime startTime;       // e.g. 09:00
    private LocalTime endTime;         // e.g. 10:15
    private String subjectId;          // which subject
    private String room;               // e.g. "Room 201"
    private String description;        // optional

    public ScheduleEntry(
            String day,
            LocalTime startTime,
            LocalTime endTime,
            String subjectId,
            String room,
            String description
    ) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectId = subjectId;
        this.room = room;
        this.description = description;
    }

    public String getDay()         { return day;         }
    public LocalTime getStartTime(){ return startTime;  }
    public LocalTime getEndTime()  { return endTime;    }
    public String getSubjectId()   { return subjectId;  }
    public String getRoom()        { return room;       }
    public String getDescription(){ return description;}

    @Override
    public String toString() {
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        return String.format(
                "%s %s–%s | %s (%s) %s",
                day,
                startTime.format(tf),
                endTime.format(tf),
                subjectId,
                room,
                description == null ? "" : description
        );
    }
}

