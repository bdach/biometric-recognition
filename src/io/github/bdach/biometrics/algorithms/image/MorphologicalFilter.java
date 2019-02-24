package io.github.bdach.biometrics.algorithms.image;

public abstract class MorphologicalFilter extends ConvolutionFilter {
    protected final int elementSize;
    protected final boolean[][] structuringElement;

    protected MorphologicalFilter(int elementSize, boolean[][] structuringElement) {
        this.elementSize = elementSize;
        this.structuringElement = new boolean[elementSize][];
        for (int y = 0; y < elementSize; ++y) {
            this.structuringElement[y] = new boolean[elementSize];
            System.arraycopy(structuringElement[y], 0, this.structuringElement[y], 0, elementSize);
        }
    }

    protected MorphologicalFilter(int elementSize) {
        this.elementSize = elementSize;
        this.structuringElement = new boolean[elementSize][elementSize];
        for (int y = 0; y < elementSize; ++y) {
            for (int x = 0; x < elementSize; ++x) {
                structuringElement[y][x] = true;
            }
        }
    }

    @Override
    public int getSize() {
        return elementSize;
    }
}
