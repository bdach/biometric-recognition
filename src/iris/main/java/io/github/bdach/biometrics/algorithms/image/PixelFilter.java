package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public abstract class PixelFilter implements Filter {
    protected abstract Color applyFilter(Color inColor);

    @Override
    public void reset() { }
    @Override
    public void preprocessPixel(Color inColor) { }
    @Override
    public void endPreprocessing() { }

    @Override
    public Color getPixelColor(PixelReader reader, int x, int y, int w, int h) {
        Color inColor = reader.getColor(x, y);
        return applyFilter(inColor);
    }
}
