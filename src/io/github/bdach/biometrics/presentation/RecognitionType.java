package io.github.bdach.biometrics.presentation;

public enum RecognitionType {
    IRIS("Iris"),
    VOICE("Voice"),
    FACE("Face");

    private String label;

    RecognitionType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
