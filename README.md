iPad Mini 2 (retina) mkv, avi, flv converter
=======================

This tool wraps ffmpeg binaries (osx and windows) into a self executing jar.

* replaces symbols like ♪ from subtitles that break iOS native player
* inserts srt subtitle into *.mp4 file
* downmixes from 5.1 to stereo making sure vocals sound ok
* increases volume to 0dB (maximum without clipping)

Just put `ipadConverter.jar` in the same directory as the video files and double click it. That's it.

**Notice:**
Current version requires you to have a subtitle with the same name as the file.

    # if you have a directory with these files
    myVideoFile_1.mkv
    myVideoFile_1.srt
    myVideoFile_2.avi
    myVideoFile_2.srt

    # ipadConverter will produce:
    myVideoFile_1_ios.mp4
    myVideoFile_2_ios.mp4
