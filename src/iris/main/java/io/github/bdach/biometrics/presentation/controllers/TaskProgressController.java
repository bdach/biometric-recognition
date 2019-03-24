package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.Controller;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import lombok.Setter;

public class TaskProgressController<T> implements Controller {
    @Setter
    private Stage primaryStage;

    @FXML public Label statusLabel;
    @FXML public ProgressBar progressBar;

    public void bindTask(Task<T> task) {
        statusLabel.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());
    }
}
