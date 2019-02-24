package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.algorithms.image.iris.IrisRecognitionTask;
import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.presentation.dialogs.TaskProgressDialog;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.File;

public class IrisRecordWizardController implements RecordWizardController<IrisRecord> {
    @Setter
    private Stage primaryStage;

    @FXML private TextField titleTextField;
    @FXML private Label imagePathLabel;
    @FXML private ImageView chosenImageView;

    private IrisRecord irisRecord;
    private Image chosenImage;

    @Override
    public IrisRecord get() {
        return irisRecord;
    }

    @FXML
    public void cancel() {
        primaryStage.close();
    }

    public String validate() {
        StringBuilder errorMessageBuilder = new StringBuilder();

        String title = titleTextField.getText();
        if (title == null || title.equals(""))
            errorMessageBuilder.append("The title cannot be empty.\n");
        if (chosenImage == null)
            errorMessageBuilder.append("Please select an iris image.\n");

        return errorMessageBuilder.toString();
    }

    public void processRecord() {
        Task<IrisRecord> task = new IrisRecognitionTask(titleTextField.getText(), chosenImage);
        TaskProgressDialog<IrisRecord> dialog = new TaskProgressDialog<>(task);
        dialog.showDialog(primaryStage);
        dialog.getResult().ifPresent(record -> irisRecord = record);
    }

    @FXML
    public void create() {
        String errors = validate();
        if (errors.length() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, errors, ButtonType.OK);
            alert.showAndWait();
            return;
        }
        processRecord();
        primaryStage.close();
    }

    @FXML
    public void browse() {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null)
            return;
        chosenImage = new Image(file.toURI().toString());
        imagePathLabel.setText(file.toString());
        chosenImageView.setImage(chosenImage);
    }
}
