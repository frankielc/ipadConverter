import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class FilesUtilities {

    /**
     * Searches and returns a list of all files that can be 
     * converted in the current working directory
     * 
     * @return list of all files that can be converted 
     */
    public static List<File> listAllConvertibleFilesInCwd() {
        File folder = new File( System.getProperty("user.dir") );
        File[] listOfFiles = folder.listFiles();
        List<File> files = new ArrayList<>();

        for (int i = 0; i < (listOfFiles != null ? listOfFiles.length : 0); i++) {
            String match = ".*(?<!ac2)\\.mkv$|.*\\.avi$|.*\\.flv$"; // match every video except mkv with ac2 behind it
            if(listOfFiles[i].getName().matches(match)) {
                files.add(listOfFiles[i]);
            }
        }
        return files;
    }


    public static boolean doesEachFileHasSubtitle(List<File> files) throws IOException {
        List<String> filesThatShouldExistButDoNot = new ArrayList<>();

        for(File f : files) {
            System.out.println("File: " + f.toString());

            String fileWithSrtExtension = f.toString().substring(0, f.toString().length()-4) + ".srt";
            File tmpFile = new File(fileWithSrtExtension);

            System.out.println(fileWithSrtExtension);
            System.out.println(tmpFile.getCanonicalFile().toString());

            System.out.println(tmpFile.getName() + " " + tmpFile.getTotalSpace()/1024 );

            if(!tmpFile.exists()||!tmpFile.getCanonicalFile().toString().equals(fileWithSrtExtension)) {
                filesThatShouldExistButDoNot.add(tmpFile.toString());
            }
        }

        for(String s : filesThatShouldExistButDoNot) {
            System.out.println(s + " does NOT exist!");
            Log.appendToInfoArea(s + " does NOT exist!");
        }
        return filesThatShouldExistButDoNot.size() <= 0;
    }


}
