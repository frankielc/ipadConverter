/**
 * Created by fleited on 23/02/2016.
 */
public class Log {
    private static Window window;

    public Log(Window window) {
        Log.window = window;
    }

    public static void appendToInfoArea(String txt) {
        if(window!=null)
            window.appendToInfoArea(txt);
    }

    public static void replaceRealTimeArea(String txt) {
        if(window!=null)
            window.replaceRealTimeArea(txt);
    }

}
