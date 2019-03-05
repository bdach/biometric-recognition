package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.IrisRecord;
import javafx.scene.image.Image;

public class IrisRecordTask extends IrisProcessingTask<IrisRecord> {
    private final String title;

    public IrisRecordTask(String title, Image image) {
        super(image);
        this.title = title;
    }

    @Override
    protected IrisRecord process(boolean[][] codes) {
        return new IrisRecord(
                title,
                image,
                unwrappedImage,
                codes,
                IrisCodeGenerator.getCodeVisualization(codes[4])
        );
    }
}
