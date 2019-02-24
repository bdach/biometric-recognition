package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.algorithms.image.iris.IrisRecognitionTask;
import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecognitionResults;
import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.presentation.controllers.IrisRecognitionResultController;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class IrisRecognitionResultDialog
        extends RecognitionResultDialog<IrisRecord, IrisRecognitionResult, IrisRecognitionResultController> {
    private Image recognizedImage;
    private Image codeImage;

    public IrisRecognitionResults process(List<IrisRecord> irisRecords) {
        IrisRecognitionTask task = new IrisRecognitionTask(recognizedImage, irisRecords);
        TaskProgressDialog<IrisRecognitionResults> dialog = new TaskProgressDialog<>(task);
        dialog.showDialog(stage);
        Optional<IrisRecognitionResults> result = dialog.getResult();
        return result.orElse(null);
    }

    @Override
    public List<IrisRecognitionResult> runRecognition(List<IrisRecord> irisRecords) {
        FileChooser chooser = new FileChooser();
        File imageFile = chooser.showOpenDialog(parentStage);
        if (imageFile == null)
            return null;
        recognizedImage = new Image(imageFile.toURI().toString());
        IrisRecognitionResults results = process(irisRecords);
        codeImage = results.getComparedCodeImage();
        return results.getResults();
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
        controller.setCodeImage(codeImage);
    }
}
