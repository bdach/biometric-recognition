package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.model.RecognitionResult;
import io.github.bdach.biometrics.model.Record;
import io.github.bdach.biometrics.presentation.controllers.RecognitionResultController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.List;

public abstract class RecognitionResultDialog<TRecord extends Record, TResult extends RecognitionResult<TRecord>,
        TController extends RecognitionResultController<TResult>>
        extends Dialog<TController>
{
    protected List<TResult> results;
    protected Stage parentStage;

    protected abstract List<TResult> runRecognition(List<TRecord> records);

    public void recognize(Stage parentStage, List<TRecord> records) {
        if (records.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No iris records in database.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        this.parentStage = parentStage;
        results = runRecognition(records);
        if (results == null)
            return;
        showDialog(parentStage);
    }

    @Override
    public void setUpDialog()
    {
        controller.setResults(results);
    }
}
