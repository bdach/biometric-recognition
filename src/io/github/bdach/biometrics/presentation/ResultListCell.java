package io.github.bdach.biometrics.presentation;

import io.github.bdach.biometrics.model.RecognitionResult;
import io.github.bdach.biometrics.model.Record;
import io.github.bdach.biometrics.presentation.controllers.ResultDetailController;
import javafx.scene.control.ListCell;

public class ResultListCell<TRecord extends Record, TResult extends RecognitionResult<TRecord>> extends ListCell<TResult> {
    @Override
    public void updateItem(TResult item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setGraphic(null);
            return;
        }
        ResultDetailController<TRecord> controller = new ResultDetailController<>();
        controller.setResult(item);
        setGraphic(controller.getBox());
    }
}
