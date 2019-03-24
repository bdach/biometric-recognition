package io.github.bdach.biometrics.model;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class IrisRecognitionResults {
    private final Image comparedCodeImage;
    private final String comparedImageFilename;
    private final List<IrisRecognitionResult> results;

    public void sort() {
        results.sort(IrisRecognitionResult::compareTo);
    }
}
