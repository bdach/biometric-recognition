package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.model.IrisRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class IrisRecordDetailController extends RecordDetailController<IrisRecord> {
    @Getter
    @FXML private VBox mainPane;
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private ImageView irisImageView;
    @FXML private ImageView unwrappedIrisImageView;
    @FXML private ImageView codeImageView;

    public IrisRecordDetailController(IrisRecord record) {
        super(record);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../views/iris_record_detail.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize() {
        titleLabel.setText(record.getName());
        dateLabel.setText(record.getCreationDate()
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        irisImageView.setImage(record.getSourceImage());
        unwrappedIrisImageView.setImage(record.getUnwrappedImage());
        codeImageView.setImage(record.getCodeImage());
    }
}
