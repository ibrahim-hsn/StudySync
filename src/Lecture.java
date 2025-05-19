public class Lecture {
    private String id;
    private String name;
    private String day;
    private Integer period;
    private String customTime;
    private boolean isCustom;

    public Lecture(String name, String day, Integer period, String customTime, boolean isCustom) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.day = day;
        this.period = period;
        this.customTime = customTime;
        this.isCustom = isCustom;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDay() { return day; }
    public Integer getPeriod() { return period; }
    public String getCustomTime() { return customTime; }
    public boolean isCustom() { return isCustom; }
    public void setName(String v) { name = v; }
    public void setDay(String v) { day = v; }
    public void setPeriod(Integer v) { period = v; }
    public void setCustomTime(String v) { customTime = v; }
    public void setCustom(boolean v) { isCustom = v; }
}