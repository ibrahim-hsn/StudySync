import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Analyzes subjects by performance: worst overall first.
 * For each subject, shows entered marks for assignments, quizzes, and exams.
 */
public class PerformanceAnalyzer {
    /**
     * Prints a performance report to the console.
     *
     * @param semesters  List of semesters to analyze
     */
    public void reportBySubjectPerformance(List<Semester> semesters) {
        // 1) Flatten all subjects
        List<Subject> allSubs = semesters.stream()
                .flatMap(s -> s.getSubjects().stream())
                .collect(Collectors.toList());

        // 2) Compute each subject's overall percentage
        Map<Subject, Double> pctMap = new HashMap<>();
        for (Subject sub : allSubs) {
            double overall = sub.getMarks().stream()
                    .mapToDouble(Marks::computeContribution)
                    .sum();
            pctMap.put(sub, overall);
        }

        // 3) Sort subjects by overall % ascending (worst first)
        List<Subject> sorted = allSubs.stream()
                .sorted(Comparator.comparingDouble(pctMap::get))
                .collect(Collectors.toList());

        // 4) Print the report
        System.out.println("\n=== Subject Performance Report ===");
        for (Subject sub : sorted) {
            double pct = pctMap.get(sub);
            System.out.printf("%n%s (%s) — Overall: %.2f%%%n",
                    sub.getSubjectId(), sub.getName(), pct);

            // Group marks by type
            Map<String, List<Marks>> marksByType = sub.getMarks().stream()
                    .collect(Collectors.groupingBy(Marks::getType));

            // For each category, print if present
            String[] types = { "assignment", "quiz", "exam" };
            for (String type : types) {
                List<Marks> list = marksByType.get(type);
                if (list != null && !list.isEmpty()) {
                    System.out.println("  " + capitalize(type) + " Marks:");
                    for (Marks m : list) {
                        System.out.printf("    • %.2f/%.2f → %.2f%% (contrib %.2f%%)%n",
                                m.getObtained(), m.getTotal(),
                                m.computePercentage(), m.computeContribution());
                    }
                }
            }
        }
        System.out.println();  // extra newline at end
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}


