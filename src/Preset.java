/**
 * ffmpeg presets for converting to H264
 *
 */
public enum Preset {
    ULTRA_FAST,
    SUPER_FAST,
    VERY_FAST,
    FASTER,
    FAST,
    MEDIUM,
    SLOW,
    SLOWER,
    VERY_SLOW;
    
    @Override
    public String toString() {
        return name().replace("_", "").toLowerCase();
    }
    
}
