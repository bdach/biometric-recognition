package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.presentation.controllers.IrisRecognitionResultController;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class IrisRecognitionResultDialog
        extends RecognitionResultDialog<IrisRecord, IrisRecognitionResult, IrisRecognitionResultController> {
    private Image recognizedImage;

    @Override
    public List<IrisRecognitionResult> runRecognition(List<IrisRecord> irisRecords) {
        FileChooser chooser = new FileChooser();
        File imageFile = chooser.showOpenDialog(parentStage);
        if (imageFile == null)
            return null;
        recognizedImage = new Image(imageFile.toURI().toString());

        return irisRecords.stream()
                .map(record -> new IrisRecognitionResult(record, 0))
                .collect(Collectors.toList());
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
