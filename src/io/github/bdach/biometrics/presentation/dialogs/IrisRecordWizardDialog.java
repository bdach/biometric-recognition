package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.model.IrisRecord;

public class IrisRecordWizardDialog extends RecordWizardDialog<IrisRecord> {
    @Override
    protected String getResourceName() {
        return "../../views/iris_record_wizard.fxml";
    }

    @Override
    protected String getTitle() {
        return "Create iris record...";
    }
}
