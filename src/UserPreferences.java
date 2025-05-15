import java.io.Serializable;

/**
 * Stores per-user settings.
 */
public class UserPreferences implements Serializable {
    private static final long serialVersionUID = 1L;

    private int defaultReminderDays = 3;
    private String dateFormat = "yyyy-MM-dd";
    private boolean darkMode = false;

    // Getters and setters
    public int getDefaultReminderDays()               { return defaultReminderDays; }
    public void setDefaultReminderDays(int d)         { this.defaultReminderDays = d; }
    public String getDateFormat()                     { return dateFormat;         }
    public void setDateFormat(String fmt)             { this.dateFormat = fmt;     }
    public boolean isDarkMode()                       { return darkMode;           }
    public void setDarkMode(boolean darkMode)         { this.darkMode = darkMode;  }

    @Override
    public String toString() {
        return String.format(
                "Prefs: reminderDays=%d, dateFormat=%s, darkMode=%s",
                defaultReminderDays, dateFormat,
                darkMode ? "ON" : "OFF"
        );
    }
}

