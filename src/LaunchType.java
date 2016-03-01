/**
 * Detects if we're launching from the terminal or from the self executing jar
 */
public enum LaunchType {
    TERMINAL,
    JAR;

    public static LaunchType getLaunchType(String s) {
        switch (s.substring(0, 3)) {
            case "jar" : return LaunchType.JAR;
            case "fil" : return LaunchType.TERMINAL;
            default: return null;
        }
    }
}
