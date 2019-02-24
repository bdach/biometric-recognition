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
    @Getter
    private final boolean[] code;
    @Getter
    private final Image codeImage;

    public IrisRecord(String name, Image sourceImage, Image unwrappedImage, boolean[] code, Image codeImage) {
        super(name);
        this.sourceImage = sourceImage;
        this.unwrappedImage = unwrappedImage;
        this.code = code;
        this.codeImage = codeImage;
    }

    @Override
    public RecognitionType getType() {
        return RecognitionType.IRIS;
    }
}
