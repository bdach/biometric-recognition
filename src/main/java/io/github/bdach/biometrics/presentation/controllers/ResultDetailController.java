package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.model.RecognitionResult;
import io.github.bdach.biometrics.model.Record;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import lombok.Getter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ResultDetailController<TRecord extends Record> {
    @Getter
    @FXML private BorderPane box;
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label resultLabel;

    private RecognitionResult<TRecord> result;

    public ResultDetailController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/result_detail.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setResult(RecognitionResult<TRecord> result) {
        this.result = result;
        titleLabel.setText(result.getRecord().getName());
        dateLabel.setText(result.getRecord().getCreationDate()
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        resultLabel.setText(Integer.toString(result.getScore()));
    }
}
