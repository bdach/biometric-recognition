package io.github.bdach.biometrics.presentation.controllers;

import io.github.bdach.biometrics.Controller;
import io.github.bdach.biometrics.model.Record;

public interface RecordWizardController<TRecord extends Record> extends Controller {
    TRecord get();
}
