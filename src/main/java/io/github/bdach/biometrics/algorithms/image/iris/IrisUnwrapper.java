package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.algorithms.image.GaussianConvolutionFilter;
import io.github.bdach.biometrics.algorithms.image.GrayscaleFilter;
import io.github.bdach.biometrics.algorithms.image.HistogramNormalizingFilter;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

class IrisUnwrapper {
    private static final int WIDTH = 720;
    private static final int HEIGHT = 400;

    private Image inputImage;
    private IrisLocation recognitionResult;
    private GrayscaleFilter grayscaleFilter;
    private GaussianConvolutionFilter gaussianFilter;

    public IrisUnwrapper(Image inputImage, IrisLocation recognitionResult) {
        this.inputImage = inputImage;
        this.recognitionResult = recognitionResult;
        this.grayscaleFilter = new GrayscaleFilter(1.0 / 3, 1.0 / 3, 1.0 / 3);
        this.gaussianFilter = new GaussianConvolutionFilter(1.0);
    }

    public Image unwrap() {
        PixelReader pixelReader = inputImage.getPixelReader();
        int originalWidth = (int) inputImage.getWidth();
        int originalHeight = (int) inputImage.getHeight();

        WritableImage blurredImage = new WritableImage(originalWidth, originalHeight);
        PixelWriter pixelWriter = blurredImage.getPixelWriter();
        for (int y = 0; y < originalHeight; ++y) {
            for (int x = 0; x < originalWidth; ++x) {
                Color outColor = gaussianFilter.getPixelColor(pixelReader, x, y, originalWidth, originalHeight);
                pixelWriter.setColor(x, y, outColor);
            }
        }

        WritableImage outputImage = new WritableImage(WIDTH, HEIGHT);
        pixelWriter = outputImage.getPixelWriter();
        pixelReader = blurredImage.getPixelReader();
        for (int y = 0; y < HEIGHT; ++y) {
            for (int x = 0; x < WIDTH; ++x) {
                double r = 1 - (double) y / HEIGHT;
                double phi = ((double) x / WIDTH) * 2 * Math.PI;
                Color color = sampleOutputPixel(pixelReader, r, phi - Math.PI / 2);
                pixelWriter.setColor(x, y, color);
            }
        }
        return outputImage;
    }

    private Color sampleOutputPixel(PixelReader reader, double r, double phi) {
        // assumption: r is in [0,1], phis is in [0, 2*pi]
        double imageR = (recognitionResult.outerRadius - recognitionResult.innerRadius) * (1 - r)
                + recognitionResult.innerRadius;
        double dx = imageR * Math.cos(phi);
        double dy = imageR * Math.sin(phi);
        double x = recognitionResult.centerX + dx;
        double y = recognitionResult.centerY + dy;

        if (x < 0 || x >= inputImage.getWidth() - 1 || y < 0 || y >= inputImage.getHeight() - 1)
            return Color.color(0, 0, 0);

        int xRound = (int) Math.floor(x);
        int yRound = (int) Math.floor(y);
        // x is in the range [xRound, xRound + 1]
        // y is in the range [yRound, yRound + 1]
        double ax = x - xRound;
        double ay = y - yRound;

        Color c00 = reader.getColor(xRound, yRound);
        Color c10 = reader.getColor(xRound + 1, yRound);
        Color c01 = reader.getColor(xRound, yRound + 1);
        Color c11 = reader.getColor(xRound + 1, yRound + 1);
        Color fullColor = Color.color(
                bilinearInterpolation(c00.getRed(), c10.getRed(), c01.getRed(), c11.getRed(), ax, ay),
                bilinearInterpolation(c00.getGreen(), c10.getGreen(), c01.getGreen(), c11.getGreen(), ax, ay),
                bilinearInterpolation(c00.getBlue(), c10.getBlue(), c01.getBlue(), c11.getBlue(), ax, ay),
                bilinearInterpolation(c00.getOpacity(), c10.getOpacity(), c01.getOpacity(), c11.getOpacity(), ax, ay)
        );
        return grayscaleFilter.applyFilter(fullColor);
    }

    private static double bilinearInterpolation(double v00, double v10, double v01, double v11, double x, double y) {
        return v00 * (1 - x) * (1 - y) +
               v10 *      x  * (1 - y) +
               v01 * (1 - x) *      y  +
               v11 *      x  *      y;
    }
}
