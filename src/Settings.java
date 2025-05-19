import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Settings {
    private double defaultLectureDuration;
    private List<String> lectureNames;
    private String uniStartTime;
    private String uniEndTime;

    public Settings() {
        this.defaultLectureDuration = 1.5;
        this.lectureNames = new ArrayList<>();
        this.uniStartTime = "08:30";
        this.uniEndTime = "17:00";
    }

    public double getDefaultLectureDuration() { return defaultLectureDuration; }
    public void setDefaultLectureDuration(double d) { this.defaultLectureDuration = d; }

    public List<String> getLectureNames() { return new ArrayList<>(lectureNames); }
    public void addLectureName(String name) {
        if (name != null && !name.trim().isEmpty() && !lectureNames.contains(name)) {
            lectureNames.add(name);
        }
    }
    public void removeLectureName(String name) { lectureNames.remove(name); }

    public String getUniStartTime() { return uniStartTime; }
    public String getUniEndTime() { return uniEndTime; }
    public void setUniStartTime(String t) { this.uniStartTime = t; }
    public void setUniEndTime(String t) { this.uniEndTime = t; }

    // -------- Plain Text Serialization Methods --------
    public void saveToTxt(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("defaultLectureDuration=" + defaultLectureDuration);
            out.println("uniStartTime=" + uniStartTime);
            out.println("uniEndTime=" + uniEndTime);
            out.println("lectureNames=" + String.join(",", lectureNames));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromTxt(String filename) {
        File file = new File(filename);
        if (!file.exists()) return; // No file, use defaults

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            lectureNames.clear();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("defaultLectureDuration=")) {
                    String val = line.substring("defaultLectureDuration=".length());
                    try { defaultLectureDuration = Double.parseDouble(val); } catch (Exception ignored) {}
                } else if (line.startsWith("uniStartTime=")) {
                    uniStartTime = line.substring("uniStartTime=".length());
                } else if (line.startsWith("uniEndTime=")) {
                    uniEndTime = line.substring("uniEndTime=".length());
                } else if (line.startsWith("lectureNames=")) {
                    String val = line.substring("lectureNames=".length());
                    if (!val.trim().isEmpty()) {
                        for (String name : val.split(",")) {
                            if (!name.trim().isEmpty()) addLectureName(name.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}