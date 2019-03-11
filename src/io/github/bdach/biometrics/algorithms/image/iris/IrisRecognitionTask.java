package io.github.bdach.biometrics.algorithms.image.iris;

import io.github.bdach.biometrics.model.IrisRecognitionResult;
import io.github.bdach.biometrics.model.IrisRecognitionResults;
import io.github.bdach.biometrics.model.IrisRecord;
import javafx.scene.image.Image;

import java.util.List;
import java.util.stream.Collectors;

public class IrisRecognitionTask extends IrisProcessingTask<IrisRecognitionResults> {
    private final List<IrisRecord> records;
    private final String imageFilename;

    public IrisRecognitionTask(Image image, String imageFilename, List<IrisRecord> records) {
        super(image);
        this.records = records;
        this.imageFilename = imageFilename;
    }

    @Override
    protected IrisRecognitionResults process(boolean[][] codes) {
        List<IrisRecognitionResult> resultList = records.stream()
                .map(record -> compare(record, codes))
                .collect(Collectors.toList());
        Image codeVisualization = IrisCodeGenerator.getCodeVisualization(codes[4]);
        return new IrisRecognitionResults(codeVisualization, imageFilename, resultList);
    }

    private IrisRecognitionResult compare(IrisRecord record, boolean[][] codes) {
        int distance = compare(record.getCodes(), codes);
        return new IrisRecognitionResult(record, distance);
    }

    private int compare(boolean[][] firstCodes, boolean[][] secondCodes) {
        int bestDistance = Integer.MAX_VALUE;
        for (boolean[] firstCode : firstCodes) {
            for (boolean[] secondCode : secondCodes) {
                int distance = calculateHammingDistance(firstCode, secondCode);
                bestDistance = Math.min(bestDistance, distance);
            }
        }
        return bestDistance;
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
