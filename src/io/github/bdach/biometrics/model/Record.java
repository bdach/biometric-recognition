package io.github.bdach.biometrics.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public abstract class Record {
    @Getter
    private final String name;
    @Getter
    private final LocalDateTime creationDate;

    protected Record(String name) {
        this.name = name;
        this.creationDate = LocalDateTime.now();
    }
}
