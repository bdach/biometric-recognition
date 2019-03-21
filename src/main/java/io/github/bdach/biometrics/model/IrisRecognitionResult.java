package io.github.bdach.biometrics.model;

import lombok.Getter;

public class IrisRecognitionResult extends RecognitionResult<IrisRecord> implements Comparable<IrisRecognitionResult> {
    @Getter
    private final int hammingDistance;

    public IrisRecognitionResult(IrisRecord record, int hammingDistance) {
        super(record);
        this.hammingDistance = hammingDistance;
    }

    @Override
    public int compareTo(IrisRecognitionResult o) {
        return Integer.compare(this.hammingDistance, o.hammingDistance);
    }

    @Override
    public int getScore() {
        return hammingDistance;
    }
}
