package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.IrisRecord;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class IrisCodeRecalculationTask extends Task<Void> {
    private final List<IrisRecord> records;

    @Override
    protected Void call() throws Exception {
        updateProgress(0, records.size());
        GaborWaveletTransform transformer = new GaborWaveletTransform();
        int done = 0;
        for (IrisRecord record : records) {
            updateMessage(record.getName());
            IrisCodeGenerator generator = new IrisCodeGenerator(record.getUnwrappedImage(), transformer);
            boolean[][] newCodes = generator.getCodes();
            record.setCodes(newCodes);
            Image newVisualization = IrisCodeGenerator.getCodeVisualization(newCodes[4]);
            record.setCodeImage(newVisualization);
            if (isCancelled())
                return null;
            done += 1;
            updateProgress(done, records.size());
        }
        return null;
    }
}
