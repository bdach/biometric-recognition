package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ProjectionData {
    private long[] projectionX;
    private long[] projectionY;

    private ProjectionData(int width, int height) {
        projectionX = new long[width];
        projectionY = new long[height];
    }

    private void addPoint(int x, int y, Color color) {
        double level = color.getRed();
        if (level == 0.0) {
            projectionX[x] += 1;
            projectionY[y] += 1;
        }
    }

    public double getEyeX() {
        return getMaxOverDimension(projectionX);
    }

    public double getEyeY() {
        return getMaxOverDimension(projectionY);
    }

    private double getMaxOverDimension(long[] projection) {
        long max = 0;
        long idxSum = 0;
        int idxCount = 0;
        for (int i = 0; i < projection.length; ++i) {
            if (projection[i] > max) {
                max = projection[i];
                idxSum = i;
                idxCount = 1;
            } else if (projection[i] == max) {
                idxSum += i;
                idxCount += 1;
            }
        }
        return (double) idxSum / idxCount;
    }

    public static ProjectionData get(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        ProjectionData data = new ProjectionData(width, height);
        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = reader.getColor(x, y);
                data.addPoint(x, y, color);
            }
        }
        return data;
    }
}
