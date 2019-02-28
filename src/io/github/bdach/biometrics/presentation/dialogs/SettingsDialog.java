package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.presentation.controllers.SettingsDialogController;

public class SettingsDialog extends Dialog<SettingsDialogController> {
    @Override
    protected String getResourceName() {
        return "../../views/settings.fxml";
    }

    @Override
    protected String getTitle() {
        return "Settings";
    }
}
