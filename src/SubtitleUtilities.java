import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by fleited on 19/02/2016.
 */
public class SubtitleUtilities {

    /**
     * Some srt files have chars very high up on the ascii table (eight note example - U+266B - ♪)
     * That break iOS native player and thus should be replaced
     */
    public static long replaceAllWeirdCharsFromFile(String f) throws IOException {
        int weirdChars = 0;
        String text = new String(Files.readAllBytes(Paths.get(f)));
        char[] textArray = text.toCharArray();

        for(int i=0; i<textArray.length; i++) {
            if(text.codePointAt(i)>500&&i!=0) {
                textArray[i] = '-';
                //System.out.println(textArray[i]);
                //System.out.println(i + " " + text.charAt(i) + " " + text.codePointAt(i) );
                weirdChars++;
            }
        }
        text = String.valueOf(textArray);
        Files.write(Paths.get(f), text.getBytes());
        return weirdChars;
    }

    public static List<String> convertFileNameToSubtitlesNames(List<File> files) {
        List<String> subtitlesList = new ArrayList<>();

        for(File f : files) {
            String fileWithSrtExtension = f.toString().substring(0, f.toString().length() - 4) + ".srt";
            subtitlesList.add(fileWithSrtExtension);
        }

        return subtitlesList;
    }

}
