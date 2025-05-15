import java.io.Serializable;
import java.time.LocalDate;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskId;
    private String description;
    private LocalDate dueDate;
    private boolean completed;

    public Task(String taskId, String description, LocalDate dueDate) {
        this.taskId      = taskId;
        this.description = description;
        this.dueDate     = dueDate;
        this.completed   = false;
    }

    public String getTaskId()       { return taskId;      }
    public String getDescription()  { return description; }
    public LocalDate getDueDate()   { return dueDate;     }
    public boolean isCompleted()    { return completed;   }
    public void markCompleted()     { this.completed = true; }

    @Override
    public String toString() {
        return String.format(
                "%s: %s (Due %s)%s",
                taskId, description, dueDate,
                completed ? " [Done]" : ""
        );
    }
}
