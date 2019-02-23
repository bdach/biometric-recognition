package io.github.bdach.biometrics.model;

import javafx.scene.image.Image;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class IrisRecord extends Record {
    @Getter
    private final Image sourceImage;

    public IrisRecord(String name) {
        this(name, null);
    }

    public IrisRecord(String name, Image image) {
        super(name);
        sourceImage = image;
    }

    @Override
    public RecognitionType getType() {
        return RecognitionType.IRIS;
    }
}
