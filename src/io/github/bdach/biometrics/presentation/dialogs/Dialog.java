package io.github.bdach.biometrics.presentation.dialogs;

import io.github.bdach.biometrics.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Dialog<TController extends Controller> {
    protected abstract String getResourceName();
    protected abstract String getTitle();

    protected TController controller;
    protected Stage stage;

    protected void setUpDialog() { }

    protected void showDialog(Stage parentStage) {
        stage = new Stage();
        String resourceName = getResourceName();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(RecordWizardDialog.class.getResource(resourceName));
            root = loader.load();
            controller = loader.getController();
            controller.setPrimaryStage(stage);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        stage.setScene(new Scene(root));
        stage.setTitle(getTitle());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        setUpDialog();
        stage.showAndWait();
    }
}
