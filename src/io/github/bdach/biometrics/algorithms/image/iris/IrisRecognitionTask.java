package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecognitionResults;
import io.github.bdach.biometrics.model.IrisRecord;
import javafx.scene.image.Image;

import java.util.List;
import java.util.stream.Collectors;

public class IrisRecognitionTask extends IrisProcessingTask<IrisRecognitionResults> {
    private final List<IrisRecord> records;

    public IrisRecognitionTask(Image image, List<IrisRecord> records) {
        super(image);
        this.records = records;
    }

    @Override
    protected IrisRecognitionResults process(boolean[] code) {
        List<IrisRecognitionResult> resultList = records.stream()
                .map(record -> compare(record, code))
                .sorted()
                .collect(Collectors.toList());
        Image codeVisualization = IrisCodeGenerator.getCodeVisualization(code);
        return new IrisRecognitionResults(codeVisualization, resultList);
    }

    private IrisRecognitionResult compare(IrisRecord record, boolean[] code) {
        int distance = calculateHammingDistance(record.getCode(), code);
        return new IrisRecognitionResult(record, distance);
    }

    private int calculateHammingDistance(boolean[] first, boolean[] second) {
        int distance = 0;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                distance += 1;
        }
        return distance;
    }
}
