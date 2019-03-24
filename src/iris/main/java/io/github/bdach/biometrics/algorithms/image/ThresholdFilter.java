package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.paint.Color;

public class ThresholdFilter extends PixelFilter {
    private double grayscaleSum;
    private long pixelCount;
    private double threshold;
    private final double divisor;

    public ThresholdFilter(double divisor) {
        this.divisor = divisor;
    }

    @Override
    public void reset() {
        grayscaleSum = 0;
        pixelCount = 0;
        threshold = 0;
    }

    @Override
    public void preprocessPixel(Color inColor) {
        grayscaleSum += inColor.getRed();
        pixelCount += 1;
    }

    @Override
    public void endPreprocessing() {
        threshold = (grayscaleSum / pixelCount) / divisor;
    }

    @Override
    public Color applyFilter(Color inColor) {
        double inValue = inColor.getRed();
        double outValue = inValue < threshold ? 0 : 1;
        return Color.color(outValue, outValue, outValue, inColor.getOpacity());
    }

}
