package io.github.bdach.biometrics;

import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.model.Record;
import io.github.bdach.biometrics.presentation.RecordListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class Controller {
    @FXML
    private ListView<Record> recordListView;

    private void setUpRecordList() {
        ObservableList<Record> collection = FXCollections.observableArrayList(
                new IrisRecord("abc"),
                new IrisRecord("def"),
                new IrisRecord("ghi")
        );
        recordListView.setCellFactory(v -> new RecordListCell());
        recordListView.setItems(collection);
    }

    @FXML
    public void initialize() {
        setUpRecordList();
    }
}
