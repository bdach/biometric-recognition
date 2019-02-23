package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.model.IrisRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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

    @FXML
    public void create() {
        StringBuilder errorMessageBuilder = new StringBuilder();

        String title = titleTextField.getText();
        if (title == null || title.equals(""))
            errorMessageBuilder.append("The title cannot be empty.\n");
        if (chosenImage == null)
            errorMessageBuilder.append("Please select an iris image.\n");

        if (errorMessageBuilder.length() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMessageBuilder.toString(), ButtonType.OK);
            alert.showAndWait();
            return;
        }
        irisRecord = new IrisRecord(title, chosenImage);
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
