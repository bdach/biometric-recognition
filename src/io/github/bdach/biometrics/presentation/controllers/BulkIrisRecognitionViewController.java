package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.Controller;
import io.github.bdach.biometrics.algorithms.image.iris.IrisRecognitionMatrixExport;
import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecognitionResults;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import org.controlsfx.control.spreadsheet.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class BulkIrisRecognitionViewController implements Controller {
    @Setter
    private Stage primaryStage;

    @FXML public SpreadsheetView recognitionResultSpreadsheetView;

    private List<IrisRecognitionResults> resultMatrix;

    @FXML
    public void exportToCsv() {
        FileChooser fileChooser = new FileChooser();
        File targetFile = fileChooser.showSaveDialog(primaryStage);
        IrisRecognitionMatrixExport export = new IrisRecognitionMatrixExport(targetFile, resultMatrix);
        export.export();
        new Alert(Alert.AlertType.INFORMATION, String.format("Data successfully exported to %s", targetFile), ButtonType.OK);
    }

    @FXML
    public void ok() {
        primaryStage.close();
    }

    public void setResultMatrix(List<IrisRecognitionResults> resultMatrix) {
        this.resultMatrix = resultMatrix;
        ObservableList<String> rowHeaders = resultMatrix.stream()
                .map(IrisRecognitionResults::getComparedImageFilename)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        ObservableList<String> columnHeaders = resultMatrix.get(0)
                .getResults()
                .stream()
                .map(row -> row.getRecord().getName())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        Grid grid = new GridBase(rowHeaders.size(), columnHeaders.size());

        ObservableList<ObservableList<SpreadsheetCell>> cells = FXCollections.observableArrayList();

        for (int rowIdx = 0; rowIdx < resultMatrix.size(); ++rowIdx) {
            IrisRecognitionResults row = resultMatrix.get(rowIdx);
            ObservableList<SpreadsheetCell> rowCells = FXCollections.observableArrayList();

            List<IrisRecognitionResult> results = row.getResults();
            int bestColIdx = 0;
            int bestScore = Integer.MAX_VALUE;
            for (int colIdx = 0; colIdx < results.size(); ++colIdx) {
                int score = results.get(colIdx).getScore();
                SpreadsheetCell cell = SpreadsheetCellType.INTEGER.createCell(rowIdx, colIdx, 0, 0, score);
                if (score < bestScore) {
                    bestScore = score;
                    bestColIdx = colIdx;
                }
                rowCells.add(cell);
            }
            rowCells.get(bestColIdx).getStyleClass().add("best-score");
            cells.add(rowCells);
        }
        grid.getColumnHeaders().setAll(columnHeaders);
        grid.getRowHeaders().setAll(rowHeaders);
        grid.setRows(cells);
        recognitionResultSpreadsheetView.setGrid(grid);
    }
}
