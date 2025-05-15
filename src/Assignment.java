import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Assignment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String assignmentId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean completed;
    private transient Subject subject;
    private String subjectId;

    public Assignment(
            String assignmentId,
            String title,
            String description,
            LocalDate dueDate,
            Subject subject
    ) {
        this.assignmentId = assignmentId;
        this.title        = title;
        this.description  = description;
        this.dueDate      = dueDate;
        this.completed    = false;
        this.subject      = subject;
        this.subjectId    = subject.getSubjectId();
    }

    public String getAssignmentId() { return assignmentId; }
    public String getTitle()        { return title;        }
    public String getDescription()  { return description;  }
    public LocalDate getDueDate()   { return dueDate;      }
    public Subject getSubject()     { return subject;      }
    public String getSubjectId()    { return subjectId;    }

    public void markCompleted()     { this.completed = true; }

    @Override
    public String toString() {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        return String.format(
                "%s: %s (due in %dd)%s",
                assignmentId, title, days,
                completed ? " [Done]" : ""
        );
    }
}
