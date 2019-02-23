package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.presentation.controllers.IrisRecognitionResultController;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IrisRecognitionResultDialog
        extends RecognitionResultDialog<IrisRecord, IrisRecognitionResult, IrisRecognitionResultController> {
    private Image recognizedImage;

    public List<IrisRecognitionResult> process(List<IrisRecord> irisRecords) {
        Task<List<IrisRecognitionResult>> task = new Task<List<IrisRecognitionResult>>() {
            @Override
            protected List<IrisRecognitionResult> call() throws Exception {
                updateProgress(0, 1200);
                updateMessage("Xyzzying the abcdef...");
                Thread.sleep(500);
                updateProgress(500, 1200);
                updateMessage("Fooing the bar...");
                Thread.sleep(700);
                updateProgress(1200, 1200);
                return irisRecords.stream()
                        .map(record -> new IrisRecognitionResult(record, 0))
                        .collect(Collectors.toList());
            }
        };
        TaskProgressDialog<List<IrisRecognitionResult>> dialog = new TaskProgressDialog<>(task);
        dialog.showDialog(stage);
        Optional<List<IrisRecognitionResult>> result = dialog.getResult();
        return result.orElse(Collections.emptyList());
    }

    @Override
    public List<IrisRecognitionResult> runRecognition(List<IrisRecord> irisRecords) {
        FileChooser chooser = new FileChooser();
        File imageFile = chooser.showOpenDialog(parentStage);
        if (imageFile == null)
            return null;
        recognizedImage = new Image(imageFile.toURI().toString());

        return process(irisRecords);
    }

    @Override
    protected String getResourceName() {
        return "../../views/iris_recognition_result.fxml";
    }

    @Override
    protected String getTitle() {
        return "Iris recognition results";
    }

    @Override
    public void setUpDialog() {
        super.setUpDialog();
        controller.setRecognizedImage(recognizedImage);
    }
}
