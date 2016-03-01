import javafx.concurrent.Task;
import org.apache.commons.lang3.SystemUtils;
import java.io.File;
import java.util.List;
import java.util.Map;


class Converter extends Task {

    public static OSType osType;
    public static LaunchType launchType;


    @Override
    protected Object call() throws Exception {
        convert();
        return null;
    }



    private void convert() {
        if(SystemUtils.IS_OS_MAC_OSX) {
            osType = OSType.OSX;
        } else if(SystemUtils.IS_OS_WINDOWS) {
            osType = OSType.WINDOWS;
        } else {
            //Console.log("OS type not recognized as OSX or Windows quitting.");
            System.out.println("OS type not recognized as OSX or Windows quitting.");
            Log.appendToInfoArea("OS type not recognized as OSX or Windows quitting.");
            return;
        }

        launchType = LaunchType.getLaunchType(Converter.class.getResource("Converter.class").toString());
        Log.appendToInfoArea("running from: " + launchType);

        try {
            // get all files to convert
            List<File> filesToConvert = FilesUtilities.listAllConvertibleFilesInCwd();

            // create a list of hypothetical subtitles
            List<String> subtitles = SubtitleUtilities.convertFileNameToSubtitlesNames(filesToConvert);

            // check if each video has a matching subtitle
            if(!FilesUtilities.doesEachFileHasSubtitle(filesToConvert)) {
                Log.appendToInfoArea("exit as not every file had a subtitle");
                return;
            }

            // fix each subtitles
            for(String f : subtitles) {
                Map.Entry<Integer, String> numReplacements = SubtitleUtilities.replaceAllWeirdCharsFromFile(f);
                //Console.log("we did " + weirdChars + " replacements on " + f);
                System.out.println("we did " + numReplacements.getKey() + " replacements on " + f + " (" + numReplacements.getValue() + ")");
                Log.appendToInfoArea("we did " + numReplacements.getKey() + " replacements on " + f + " (" + numReplacements.getValue() + ")");
            }

            // if subtitles and files to convert don't match output error
            if(filesToConvert.size()!=subtitles.size()) {
                System.out.println("subtitles and files to convert don't match");
                Log.appendToInfoArea("subtitles and files to convert don't match");
                System.exit(-1);
            }

            // sends files to ffmpeg for compression with clean subtitle files
            try {
                for(int i=0; i<filesToConvert.size(); i++) {
                    Log.appendToInfoArea("");
                    Ffmpeg.compress(filesToConvert.get(i).toString(), subtitles.get(i).substring(0, subtitles.get(i).length() - 4) + "_clean.srt");
                }
            } catch (Exception e) {
                Log.appendToInfoArea(e.toString());
                e.printStackTrace();
            }

            Log.appendToInfoArea("\nall files converted");

        } catch (Exception e) {
            Log.appendToInfoArea(e.toString());
            e.printStackTrace();
        }

    }



}
