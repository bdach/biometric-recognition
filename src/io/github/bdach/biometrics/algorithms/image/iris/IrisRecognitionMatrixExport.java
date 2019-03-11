package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecognitionResults;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class IrisRecognitionMatrixExport {
    private final File targetFile;
    private final List<IrisRecognitionResults> resultGrid;

    public void export() {
        try {
            targetFile.createNewFile();
            PrintWriter writer = new PrintWriter(targetFile);
            String header = String.join(
                    ",",
                    resultGrid.get(0)
                        .getResults()
                        .stream()
                        .map(result -> result.getRecord().getName())
                        .collect(Collectors.toList())
            );
            writer.println("," + header);

            for (IrisRecognitionResults row : resultGrid) {
                List<String> rowContent = row.getResults()
                        .stream()
                        .map(result -> Integer.toString(result.getScore()))
                        .collect(Collectors.toList());
                rowContent.add(0, row.getComparedImageFilename());
                String rowString = String.join(",", rowContent);
                writer.println(rowString);
            }
            writer.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
