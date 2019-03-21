package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecognitionResults;
import io.github.bdach.biometrics.model.IrisRecord;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BulkIrisRecognitionTask extends Task<List<IrisRecognitionResults>> {
    private final List<IrisRecord> databaseRecords;
    private final List<File> comparedImageFiles;

    @Override
    protected List<IrisRecognitionResults> call() throws Exception {
        ArrayList<IrisRecognitionResults> results = new ArrayList<>();
        updateProgress(0, comparedImageFiles.size());
        for (File file : comparedImageFiles) {
            Image image = new Image(file.toURI().toString());
            String fileName = file.getName();
            updateMessage(fileName);
            IrisRecognitionTask subtask = new IrisRecognitionTask(image, fileName, databaseRecords);
            IrisRecognitionResults result = subtask.call();
            if (isCancelled())
                return null;
            results.add(result);
            updateProgress(results.size(), comparedImageFiles.size());
        }
        return results;
    }
}
