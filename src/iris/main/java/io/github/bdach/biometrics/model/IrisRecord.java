package io.github.bdach.biometrics.model;

import javafx.scene.image.Image;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
public class IrisRecord extends Record {
    @Getter
    private final Image sourceImage;
    @Getter
    private final Image unwrappedImage;
    @Getter
    @Setter
    private boolean[][] codes;
    @Getter
    @Setter
    private Image codeImage;

    public IrisRecord(String name, Image sourceImage, Image unwrappedImage, boolean[][] codes, Image codeImage) {
        super(name);
        this.sourceImage = sourceImage;
        this.unwrappedImage = unwrappedImage;
        this.codes = codes;
        this.codeImage = codeImage;
    }

    @Override
    public RecognitionType getType() {
        return RecognitionType.IRIS;
    }
}
