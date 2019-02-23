package io.github.bdach.biometrics;

import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.model.RecognitionType;
import io.github.bdach.biometrics.model.Record;
import io.github.bdach.biometrics.presentation.RecordListCell;
import io.github.bdach.biometrics.presentation.controllers.RecordDetailController;
import io.github.bdach.biometrics.presentation.dialogs.RecordWizardDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;

public class MainController implements Controller {
    @Setter
    private Stage primaryStage;

    @FXML BorderPane mainBorderPane;
    @FXML private ListView<Record> recordListView;
    @FXML private Button addButton;
    @FXML private ComboBox<RecognitionType> typeChoiceBox;

    @FXML
    private void add() {
        Record newRecord = RecordWizardDialog.create(primaryStage, typeChoiceBox.getValue());
        if (newRecord != null)
            recordListView.getItems().add(newRecord);
    }

    private void setUpRecordList() {
        ObservableList<Record> collection = FXCollections.observableArrayList(
                new IrisRecord("abc"),
                new IrisRecord("def"),
                new IrisRecord("ghi")
        );
        recordListView.setCellFactory(v -> new RecordListCell());
        recordListView.setItems(collection);
        recordListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldVal, newVal) -> {
                    RecordDetailController<? extends Record> controller = RecordDetailController.create(newVal);
                    mainBorderPane.setRight(controller.getMainPane());
                }
        );
    }

    @FXML
    public void initialize()
    {
        setUpRecordList();
    }
}
