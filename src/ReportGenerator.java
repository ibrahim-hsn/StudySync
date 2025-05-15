import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility for generating various student reports.
 */
public class ReportGenerator {

    /**
     * Exports all subjects, their overall percentage, credit hours,
     * and computed GPA (4.0 scale) for a single semester into a CSV file.
     *
     * @param semesters   the list of all semesters
     * @param semNum      the semester number to report on
     * @param outputPath  path to the CSV file to write
     * @throws IOException on I/O errors
     */
    public void generateSemesterReport(
            List<Semester> semesters,
            int semNum,
            String outputPath
    ) throws IOException {
        // Find the semester
        List<Subject> subjects = semesters.stream()
                .filter(s -> s.getSemesterNumber() == semNum)
                .flatMap(s -> s.getSubjects().stream())
                .collect(Collectors.toList());

        try (BufferedWriter out = new BufferedWriter(new FileWriter(outputPath))) {
            // CSV header
            out.write("Subject ID,Subject Name,Credits,Overall %,GPA (4.0)\n");

            GpaCalculator gpaCalc = new GpaCalculator();
            for (Subject sub : subjects) {
                // Sum weighted contributions = overall percentage
                double overallPercent = sub.getMarks().stream()
                        .mapToDouble(Marks::computeContribution)
                        .sum();
                double gpa = gpaCalc.convertPercentageToGpa(overallPercent);

                out.write(String.format(
                        "%s,%s,%d,%.2f,%.2f\n",
                        sub.getSubjectId(),
                        sub.getName().replace(",", ""),  // avoid commas inside
                        sub.getCreditHours(),
                        overallPercent,
                        gpa
                ));
            }
        }
    }

    /**
     * Generates a full transcript: all semesters with their GPA,
     * plus cumulative GPA at the end, as a plain-text file.
     *
     * @param semesters   the list of all semesters
     * @param outputPath  path to the .txt report to write
     * @throws IOException on I/O errors
     */
    public void generateTranscript(
            List<Semester> semesters,
            String outputPath
    ) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(outputPath))) {
            GpaCalculator gpaCalc = new GpaCalculator();
            double cumulativePoints = 0;
            int cumulativeCredits = 0;

            out.write("---------- STUDENT TRANSCRIPT ----------\n\n");
            for (Semester sem : semesters) {
                int semNum = sem.getSemesterNumber();
                double semGpa = gpaCalc.calculateSemesterGpa(semesters, semNum);

                out.write(String.format("Semester %d GPA: %.2f%n", semNum, semGpa));

                // accumulate for cumulative
                for (Subject sub : sem.getSubjects()) {
                    int credits = sub.getCreditHours();
                    double overallPercent = sub.getMarks().stream()
                            .mapToDouble(Marks::computeContribution)
                            .sum();
                    double gradePoint = gpaCalc.convertPercentageToGpa(overallPercent);

                    cumulativePoints += gradePoint * credits;
                    cumulativeCredits += credits;
                }
            }

            double cumulativeGpa = cumulativeCredits == 0
                    ? 0
                    : cumulativePoints / cumulativeCredits;

            out.write(String.format("%nCumulative GPA: %.2f%n", cumulativeGpa));
        }
    }

    /**
     * Exports all individual marks for a single subject as CSV:
     * type, obtained, total, weight, percentage, contribution.
     *
     * @param subjects    the list of all subjects across semesters
     * @param subjectId   the subject to export
     * @param outputPath  path to the CSV file to write
     * @throws IOException on I/O errors
     */
    public void exportMarksCsv(
            List<Subject> subjects,
            String subjectId,
            String outputPath
    ) throws IOException {
        // find the subject
        Subject sub = subjects.stream()
                .filter(s -> s.getSubjectId().equalsIgnoreCase(subjectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));

        try (BufferedWriter out = new BufferedWriter(new FileWriter(outputPath))) {
            out.write("Type,Obtained,Total,Weight %,Percentage %,Contribution %\n");
            for (Marks m : sub.getMarks()) {
                double pct = m.computePercentage();
                double contrib = m.computeContribution();
                out.write(String.format(
                        "%s,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                        m.getType(),
                        m.getObtained(),
                        m.getTotal(),
                        m.getWeight(),
                        pct,
                        contrib
                ));
            }
        }
    }
}

