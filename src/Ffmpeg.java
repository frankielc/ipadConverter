import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class Ffmpeg {




    public static boolean compress(String fileStr, String subtitleStr) throws IOException, InterruptedException {
        unpackFfmpeg(); // we extract ffmpeg to perform it's thing

        // convert file to stereo
        convertFileToStereo(fileStr);

        // get max DB
        BigDecimal decibel = getMaxVolume(fileStr.substring(0, fileStr.length()-4)+"_ac2.mkv");

        // compress to iPad with proper sound
        compressWithMaxSound(fileStr, subtitleStr, decibel);

        // this just serves to see if max volume is respected or not and can be cancelled in the future
        getMaxVolume(fileStr.substring(0, fileStr.length()-4)+"_ios.mp4");

        // cleanup temporary AC2 files
        try {
            Files.delete(Paths.get(fileStr.substring(0, fileStr.length() - 4) + "_ac2.mkv"));
        } catch (IOException e) {
            Log.appendToInfoArea(fileStr.substring(0, fileStr.length() - 4) + "_ac2.mkv was not found to be deleted");
            e.printStackTrace();
        }

        deleteFfmpeg(); // we clean up the no longer needed library

        return true;
    }


    private static boolean convertFileToStereo(String fileStr) throws IOException, InterruptedException {
        Log.appendToInfoArea("downmixing " + printFileWithoutPath(fileStr) + " to stereo");

        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg",
                "-y", "-i", fileStr,
                "-c:v", "copy",
                "-preset", "ultrafast",
                "-codec:a" ,"aac",
                "-strict", "experimental",
                "-ac", "2",
                "-clev", "1.414",
                "-slev", ".5",
                "-ab", "160k",
                fileStr.substring(0, fileStr.length()-4)+"_ac2.mkv");
        builder.directory( new File( "/" ).getAbsoluteFile() ); // this is where you set the root folder for the executable to run with
        builder.redirectErrorStream(true);
        //Log.appendToInfoArea(printCommand(builder));
        System.out.println("running: " + printCommand(builder));
        Process process =  builder.start();

        String output = processOutput(process);
        int result = process.waitFor();
        process.destroyForcibly();

        System.out.printf("convertFileToStereo exited with result %d and output %s%n", result, output);
        Log.replaceRealTimeArea(String.format("convertFileToStereo exited with result %d and output %s%n", result, output));
        return true;
    }


    private static BigDecimal getMaxVolume(final String fileStr) throws IOException, InterruptedException {
        Log.appendToInfoArea("getting maximum volume for " + printFileWithoutPath(fileStr));

        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg",
                "-i", fileStr,
                "-af", "volumedetect",
                "-f", "null", "/dev/null");
        builder.directory( new File( "/" ).getAbsoluteFile() ); // this is where you set the root folder for the executable to run with
        builder.redirectErrorStream(true);
        //Log.appendToInfoArea(printCommand(builder));
        System.out.println("running: " + printCommand(builder));
        Process process =  builder.start();

        String output = processOutput(process);
        int result = process.waitFor();
        process.destroyForcibly();

        System.out.printf("convertFileToStereo exited with result %d and output %s%n", result, output);
        Log.replaceRealTimeArea(String.format("convertFileToStereo exited with result %d and output %s%n", result, output));

        Pattern p = Pattern.compile("max_volume:(.*)dB", Pattern.MULTILINE);
        Matcher matcher = p.matcher(output);
        if (matcher.find())
        {
            BigDecimal decibel = new BigDecimal(matcher.group(1).replaceAll("\\s",""));
            System.out.println(printFileWithoutPath(fileStr) + " has max volume of " + decibel + " dB");
            Log.appendToInfoArea(printFileWithoutPath(fileStr) + " has max volume of " + decibel + " dB");
            return decibel;
        } else {
            return new BigDecimal(0);
        }
    }


    private static boolean compressWithMaxSound(String fileStr, String subtitleStr, BigDecimal maxSound) throws IOException, InterruptedException {
        String fileStrAC2 = fileStr.substring(0, fileStr.length()-4)+"_ac2.mkv";
        Log.appendToInfoArea("compressWithMaxSound " + printFileWithoutPath(fileStr));

        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", fileStrAC2,
                "-i", subtitleStr,
                "-map_metadata", "-1",
                "-map", "0:v:0",
                "-map", "0:a:0",
                "-map", "1:s",
                "-c", "copy",
                "-c:s", "mov_text",
                "-metadata:s:s:0", "language=eng",
                "-vcodec", "libx264",
                "-profile:v", "high",
                "-tune", "film",
                "-level", "4.1",
                "-crf", "23",
                "-threads", "0",
                "-vf", "scale='min(iw,1920)':-1",
                "-acodec", "aac",
                "-strict", "experimental",
                "-af", "volume=volume="+maxSound.abs().toPlainString()+"dB:precision=fixed",
                "-preset", "veryfast",
                "-f", "mp4",
                fileStr.substring(0, fileStr.length()-4)+"_ios.mp4");
        builder.directory( new File( "/" ).getAbsoluteFile() ); // this is where you set the root folder for the executable to run with
        builder.redirectErrorStream(true);
        //Log.appendToInfoArea(printCommand(builder));
        System.out.println("running: " + printCommand(builder));
        Process process =  builder.start();

        String output = processOutput(process);
        int result = process.waitFor();
        process.destroyForcibly();

        System.out.printf("compressWithMaxSound exited with result %d and output %s%n", result, output);
        Log.replaceRealTimeArea(String.format("compressWithMaxSound exited with result %d and output %s%n", result, output));
        return true;
    }


    private static void unpackFfmpeg() throws IOException {
        // detect if this is being run from inside the jar
        if(Converter.launchType.equals(LaunchType.JAR)) {
            String ffmpegStr = "";
            switch (Converter.osType) {
                case WINDOWS:
                    ffmpegStr = "ffmpeg.exe";
                    break;
                case OSX:
                    ffmpegStr = "ffmpeg";
                    break;
            }

            // unzip ffmpeg from inside the JAR
            JarFile jarfile = new JarFile(System.getProperty("java.class.path"));

            Enumeration<JarEntry> entries = jarfile.entries();
            while(entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                System.out.println(e.getName() + " " + e.toString());
            }

            System.out.println("will now try to capture " + ffmpegStr + " from jar file");
            ZipEntry ffmpeg = jarfile.getEntry(ffmpegStr);
            if(ffmpeg==null) {
                System.out.println("could not extract " + ffmpegStr + " from jarfile - exit");
                System.exit(-1);
            }

            InputStream inputStream = jarfile.getInputStream(ffmpeg);
            Files.copy(inputStream, Paths.get(ffmpegStr));

            return;
        }


        if(Converter.launchType.equals(LaunchType.TERMINAL)) {
            switch (Converter.osType) {
                case WINDOWS:
                    Files.copy(Paths.get("ffmpeg.exe"), Paths.get("ffmpeg.exe"), StandardCopyOption.REPLACE_EXISTING) ;
                    break;
                case OSX:
                    Files.copy(Paths.get("ffmpeg"), Paths.get("ffmpeg"), StandardCopyOption.REPLACE_EXISTING) ;
                    break;
            }
            return;
        }

        Log.appendToInfoArea("we could not detect if we were running from JAR or from TERMINAL so ffmpeg was not unpacked");
        System.out.println("we could not detect if we were running from JAR or from TERMINAL so ffmpeg was not unpacked");
    }


    private static void deleteFfmpeg() throws IOException {
        switch (Converter.osType) {
            case WINDOWS:
                Files.delete(Paths.get("ffmpeg.exe"));
                break;
            case OSX:
                Files.delete(Paths.get("ffmpeg"));
                break;
        }
    }


    private static String processOutput(Process process) {
        Scanner s = new Scanner(process.getInputStream());
        StringBuilder text = new StringBuilder();
        while (s.hasNextLine()) {
            String line = s.nextLine();
            System.out.print("\r           " + line);
            Log.replaceRealTimeArea("\r           " + line);

            text.append(line);
            text.append("\n");
        }
        s.close();

        return text.toString();
    }


    private static String printCommand(ProcessBuilder builder) {
        String cmd = "";
        for(String c : builder.command()) {
            cmd += c + " ";
        }
        return "running: " + cmd.replaceAll(Matcher.quoteReplacement(System.getProperty("user.dir") + "\\"), "");
    }


    private static String printFileWithoutPath(String file) {
        return file.replaceAll(Matcher.quoteReplacement(System.getProperty("user.dir") + "\\"), "");
    }


}
