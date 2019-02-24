package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public interface Filter {
    void reset();
    void preprocessPixel(Color color);
    void endPreprocessing();
    Color getPixelColor(PixelReader reader, int x, int y, int w, int h);
}
