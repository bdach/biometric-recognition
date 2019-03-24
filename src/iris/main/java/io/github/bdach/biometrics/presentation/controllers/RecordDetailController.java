package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.model.IrisRecord;
import io.github.bdach.biometrics.model.Record;
import javafx.scene.Node;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RecordDetailController<TRecord extends Record> {
    protected final TRecord record;

    public abstract Node getMainPane();

    public static RecordDetailController<? extends Record> create(Record record) {
        if (record == null)
            return null;

        switch (record.getType()) {
            case IRIS:
                IrisRecord irisRecord = (IrisRecord) record;
                return new IrisRecordDetailController(irisRecord);
            default:
                throw new IllegalArgumentException("record.getType()");
        }
    }
}
