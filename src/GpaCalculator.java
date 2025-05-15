import java.util.List;
import java.util.stream.Collectors;

public class GpaCalculator {
    /** Map an overall percentage (0–100) to a 4.0 scale GPA. */
    public double convertPercentageToGpa(double percent) {
        if (percent >= 85) return 4.0;
        if (percent < 50) return 0.0;  // fail
        double steps = Math.ceil((85 - percent) / 2.0);
        double gpa = 4.0 - steps * 0.1;
        return Math.max(gpa, 2.0);
    }

    /** Compute semester GPA (weighted by credit hours). */
    public double calculateSemesterGpa(List<Semester> semesters, int semNum) {
        List<Subject> subjects = semesters.stream()
                .filter(s -> s.getSemesterNumber() == semNum)
                .flatMap(s -> s.getSubjects().stream())
                .collect(Collectors.toList());

        double totalPoints = 0;
        int totalCredits = 0;
        for (Subject sub : subjects) {
            double percent = sub.getMarks().stream()
                    .mapToDouble(Marks::computeContribution)
                    .sum();
            double gradePoint = convertPercentageToGpa(percent);
            int credits = sub.getCreditHours();
            totalPoints += gradePoint * credits;
            totalCredits += credits;
        }
        return totalCredits == 0 ? 0 : totalPoints / totalCredits;
    }

    /**
     * Compute the GPA you must average on the remaining credit hours
     * to reach a target semester GPA.
     *
     * @param semesters       all semesters
     * @param semNum          target semester number
     * @param targetGpa       desired semester GPA
     * @param remainingCredits credit hours not yet graded
     * @return required GPA on remaining credits
     */
    public double projectRequiredGpa(List<Semester> semesters,
                                     int semNum,
                                     double targetGpa,
                                     int remainingCredits) {
        List<Subject> subjects = semesters.stream()
                .filter(s -> s.getSemesterNumber() == semNum)
                .flatMap(s -> s.getSubjects().stream())
                .collect(Collectors.toList());

        double currentPoints = 0;
        int completedCredits = 0;
        for (Subject sub : subjects) {
            double percent = sub.getMarks().stream()
                    .mapToDouble(Marks::computeContribution)
                    .sum();
            double gradePoint = convertPercentageToGpa(percent);
            int credits = sub.getCreditHours();
            currentPoints += gradePoint * credits;
            completedCredits += credits;
        }

        double totalNeededPoints = targetGpa * (completedCredits + remainingCredits);
        double neededPoints = totalNeededPoints - currentPoints;
        return remainingCredits == 0 ? 0 : neededPoints / remainingCredits;
    }
}


