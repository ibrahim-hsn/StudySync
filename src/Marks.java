import java.io.Serializable;

public class Marks implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subjectId;
    private String type;       // "assignment", "quiz", "exam"
    private double obtained;
    private double total;
    private double weight;     // % of overall grade

    public Marks(
            String subjectId,
            String type,
            double obtained,
            double total,
            double weight
    ) {
        this.subjectId = subjectId;
        this.type      = type;
        this.obtained  = obtained;
        this.total     = total;
        this.weight    = weight;
    }

    public String getSubjectId() { return subjectId; }
    public String getType()      { return type;      }
    public double getObtained()  { return obtained;  }
    public double getTotal()     { return total;     }
    public double getWeight()    { return weight;    }

    /** Percentage score on this item */
    public double computePercentage() {
        return (total == 0) ? 0.0 : (obtained / total) * 100.0;
    }

    /** Contribution toward overall grade (%) */
    public double computeContribution() {
        return (computePercentage() / 100.0) * weight;
    }

    @Override
    public String toString() {
        return String.format(
                "%s: %.1f/%.1f (%.1f%%) → contrib %.1f%%",
                type, obtained, total,
                computePercentage(),
                computeContribution()
        );
    }
}
