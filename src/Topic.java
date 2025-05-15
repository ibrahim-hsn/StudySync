import java.io.Serializable;
import java.time.LocalDate;

/**
 * A quiz/revision topic within a subject.
 */
public class Topic implements Serializable {
    private static final long serialVersionUID = 1L;

    private String topicId;
    private String name;
    private String subjectId;
    private LocalDate lastRevised;

    public Topic(String topicId, String name, String subjectId, LocalDate lastRevised) {
        this.topicId = topicId;
        this.name = name;
        this.subjectId = subjectId;
        this.lastRevised = lastRevised;
    }

    public String getTopicId()      { return topicId;     }
    public String getName()         { return name;        }
    public String getSubjectId()    { return subjectId;   }
    public LocalDate getLastRevised(){ return lastRevised;}

    @Override
    public String toString() {
        return String.format("%s: %s (Last revised: %s)",
                topicId, name, lastRevised);
    }
}
