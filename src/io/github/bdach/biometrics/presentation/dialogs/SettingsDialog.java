package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.SettingChangeListener;
import io.github.bdach.biometrics.presentation.controllers.SettingsDialogController;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SettingsDialog extends Dialog<SettingsDialogController> {
    private final SettingChangeListener listener;

    @Override
    protected String getResourceName() {
        return "../../views/settings.fxml";
    }

    @Override
    protected String getTitle() {
        return "Settings";
    }

    @Override
    protected void setUpDialog() {
        super.setUpDialog();
        controller.setListener(listener);
    }
}
