package io.github.bdach.biometrics.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RecognitionResult<TRecord extends Record> {
    @Getter
    protected final TRecord record;

    public abstract int getScore();
}
