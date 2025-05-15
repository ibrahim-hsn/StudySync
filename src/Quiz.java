import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Quiz implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String quizId;
    private final LocalDate quizDate;
    private final List<String> topics = new ArrayList<>();

    public Quiz(String quizId, LocalDate quizDate, List<String> topics) {
        this.quizId   = quizId;
        this.quizDate = quizDate;
        this.topics.addAll(topics);
    }

    public String getQuizId()     { return quizId;   }
    public LocalDate getQuizDate(){ return quizDate; }
    public List<String> getTopics(){return topics;    }

    @Override
    public String toString() {
        return String.format(
                "%s on %s [%s]",
                quizId, quizDate,
                String.join(", ", topics)
        );
    }
}
