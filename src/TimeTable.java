import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

public class TimeTable {
    private List<Lecture> lectures;
    private List<String> days;
    private List<String> periods;
    private Settings settings;

    public TimeTable(Settings settings) {
        this.settings = settings;
        lectures = new ArrayList<>();
        days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        periods = new ArrayList<>();
        generatePeriods();
    }

    public void generatePeriods() {
        periods.clear();
        double duration = settings.getDefaultLectureDuration();
        LocalTime start = LocalTime.parse(settings.getUniStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(settings.getUniEndTime(), DateTimeFormatter.ofPattern("HH:mm"));
        int periodNum = 1;
        while (!start.plusMinutes((long)(duration * 60)).isAfter(end)) {
            LocalTime next = start.plusMinutes((long)(duration * 60));
            String label = String.format("Period %d: %s-%s", periodNum++, start, next);
            periods.add(label);
            start = next;
        }
    }

    public List<String> getDays() { return days; }
    public List<String> getPeriods() { return new ArrayList<>(periods); }

    public void addLecture(String name, String day, Integer period, String customTime, boolean isCustom) {
        lectures.add(new Lecture(name, day, period, customTime, isCustom));
    }

    public boolean deleteLecture(String lectureId) {
        for (int i = 0; i < lectures.size(); i++) {
            if (lectures.get(i).getId().equals(lectureId)) {
                lectures.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean editLecture(String lectureId, String name, String day, Integer period, String customTime, boolean isCustom) {
        for (Lecture lec : lectures) {
            if (lec.getId().equals(lectureId)) {
                lec.setName(name);
                lec.setDay(day);
                lec.setPeriod(period);
                lec.setCustomTime(customTime);
                lec.setCustom(isCustom);
                return true;
            }
        }
        return false;
    }

    public List<Lecture> getAllLectures() {
        return new ArrayList<>(lectures);
    }

    // ----------- File Storage -----------

    public void saveToTxt(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (Lecture lec : lectures) {
                out.print(safe(lec.getId())); out.print('\t');
                out.print(safe(lec.getName())); out.print('\t');
                out.print(safe(lec.getDay())); out.print('\t');
                out.print(lec.getPeriod() == null ? "" : lec.getPeriod().toString()); out.print('\t');
                out.print(safe(lec.getCustomTime())); out.print('\t');
                out.println(lec.isCustom() ? "1" : "0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromTxt(String filename) {
        lectures.clear();
        File file = new File(filename);
        if (!file.exists()) return;
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t", -1);
                if (parts.length >= 6) {
                    String id = parts[0];
                    String name = parts[1];
                    String day = parts[2];
                    Integer period = parts[3].isEmpty() ? null : Integer.parseInt(parts[3]);
                    String customTime = parts[4];
                    boolean isCustom = "1".equals(parts[5]);
                    Lecture lec = new Lecture(name, day, period, customTime, isCustom);
                    setLectureId(lec, id);
                    lectures.add(lec);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper to set private id field via reflection (since id is generated in constructor)
    private void setLectureId(Lecture lec, String id) {
        try {
            java.lang.reflect.Field field = Lecture.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(lec, id);
        } catch (Exception e) {
            // ignore, use generated id
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\t", " ");
    }

    // ------- Upcoming classes feature --------

    public static class UpcomingClassInfo {
        public String name;
        public String time;
        public LocalDateTime dateTime;
        public UpcomingClassInfo(String name, String time, LocalDateTime dateTime) {
            this.name = name;
            this.time = time;
            this.dateTime = dateTime;
        }
    }

    /**
     * Returns the next `count` upcoming classes *for today only*, sorted by date and time.
     * Ongoing classes (within 10 minutes of start) are shown at the top.
     * Classes more than 10 minutes past their start are omitted.
     */
    public List<UpcomingClassInfo> getNextUpcomingClassesWithOngoingToday(int count) {
        List<UpcomingClassInfo> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        DayOfWeek todayDayOfWeek = today.getDayOfWeek();

        for (Lecture lec : lectures) {
            if (lec.getDay() == null) continue;
            DayOfWeek lectureDay = parseDayOfWeek(lec.getDay());
            if (lectureDay == null) continue;

            if (!lectureDay.equals(todayDayOfWeek)) continue; // Only today's lectures

            LocalTime lectureTime = null;
            if (lec.isCustom()) {
                try {
                    lectureTime = LocalTime.parse(lec.getCustomTime());
                } catch (Exception ignored) {}
            } else if (lec.getPeriod() != null) {
                try {
                    double duration = settings.getDefaultLectureDuration();
                    LocalTime start = LocalTime.parse(settings.getUniStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
                    int periodIndex = lec.getPeriod() - 1;
                    lectureTime = start.plusMinutes((long) (duration * 60 * periodIndex));
                } catch (Exception ignored) {}
            }
            if (lectureTime == null) continue;

            LocalDateTime classDateTime = LocalDateTime.of(today, lectureTime);

            // If class is already over (10 min grace), skip it
            if (now.isAfter(classDateTime.plusMinutes(10))) {
                continue;
            }

            String timeString = String.format("%02d:%02d", lectureTime.getHour(), lectureTime.getMinute());
            result.add(new UpcomingClassInfo(lec.getName(), timeString, classDateTime));
        }

        // Sort: ongoing classes (now >= start & now < start+10min) first, then by date/time
        result.sort((a, b) -> {
            LocalDateTime nowTime = LocalDateTime.now();
            boolean aOngoing = !nowTime.isBefore(a.dateTime) && nowTime.isBefore(a.dateTime.plusMinutes(10));
            boolean bOngoing = !nowTime.isBefore(b.dateTime) && nowTime.isBefore(b.dateTime.plusMinutes(10));
            if (aOngoing && !bOngoing) return -1;
            if (!aOngoing && bOngoing) return 1;
            return a.dateTime.compareTo(b.dateTime);
        });

        // Only keep up to count
        return result.size() > count ? result.subList(0, count) : result;
    }

    private DayOfWeek parseDayOfWeek(String day) {
        switch (day.toLowerCase()) {
            case "monday": return DayOfWeek.MONDAY;
            case "tuesday": return DayOfWeek.TUESDAY;
            case "wednesday": return DayOfWeek.WEDNESDAY;
            case "thursday": return DayOfWeek.THURSDAY;
            case "friday": return DayOfWeek.FRIDAY;
            case "saturday": return DayOfWeek.SATURDAY;
            case "sunday": return DayOfWeek.SUNDAY;
            default: return null;
        }
    }
}