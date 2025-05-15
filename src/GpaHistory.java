import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GpaHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<GpaHistoryEntry> entries = new ArrayList<>();

    public void addEntry(GpaHistoryEntry entry) {
        entries.add(entry);
    }

    public List<GpaHistoryEntry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GpaHistoryEntry e : entries) {
            sb.append(e).append("\n");
        }
        return sb.toString();
    }
}
