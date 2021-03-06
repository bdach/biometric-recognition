package io.github.bdach.biometrics.algorithms.image.iris;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class IrisProcessingTask<T> extends Task<T> {
    protected final Image image;

    protected Image unwrappedImage;

    protected abstract T process(boolean[][] codes);

    @Override
    protected T call() throws Exception {
        updateMessage("Locating iris...");
        updateProgress(0, 4);
        IrisLocation location = IrisLocator.locate(image);
        if (isCancelled())
            return null;

        updateMessage("Unwrapping iris...");
        updateProgress(1, 4);
        IrisUnwrapper unwrapper = new IrisUnwrapper(image, location);
        unwrappedImage = unwrapper.unwrap();
        if (isCancelled())
            return null;

        updateMessage("Generating code...");
        updateProgress(2, 4);
        GaborWaveletTransform transformer = new GaborWaveletTransform();
        IrisCodeGenerator generator = new IrisCodeGenerator(unwrappedImage, transformer);
        boolean[][] codes = generator.getCodes();

        updateMessage("Processing code...");
        updateProgress(3, 4);
        T result = process(codes);

        updateMessage("Done.");
        updateProgress(4, 4);
        return result;
    }
}
