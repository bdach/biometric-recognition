package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.Controller;
import io.github.bdach.biometrics.SettingChangeListener;
import io.github.bdach.biometrics.model.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

public class SettingsDialogController implements Controller {
    @Setter
    private Stage primaryStage;
    @Setter
    private SettingChangeListener listener;

    private Settings settings;

    @FXML
    private TextField gaborWaveletFrequencyTextField;

    @FXML
    public void initialize() {
        this.settings = Settings.getInstance();
        String f = Double.toString(settings.getGaborWaveletFrequency() / Math.PI);
        gaborWaveletFrequencyTextField.setText(f);
    }

    @FXML
    public void ok() {
        StringBuilder errors = new StringBuilder();

        try {
            double f = Double.parseDouble(gaborWaveletFrequencyTextField.getText());
            this.settings.setGaborWaveletFrequency(f * Math.PI);
        } catch (NumberFormatException ex) {
            errors.append("Invalid Gabor wavelet frequency.\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errors.toString());
            alert.showAndWait();
            return;
        }

        Settings.setInstance(this.settings, this.listener);
        primaryStage.close();
    }

    @FXML
    public void cancel() {
        primaryStage.close();
    }
}
