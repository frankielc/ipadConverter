import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


class SubtitleUtilities {

    /**
     * Some srt files have chars very high up on the ascii table (eight note example - U+266B - ♪)
     * That break iOS native player and thus should be replaced
     */
    public static Map.Entry<Integer, String> replaceAllWeirdCharsFromFile(String f) throws IOException {
        int weirdCharsCount = 0;
        Set<String> weirdCharsList = new HashSet<>();
        
        String text = new String(Files.readAllBytes(Paths.get(f)), Charset.forName("UTF-8"));
        char[] textArray = text.toCharArray();

        for(int i=0; i<textArray.length; i++) {
            if(text.codePointAt(i)>500&&i!=0) {
                textArray[i] = '-';
                weirdCharsList.add(String.valueOf(text.charAt(i)));
                
                
                //System.out.println(textArray[i]);
                System.out.println(i + " " + text.charAt(i) + " " + text.codePointAt(i) );
                weirdCharsCount++;
            }
        }
        
        text = String.valueOf(textArray);
        Files.write(Paths.get(f.substring(0, f.length() - 4) + "_clean.srt"), text.getBytes());
        return new AbstractMap.SimpleEntry<>(weirdCharsCount, String.join(", ", weirdCharsList));
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
