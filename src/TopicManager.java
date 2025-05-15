/* TopicManager.java */
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Manages Topic entries and persists to topic.ser.
 */
public class TopicManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Topic> topics = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static TopicManager load() {
        List<Topic> list = SerializationUtil.load("topic.ser");
        TopicManager mgr = new TopicManager();
        if (list != null) mgr.topics = list;
        return mgr;
    }

    public void save() {
        SerializationUtil.save(topics, "topic.ser");
    }

    public void manage(Scanner sc) {
        System.out.println("\n=== Topic Manager ===");
        while (true) {
            System.out.println("[add, remove, list, done]");
            String cmd = sc.nextLine().trim().toLowerCase();
            if (cmd.equals("add")) {
                System.out.print("Topic ID: ");
                String id = sc.nextLine().trim();
                System.out.print("Name: ");
                String name = sc.nextLine().trim();
                System.out.print("Subject ID: ");
                String sid = sc.nextLine().trim();
                topics.add(new Topic(id, name, sid, LocalDate.now()));
                System.out.println("Added.");
            } else if (cmd.equals("remove")) {
                list();  //Uses the public list method
                System.out.print("Index to remove: ");
                try {
                    int i = Integer.parseInt(sc.nextLine().trim()) - 1;
                    System.out.println("Removed: " + topics.remove(i));
                } catch(Exception e) {
                    System.out.println("Invalid.");
                }
            } else if (cmd.equals("list")) {
                list();  //Uses the public list method
            } else if (cmd.equals("done")) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
        save();
    }

    /**
     * Public listing of all topics.
     */
    public void list() {
        if (topics.isEmpty()) {
            System.out.println("No topics available.");
        } else {
            System.out.println("=== Topics ===");
            for (int i = 0; i < topics.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, topics.get(i));
            }
        }
    }

    public List<Topic> getTopics() {
        return topics;
    }
}
