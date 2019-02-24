package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.paint.Color;

public class DilationFilter extends MorphologicalFilter {
    public DilationFilter(int elementSize) {
        super(elementSize);
    }

    @Override
    public Color apply(Color[][] colorMatrix) {
        for (int y = 0; y < elementSize; ++y) {
            for (int x = 0; x < elementSize; ++x) {
                if (structuringElement[y][x] && colorMatrix[y][x].getRed() == 0.0f) {
                    return Color.BLACK;
                }
            }
        }
        return Color.WHITE;
    }

}
