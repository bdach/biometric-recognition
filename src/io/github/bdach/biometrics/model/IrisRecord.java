package io.github.bdach.biometrics.model;

import javafx.scene.image.Image;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
public class IrisRecord extends Record {
    @Getter
    private final Image sourceImage;
    @Getter
    private final Image unwrappedImage;

    public IrisRecord(String name, Image sourceImage, Image unwrappedImage) {
        super(name);
        this.sourceImage = sourceImage;
        this.unwrappedImage = unwrappedImage;
    }

    @Override
    public RecognitionType getType() {
        return RecognitionType.IRIS;
    }
}
