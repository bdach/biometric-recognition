package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.paint.Color;

public class HistogramNormalizingFilter extends PixelFilter {
    private double minRed;
    private double minGreen;
    private double minBlue;

    private double maxRed;
    private double maxGreen;
    private double maxBlue;

    private Double redFactor;
    private Double greenFactor;
    private Double blueFactor;

    public HistogramNormalizingFilter() {
        reset();
    }

    @Override
    public void reset() {
        this.minRed = 1;
        this.minGreen = 1;
        this.minBlue = 1;

        this.maxRed = 0;
        this.maxGreen = 0;
        this.maxBlue = 0;

        this.redFactor = null;
        this.greenFactor = null;
        this.blueFactor = null;
    }

    @Override
    public void preprocessPixel(Color inColor) {
        double red = inColor.getRed();
        double green = inColor.getGreen();
        double blue = inColor.getBlue();

        this.minRed = Math.min(red, minRed);
        this.minGreen = Math.min(green, minGreen);
        this.minBlue = Math.min(blue, minBlue);

        this.maxRed = Math.max(red, maxRed);
        this.maxGreen = Math.max(green, maxGreen);
        this.maxBlue = Math.max(blue, maxBlue);
    }

    private void calculateFactors() {
        if (redFactor != null && greenFactor != null && blueFactor != null) {
            return;
        }
        redFactor = minRed < maxRed ? maxRed - minRed : 1;
        greenFactor = minGreen < maxGreen ? maxGreen - minGreen : 1;
        blueFactor = minBlue < maxBlue ? maxBlue - minBlue : 1;
    }

    @Override
    public Color applyFilter(Color inColor) {
        calculateFactors();

        double redComponent = (inColor.getRed() - minRed) / redFactor;
        double greenComponent = (inColor.getGreen() - minGreen) / greenFactor;
        double blueComponent = (inColor.getBlue() - minBlue) / blueFactor;
        return Color.color(
                Helpers.clamp(redComponent),
                Helpers.clamp(greenComponent),
                Helpers.clamp(blueComponent),
                inColor.getOpacity()
        );
    }

}
