package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.algorithms.image.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class IrisLocator {
    private static final Filter[] PUPIL_CHAIN = {
            new HistogramNormalizingFilter(),
            new GaussianConvolutionFilter(1.5),
            new GrayscaleFilter(0.299, 0.587, 0.114),
            new ThresholdFilter(5.5),
            new ErosionFilter(5),
            new DilationFilter(5),
    };
    private static final Filter[] IRIS_CHAIN = {
            new HistogramNormalizingFilter(),
            new GaussianConvolutionFilter(1.8),
            new GrayscaleFilter(0.299, 0.587, 0.114),
            new ThresholdFilter(1.4),
            new DilationFilter(7),
            new ErosionFilter(7),
    };

    public static IrisLocation locate(Image inputImage) {
        WritableImage pupilImage = runChain(inputImage, PUPIL_CHAIN);
        ProjectionData projectionData = ProjectionData.get(pupilImage);
        Rectangle pupilArea = AreaFinder.findByFloodFill(
                pupilImage,
                (int) projectionData.getEyeX(),
                (int) projectionData.getEyeY()
        );
        int centerX = (int) pupilArea.getCenterX();
        int centerY = (int) pupilArea.getCenterY();
        int innerRadius = (pupilArea.width + pupilArea.height) / 4;

        WritableImage irisImage = runChain(inputImage, IRIS_CHAIN);
        Rectangle irisArea = AreaFinder.findRadially(irisImage, centerX, centerY, innerRadius);
        int outerRadius = (irisArea.width + irisArea.height) / 4;

        return new IrisLocation(
                centerX,
                centerY,
                innerRadius,
                outerRadius
        );
    }

    private static WritableImage runChain(Image inputImage, Filter[] filters) {
        PixelReader input = inputImage.getPixelReader();
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();
        WritableImage originalImage = new WritableImage(input, width, height);
        WritableImage resultImage = new WritableImage(input, width, height);
        for (Filter filter : filters) {
            filter.reset();
            PixelReader pixelReader = originalImage.getPixelReader();
            PixelWriter pixelWriter = resultImage.getPixelWriter();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    Color color = pixelReader.getColor(x, y);
                    filter.preprocessPixel(color);
                }
            }
            filter.endPreprocessing();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    Color outColor = filter.getPixelColor(pixelReader, x, y, width, height);
                    pixelWriter.setColor(x, y, outColor);
                }
            }
            originalImage = resultImage;
            resultImage = new WritableImage(originalImage.getPixelReader(), width, height);
        }
        return originalImage;
    }
}
