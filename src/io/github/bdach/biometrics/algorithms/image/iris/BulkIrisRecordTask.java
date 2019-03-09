package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.IrisRecord;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BulkIrisRecordTask extends Task<IrisRecord[]> {
    private final List<File> files;

    @Override
    protected IrisRecord[] call() throws Exception {
        updateProgress(0, files.size());
        ArrayList<IrisRecord> results = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            updateMessage(name);
            Image image = new Image(file.toURI().toString());
            IrisRecordTask subTask = new IrisRecordTask(name, image);
            IrisRecord record = subTask.call();
            results.add(record);
            if (isCancelled())
                return null;
            updateProgress(results.size(), files.size());
        }
        return results.toArray(new IrisRecord[files.size()]);
    }
}
