package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.paint.Color;

public class GrayscaleFilter extends PixelFilter {
    private final double redFactor;
    private final double greenFactor;
    private final double blueFactor;

    public GrayscaleFilter(double redFactor, double greenFactor, double blueFactor) {
        this.redFactor = redFactor;
        this.greenFactor = greenFactor;
        this.blueFactor = blueFactor;
    }

    @Override
    public Color applyFilter(Color inColor) {
        double color = redFactor * inColor.getRed()
                + greenFactor * inColor.getGreen()
                + blueFactor * inColor.getBlue();
        double clampedColor = Helpers.clamp(color);
        return Color.color(
                clampedColor,
                clampedColor,
                clampedColor,
                inColor.getOpacity()
        );
    }

}
