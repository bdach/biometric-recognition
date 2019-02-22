package io.github.bdach.biometrics.presentation;

import io.github.bdach.biometrics.model.Record;
import io.github.bdach.biometrics.presentation.controllers.RecordViewController;
import javafx.scene.control.ListCell;

public class RecordListCell extends ListCell<Record> {
    @Override
    public void updateItem(Record item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null)
            return;
        RecordViewController controller = new RecordViewController();
        controller.setRecord(item);
        setGraphic(controller.getBox());
    }
}
