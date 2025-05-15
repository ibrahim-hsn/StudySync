import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String subjectId;
    private String name;
    private int creditHours;

    private List<Assignment> assignments = new ArrayList<>();
    private List<Quiz>       quizzes     = new ArrayList<>();
    private List<Marks>      marks       = new ArrayList<>();
    private List<Exam>       exams       = new ArrayList<>();

    public Subject(String subjectId, String name, int creditHours) {
        this.subjectId   = subjectId;
        this.name        = name;
        this.creditHours = creditHours;
    }

    public String getSubjectId()            { return subjectId;   }
    public String getName()                 { return name;        }
    public int    getCreditHours()          { return creditHours; }
    public List<Assignment> getAssignments(){ return assignments; }
    public List<Quiz>       getQuizzes()    { return quizzes;     }
    public List<Marks>      getMarks()      { return marks;       }
    public List<Exam>       getExams()      { return exams;       }

    // ... setters omitted for brevity ...

    public void manageAssignments() { /* as shown earlier, pass `this` into Assignment(...) */ }
    public void manageQuizzes()     { /* as shown earlier */ }
    public void manageMarks()       { /* as shown earlier */ }
    public void manageExams()       { /* use new Exam(subjectId).manageExam() */ }
}
