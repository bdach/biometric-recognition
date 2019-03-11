package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.algorithms.image.iris.BulkIrisRecognitionTask;
import io.github.bdach.biometrics.model.IrisRecognitionResults;
import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.model.Record;
import io.github.bdach.biometrics.presentation.controllers.BulkIrisRecognitionViewController;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class BulkIrisRecognitionResultDialog extends Dialog<BulkIrisRecognitionViewController> {
    private List<IrisRecognitionResults> resultMatrix;

    @Override
    protected String getResourceName() {
        return "../../views/bulk_iris_recognition.fxml";
    }

    @Override
    protected String getTitle() {
        return "Iris recognition - Hamming distance spreadsheet";
    }

    public void recognizeBulk(Stage primaryStage, List<IrisRecord> irisRecords) {
        FileChooser fileChooser = new FileChooser();
        List<File> imageFiles = fileChooser.showOpenMultipleDialog(primaryStage);
        if (imageFiles == null || imageFiles.size() == 0)
            return;
        BulkIrisRecognitionTask task = new BulkIrisRecognitionTask(irisRecords, imageFiles);
        TaskProgressDialog<List<IrisRecognitionResults>> progressDialog = new TaskProgressDialog<>(task);
        progressDialog.showDialog(primaryStage);
        Optional<List<IrisRecognitionResults>> result = progressDialog.getResult();
        if (!result.isPresent())
            return;
        resultMatrix = result.get();
        showDialog(primaryStage);
    }

    @Override
    protected void setUpDialog() {
        super.setUpDialog();
        controller.setResultMatrix(resultMatrix);
    }
}
