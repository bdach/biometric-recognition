package io.github.bdach.biometrics.algorithms.image.iris;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class IrisCodeGenerator {
    private static final List<Double> INNER = Arrays.asList(  0.0, 165.0, 195.0, 360.0);
    private static final List<Double> MID = Arrays.asList( 33.5, 146.5, 213.5, 326.5);
    private static final List<Double> OUTER = Arrays.asList( 45.0, 135.0, 225.0, 315.0);

    private static final List<List<Double>> RINGS = Arrays.asList(INNER, INNER, INNER, INNER, MID, MID, OUTER, OUTER);

    private static final int SAMPLES_PER_RING = 128;

    private final Image image;
    private final GaborWaveletTransform transformer;

    public boolean[] getCode() {
        double[][] rings = getRings();
        boolean[] code = new boolean[2 * SAMPLES_PER_RING * RINGS.size()];

        for (int i = 0; i < RINGS.size(); ++i) {
            double[] ring = rings[i];
            ComplexNumber[] transform = transformer.transform(ring, SAMPLES_PER_RING);
            for (int sampleIdx = 0; sampleIdx < transform.length; ++sampleIdx) {
                int baseIdx = 2 * (i * SAMPLES_PER_RING + sampleIdx);
                ComplexNumber sample = transform[sampleIdx];
                code[baseIdx] = sample.getImaginary() < 0;
                code[baseIdx + 1] = sample.getReal() < 0;
            }
        }
        return code;
    }

    public static Image getCodeVisualization(boolean[] code) {
        WritableImage image = new WritableImage(2 * SAMPLES_PER_RING, RINGS.size());
        PixelWriter pixelWriter = image.getPixelWriter();
        for (int i = 0; i < code.length; ++i) {
            int y = i / (2 * SAMPLES_PER_RING);
            int x = i % (2 * SAMPLES_PER_RING);
            if (!code[i]) {
                pixelWriter.setColor(x, y, Color.BLACK);
            } else {
                pixelWriter.setColor(x, y, Color.WHITE);
            }
        }
        return image;
    }

    private double[][] getRings() {
        double[][] rings = new double[RINGS.size()][];
        for (int i = 0; i < RINGS.size(); ++i) {
            rings[i] = getRing(RINGS.get(i), i);
        }
        return rings;
    }

    private double[] getRing(List<Double> ring, int ringNumber) {
        double width = image.getWidth();
        double height = image.getHeight();
        List<Double> samples = new ArrayList<>();

        PixelReader pixelReader = image.getPixelReader();

        int startY = (int) (height * ringNumber / RINGS.size());
        int endY = (int) (height * (1 + ringNumber) / RINGS.size());

        int startX = (int) (width * ring.get(0) / 360);
        int endX = (int) (width * ring.get(1) / 360);
        sampleRingPart(samples, pixelReader, startY, endY, startX, endX);

        startX = (int) (width * ring.get(2) / 360);
        endX = (int) (width * ring.get(3) / 360);
        sampleRingPart(samples, pixelReader, startY, endY, startX, endX);

        return samples.stream().mapToDouble(d -> d).toArray();
    }

    private void sampleRingPart(List<Double> samples, PixelReader pixelReader, int startY, int endY, int startX, int endX) {
        for (int x = startX; x < endX; ++x) {
            double sample = 0.0;
            for (int y = startY; y < endY; ++y) {
                sample += pixelReader.getColor(x, y).getRed();
            }
            sample /= endY - startY;
            samples.add(sample);
        }
    }
}