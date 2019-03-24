package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.Controller;
import io.github.bdach.biometrics.model.RecognitionResult;
import io.github.bdach.biometrics.model.Record;

import java.util.List;

public interface RecognitionResultController<TResult extends RecognitionResult> extends Controller {
    void setResults(List<TResult> results);
}
