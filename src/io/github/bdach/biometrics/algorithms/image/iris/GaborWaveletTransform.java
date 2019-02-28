package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.Settings;

public class GaborWaveletTransform {
    private final double f;
    private final double sigma;

    public GaborWaveletTransform() {
        this.f = Settings.getInstance().getGaborWaveletFrequency();
        this.sigma = 0.5 * Math.PI * f;
    }

    public ComplexNumber[] transform(double[] data, int sampleCount) {
        double[] samples = prepareSamples(data, sampleCount);
        ComplexNumber[] coefficients = new ComplexNumber[sampleCount];

        for (int k = 0; k < sampleCount; ++k) {
            ComplexNumber result = new ComplexNumber();
            for (int i = 0; i < sampleCount; ++i) {
                double sample = samples[i];
                double expTerm = Math.exp(-Math.pow(i - k, 2) / sigma);
                double complexArg = -2 * Math.PI * f * i;
                double realTerm = Math.cos(complexArg);
                double imaginaryTerm = Math.sin(complexArg);

                result.addRealPart(sample * expTerm * realTerm);
                result.addImaginaryPart(sample * expTerm * imaginaryTerm);
            }
            coefficients[k] = result;
        }
        return coefficients;
    }

    private double[] prepareSamples(double[] data, int sampleCount) {
        double[] samples = new double[sampleCount];
        for (int i = 0; i < sampleCount; ++i) {
            double targetIndex = (double) data.length * i / sampleCount;
            int floor = (int) Math.floor(targetIndex);
            double frac = targetIndex - floor;

            double sample;
            if (Math.abs(frac) > 1e-2) {
                sample = (1 - frac) * data[floor] + frac * data[floor + 1];
            } else {
                sample = data[floor];
            }
            samples[i] = sample;
        }
        return samples;
    }
}
