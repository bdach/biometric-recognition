package io.github.bdach.biometrics.algorithms.image;

public class Helpers {
    public static final int LEVEL_COUNT = 256;

    private Helpers() { }

    static double clamp(double value) {
        double lowerClamp = Math.max(0, value);
        return Math.min(1, lowerClamp);
    }

    public static int clamp(int value, int min, int max) {
        int lowerClamp = Math.max(min, value);
        return Math.min(max, lowerClamp);
    }

    public static int getLevel(double inLevel) {
        return (int) (inLevel * (LEVEL_COUNT - 1));
    }
}
