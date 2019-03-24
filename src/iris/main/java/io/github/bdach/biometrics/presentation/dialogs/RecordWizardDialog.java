package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.model.RecognitionType;
import io.github.bdach.biometrics.model.Record;
import io.github.bdach.biometrics.presentation.controllers.RecordWizardController;
import javafx.stage.Stage;

public abstract class RecordWizardDialog<TRecord extends Record> extends Dialog<RecordWizardController<TRecord>> {
    public TRecord create(Stage parentStage) {
        showDialog(parentStage);
        return controller.get();
    }

    public static Record create(Stage parentStage, RecognitionType type) {
        switch (type) {
            case IRIS:
                return new IrisRecordWizardDialog().create(parentStage);
            default:
                throw new IllegalArgumentException("type");
        }
    }
}
