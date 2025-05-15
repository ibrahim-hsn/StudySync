import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class AssignmentPriorityCalculator implements Comparator<Assignment> {
    @Override
    public int compare(Assignment a1, Assignment a2) {
        long d1 = ChronoUnit.DAYS.between(LocalDate.now(), a1.getDueDate());
        long d2 = ChronoUnit.DAYS.between(LocalDate.now(), a2.getDueDate());

        boolean u1 = d1 < 3, u2 = d2 < 3;
        if (u1 && !u2) return -1;
        if (u2 && !u1) return 1;
        if (u1) return Long.compare(d1, d2);

        // non-urgent: higher-credit subjects first
        int c1 = findCredits(a1), c2 = findCredits(a2);
        int cmp = Integer.compare(c2, c1);
        if (cmp != 0) return cmp;
        return Long.compare(d1, d2);
    }

    private int findCredits(Assignment a) {
        // In this flat model we need a lookup;
        // for now stub to 0 or wire in via a map if needed.
        return 0;
    }
}
