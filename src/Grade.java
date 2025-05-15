import java.io.Serializable;

/**
 * Represents a letter or numeric grade entry for a subject.
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;

    private String gradeId;
    private String subjectId;
    private int semesterNumber;
    private double marksObtained;
    private String type; // e.g. "Quiz", "Exam"

    public Grade(
            String gradeId,
            String subjectId,
            int semesterNumber,
            double marksObtained,
            String type
    ) {
        this.gradeId = gradeId;
        this.subjectId = subjectId;
        this.semesterNumber = semesterNumber;
        this.marksObtained = marksObtained;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format(
                "%s [%s] Sem %d: %.2f (%s)",
                gradeId, subjectId, semesterNumber, marksObtained, type
        );
    }
}

