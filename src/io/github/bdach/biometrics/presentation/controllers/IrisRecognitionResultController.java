package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.presentation.ResultListCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.List;

public class IrisRecognitionResultController implements RecognitionResultController<IrisRecognitionResult> {
    @Setter
    private Stage primaryStage;

    @FXML public ListView<IrisRecognitionResult> comparisonResultListView;
    @FXML public ImageView originalImageView;
    @FXML public ImageView comparedImageView;
    @FXML public ImageView originalCodeImageView;
    @FXML public ImageView comparedCodeImageView;

    @FXML
    public void initialize() {
        comparisonResultListView.setCellFactory(ignored -> new ResultListCell<>());
        comparisonResultListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    comparedImageView.setImage(newValue.getRecord().getSourceImage());
                    comparedCodeImageView.setImage(newValue.getRecord().getCodeImage());
                }
        );
    }

    public void setResults(List<IrisRecognitionResult> results)
    {
        comparisonResultListView.setItems(FXCollections.observableList(results));
    }

    public void setRecognizedImage(Image recognizedImage) {
        originalImageView.setImage(recognizedImage);
    }

    public void setCodeImage(Image comparedCodeImage) {
        originalCodeImageView.setImage(comparedCodeImage);
    }
}
