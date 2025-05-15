import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Exam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subjectId;
    private LocalDate midTermDate;
    private LocalDate finalExamDate;
    private double examPercentage;

    public Exam(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectId()      { return subjectId;    }
    public LocalDate getMidTermDate() { return midTermDate;  }
    public LocalDate getFinalExamDate(){return finalExamDate;}
    public double getExamPercentage() { return examPercentage;}

    public void manageExam() {
        Scanner scanner = new Scanner(System.in);

        // Mid-term date
        while (true) {
            System.out.print("Mid-term date (YYYY-MM-DD or skip): ");
            String in = scanner.nextLine().trim();
            if (in.equalsIgnoreCase("skip")) break;
            try {
                midTermDate = LocalDate.parse(in);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Bad format.");
            }
        }

        // Final date
        while (true) {
            System.out.print("Final exam date (YYYY-MM-DD or skip): ");
            String in = scanner.nextLine().trim();
            if (in.equalsIgnoreCase("skip")) break;
            try {
                finalExamDate = LocalDate.parse(in);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Bad format.");
            }
        }

        // Percentage weight
        while (true) {
            System.out.print("Exam weight % (or skip): ");
            String in = scanner.nextLine().trim();
            if (in.equalsIgnoreCase("skip")) break;
            try {
                double p = Double.parseDouble(in);
                if (p < 0 || p > 100) {
                    System.out.println("Must be 0–100");
                    continue;
                }
                examPercentage = p;
                break;
            } catch (NumberFormatException e) {
                System.out.println("Bad number.");
            }
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Exam [%s] mid:%s final:%s weight:%.1f%%",
                subjectId,
                midTermDate != null ? midTermDate : "N/A",
                finalExamDate != null ? finalExamDate : "N/A",
                examPercentage
        );
    }
}
