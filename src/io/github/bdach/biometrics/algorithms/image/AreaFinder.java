package io.github.bdach.biometrics.algorithms.image;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

public class AreaFinder {
    private AreaFinder() {}

    private static final int[][] NEIGHBOURHOOD = {
            {-1,  0},
            { 1,  0},
            { 0, -1},
            { 0,  1}
    };

    public static Rectangle findByFloodFill(Image image, int startX, int startY) {
        Stack<Point> pointsToVisit = new Stack<>();

        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        boolean[][] visited = new boolean[width][height];

        int minX = startX;
        int maxX = startX;
        int minY = startY;
        int maxY = startY;
        pointsToVisit.push(new Point(startX, startY));
        while (!pointsToVisit.empty()) {
            Point next = pointsToVisit.pop();
            Color color = pixelReader.getColor(next.x, next.y);
            boolean isBlack = color.getRed() == 0.0;
            if (isBlack) {
                minX = Math.min(minX, next.x);
                maxX = Math.max(maxX, next.x);
                minY = Math.min(minY, next.y);
                maxY = Math.max(maxY, next.y);
                visited[next.x][next.y] = true;
                for (int[] neighbour : NEIGHBOURHOOD) {
                    int newX = next.x + neighbour[0];
                    int newY = next.y + neighbour[1];
                    if (newX < 0 || newX >= width || newY < 0 || newY >= height || visited[newX][newY]) continue;
                    Point newPoint = new Point(newX, newY);
                    pointsToVisit.push(newPoint);
                }
            }
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public static Rectangle findRadially(Image image, int centerX, int centerY, int pupilRadius) {
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        Hashtable<Integer, Long> radiusCounts = new Hashtable<>();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                Color color = pixelReader.getColor(x, y);
                if (color.getRed() == 1.0) continue;
                double dx = centerX - x;
                double dy = centerY - y;
                double r = Math.sqrt(dx * dx + dy * dy);
                if (r < 2 * pupilRadius || r > 5 * pupilRadius) continue;
                int rFloor = (int) Math.floor(r);
                radiusCounts.compute(rFloor, (k, v) -> (v == null) ? 0 : v + 1);
            }
        }
        long maxCount = 0;
        int resultRadius = Math.max(width, height);
        for (Map.Entry<Integer, Long> entry : radiusCounts.entrySet()) {
            double proportion = (double) entry.getValue() / (2 * Math.PI * entry.getKey());
            if (maxCount <= entry.getValue()) {
                maxCount = entry.getValue();
                resultRadius = entry.getKey();
            }
        }
        return new Rectangle(
                centerX - resultRadius,
                centerY - resultRadius,
                2 * resultRadius,
                2 * resultRadius
        );
    }
}
