package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.IrisRecord;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IrisRecognitionTask extends Task<IrisRecord> {
    private final String title;
    private final Image image;

    @Override
    protected IrisRecord call() throws Exception {
        updateMessage("Locating iris...");
        updateProgress(0, 2);
        IrisLocation location = IrisLocator.locate(image);
        if (isCancelled())
            return null;

        updateMessage("Unwrapping iris...");
        updateProgress(1, 2);
        IrisUnwrapper unwrapper = new IrisUnwrapper(image, location);
        Image unwrapped = unwrapper.unwrap();
        if (isCancelled())
            return null;

        updateMessage("Done.");
        updateProgress(2, 2);
        return new IrisRecord(title, image, unwrapped);
    }
}
