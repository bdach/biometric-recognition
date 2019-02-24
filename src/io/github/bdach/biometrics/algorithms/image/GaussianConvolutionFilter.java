package io.github.bdach.biometrics.algorithms.image;

public class GaussianConvolutionFilter extends ConvolutionFilterImpl {
    public GaussianConvolutionFilter(double factor) {
        super(
                3,
                new double[][] {
                        new double[] {1, factor, 1},
                        new double[] {factor, factor * factor, factor},
                        new double[] {1, factor, 1}
                }
        );
    }

}
