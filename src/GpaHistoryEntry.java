import java.io.Serializable;
import java.time.LocalDate;

public class GpaHistoryEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int semesterNumber;
    private double gpa;
    private LocalDate date;

    public GpaHistoryEntry(int semesterNumber, double gpa) {
        this.semesterNumber = semesterNumber;
        this.gpa = gpa;
        this.date = LocalDate.now();
    }

    public int getSemesterNumber() { return semesterNumber; }
    public double getGpa()            { return gpa;           }
    public LocalDate getDate()        { return date;          }

    @Override
    public String toString() {
        return String.format("Semester %d: GPA=%.2f on %s", semesterNumber, gpa, date);
    }
}
