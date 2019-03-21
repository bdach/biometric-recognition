package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public abstract class ConvolutionFilter implements Filter {
    public abstract Color apply(Color[][] colorMatrix);
    public abstract int getSize();

    @Override
    public void reset() { }
    @Override
    public void preprocessPixel(Color color) { }
    @Override
    public void endPreprocessing() { }

    @Override
    public Color getPixelColor(PixelReader reader, int x, int y, int w, int h) {
        int matrixSize = getSize();
        int offset = matrixSize / 2;

        Color[][] colors = new Color[matrixSize][];
        for (int i = 0; i < matrixSize; ++i) {
            colors[i] = new Color[matrixSize];
        }

        for (int convY = 0; convY < matrixSize; ++convY) {
            for (int convX = 0; convX < matrixSize; ++convX) {
                int xNeighbor = x + convX - offset;
                int yNeighbor = y + convY - offset;
                colors[convY][convX] = reader.getColor(
                        Helpers.clamp(xNeighbor, 0, w - 1),
                        Helpers.clamp(yNeighbor, 0, h - 1)
                );
            }
        }
        return apply(colors);
    }
}
