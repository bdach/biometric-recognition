package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.model.Record;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class RecordViewController {
    @Getter
    @FXML private VBox box;
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;

    private Record record;

    public RecordViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/record.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setRecord(Record record) {
        this.record = record;
        titleLabel.setText(record.getName());
        dateLabel.setText(record.getCreationDate()
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
    }
}
