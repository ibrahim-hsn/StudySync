import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String name;
    private List<Semester> semesters;

    public Student(String studentId, String name, List<Semester> semesters) {
        this.studentId = studentId;
        this.name = name;
        this.semesters = semesters;
    }

    public List<Semester> getSemesters() {
        return semesters;
    }
}
