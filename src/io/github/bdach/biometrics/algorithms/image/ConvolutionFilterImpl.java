package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.paint.Color;

public class ConvolutionFilterImpl extends ConvolutionFilter {
    private final int neighbourhoodSize;
    private final double[][] coefficients;
    private final double denominator;

    public ConvolutionFilterImpl(int neighbourhoodSize, double[][] coefficients, double denominator) {
        this.neighbourhoodSize = neighbourhoodSize;
        this.coefficients = new double[neighbourhoodSize][];
        for (int y = 0; y < neighbourhoodSize; ++y) {
            this.coefficients[y] = new double[neighbourhoodSize];
            System.arraycopy(coefficients[y], 0, this.coefficients[y], 0, neighbourhoodSize);
        }
        this.denominator = denominator;
    }

    public ConvolutionFilterImpl(int neighbourhoodSize, double[][] coefficients) {
        this.neighbourhoodSize = neighbourhoodSize;
        this.coefficients = new double[neighbourhoodSize][];
        double denominator = 0;
        for (int y = 0; y < neighbourhoodSize; ++y) {
            this.coefficients[y] = new double[neighbourhoodSize];
            System.arraycopy(coefficients[y], 0, this.coefficients[y], 0, neighbourhoodSize);
            for (int x = 0; x < neighbourhoodSize; ++x) {
                denominator += this.coefficients[y][x];
            }
        }
        this.denominator = denominator != 0.0 ? denominator : 1;
    }

    @Override
    public int getSize() {
        return neighbourhoodSize;
    }

    @Override
    public Color apply(Color[][] colorMatrix) {
        double red = 0, green = 0, blue = 0, alpha = 0;

        for (int y = 0; y < neighbourhoodSize; ++y) {
            for (int x = 0; x < neighbourhoodSize; ++x) {
                Color color = colorMatrix[y][x];
                double coefficient = coefficients[y][x];
                red += color.getRed() * coefficient;
                green += color.getGreen() * coefficient;
                blue += color.getBlue() * coefficient;
            }
        }
        red /= denominator;
        green /= denominator;
        blue /= denominator;
        return Color.color(
                Helpers.clamp(red),
                Helpers.clamp(green),
                Helpers.clamp(blue),
                colorMatrix[neighbourhoodSize / 2][neighbourhoodSize / 2].getOpacity()
        );
    }

}
