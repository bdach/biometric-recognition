package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.algorithms.image.iris.BulkIrisRecordTask;
import io.github.bdach.biometrics.model.IrisRecord;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BulkIrisRecordWizard {
    public static IrisRecord[] bulkImport(Stage parentStage) {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(parentStage);
        BulkIrisRecordTask task = new BulkIrisRecordTask(files);
        TaskProgressDialog<IrisRecord[]> dialog = new TaskProgressDialog<>(task);
        dialog.showDialog(parentStage);
        return dialog.getResult().orElse(null);
    }
}
