package io.github.bdach.biometrics;

import io.github.bdach.biometrics.algorithms.image.iris.IrisCodeRecalculationTask;
import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.model.RecognitionType;
import io.github.bdach.biometrics.model.Record;
import io.github.bdach.biometrics.presentation.RecordListCell;
import io.github.bdach.biometrics.presentation.controllers.RecordDetailController;
import io.github.bdach.biometrics.presentation.dialogs.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class MainController implements Controller, SettingChangeListener {
    @Setter
    private Stage primaryStage;

    @FXML BorderPane mainBorderPane;
    @FXML private ListView<Record> recordListView;
    @FXML private Button addButton;
    @FXML private ComboBox<RecognitionType> typeChoiceBox;

    private ObservableList<Record> irisRecords = FXCollections.observableArrayList();

    @FXML
    private void add() {
        switch (typeChoiceBox.getValue()) {
            case IRIS:
                IrisRecord newRecord = new IrisRecordWizardDialog().create(primaryStage);
                if (newRecord != null)
                    irisRecords.add(newRecord);
                break;
        }
    }

    @FXML
    public void recognize() {
        switch (typeChoiceBox.getValue()) {
            case IRIS:
                IrisRecognitionResultDialog dialog = new IrisRecognitionResultDialog();
                List<IrisRecord> irisRecords = getIrisRecords();
                dialog.recognize(primaryStage, irisRecords);
                break;
        }
    }

    @FXML
    public void bulkImport() {
        switch (typeChoiceBox.getValue()) {
            case IRIS:
                IrisRecord[] newRecords = BulkIrisRecordWizard.bulkImport(primaryStage);
                if (newRecords != null)
                    irisRecords.addAll(newRecords);
                break;
        }
    }

    @FXML
    public void delete() {
        ObservableList<Record> selectedItems = recordListView.getSelectionModel().getSelectedItems();
        recordListView.getItems().removeAll(selectedItems);
    }

    @FXML
    public void bulkRecognize() {
        switch (typeChoiceBox.getValue()) {
            case IRIS:
                BulkIrisRecognitionResultDialog dialog = new BulkIrisRecognitionResultDialog();
                List<IrisRecord> irisRecords = getIrisRecords();
                dialog.recognizeBulk(primaryStage, irisRecords);
                break;
        }
    }

    @FXML
    public void settings() {
        SettingsDialog dialog = new SettingsDialog(this);
        dialog.showDialog(primaryStage);
    }

    private void setUpRecordList() {
        recordListView.setCellFactory(v -> new RecordListCell());
        recordListView.setItems(irisRecords);
        recordListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldVal, newVal) -> {
                    RecordDetailController<? extends Record> controller = RecordDetailController.create(newVal);
                    Node mainPane = controller != null ? controller.getMainPane() : null;
                    mainBorderPane.setRight(mainPane);
                }
        );
    }

    @FXML
    public void initialize()
    {
        setUpRecordList();
    }

    @Override
    public void onGaborWaveletFrequencyChanged() {
        List<IrisRecord> records = getIrisRecords();
        IrisCodeRecalculationTask task = new IrisCodeRecalculationTask(records);
        TaskProgressDialog<Void> dialog = new TaskProgressDialog<>(task);
        dialog.setTitle("Recalculating iris codes...");
        dialog.showDialog(primaryStage);
        recordListView.getSelectionModel().clearSelection();
    }

    private List<IrisRecord> getIrisRecords() {
        return this.irisRecords.stream()
                .map(IrisRecord.class::cast)
                .collect(Collectors.toList());
    }
}
