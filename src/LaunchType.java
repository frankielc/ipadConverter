/**
 * Created by fleited on 19/02/2016.
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
