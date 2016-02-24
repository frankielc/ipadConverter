import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fleited on 19/02/2016.
 */
public class FilesUtilities {

    /**
     * Returns a list of all files that can be converted in the current working directory
     * @return
     */
    public static List<File> listAllConvertableFilesInCwd() {
        File folder = new File( System.getProperty("user.dir") );
        File[] listOfFiles = folder.listFiles();
        List<File> files = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            String match = ".*(?<!ac2)\\.mkv$|.*\\.avi$|.*\\.flv$"; // match every video except mkv with ac2 behind it
            if(listOfFiles[i].getName().matches(match)) {
                files.add(listOfFiles[i]);
            }
        }
        return files;
    }


    public static boolean doesEachFileHasSubtitle(List<File> files) throws IOException {
        List<String> filesThatShouldExistButDont = new ArrayList<>();

        for(File f : files) {
            System.out.println("File: " + f.toString());

            String fileWithSrtExtension = f.toString().substring(0, f.toString().length()-4) + ".srt";
            File tmpFile = new File(fileWithSrtExtension);

            System.out.println(fileWithSrtExtension);
            System.out.println(tmpFile.getCanonicalFile().toString());

            System.out.println(tmpFile.getName() + " " + tmpFile.getTotalSpace()/1024 );

            if(!tmpFile.exists()||!tmpFile.getCanonicalFile().toString().equals(fileWithSrtExtension)) {
                filesThatShouldExistButDont.add(tmpFile.toString());
            }
        }

        for(String s : filesThatShouldExistButDont) {
            System.out.println(s + " does NOT exist!");
            Log.appendToInfoArea(s + " does NOT exist!");
        }
        return (filesThatShouldExistButDont.size()>0) ? false : true;
    }


}
