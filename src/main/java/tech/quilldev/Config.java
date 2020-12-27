package tech.quilldev;

public class Config {
    public static float TICKS_PER_SECOND = 90f;
    public static float TICK_RATE = 1 / TICKS_PER_SECOND;
    public static long MS_PER_TICK = (long) (1f / TICKS_PER_SECOND * 1000f);
}
