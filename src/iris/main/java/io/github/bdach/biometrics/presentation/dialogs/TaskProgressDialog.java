package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.presentation.controllers.TaskProgressController;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@RequiredArgsConstructor
public class TaskProgressDialog<T> extends Dialog<TaskProgressController<T>> {
    private final Task<T> task;
    @Getter
    @Setter
    private String title = "Operation in progress...";

    @Override
    protected String getResourceName() {
        return "/task_progress.fxml";
    }

    @Override
    public void setUpDialog() {
        task.setOnSucceeded(event -> stage.close());
        task.setOnFailed(event -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, task.getException().getLocalizedMessage(), ButtonType.OK);
            alert.showAndWait();
            stage.close();
        });
        controller.bindTask(task);
        Thread thread = new Thread(task);
        thread.start();
    }

    public Optional<T> getResult() {
        if (task.getState() == Worker.State.SUCCEEDED) {
            return Optional.of(task.getValue());
        } else {
            return Optional.empty();
        }
    }
}
