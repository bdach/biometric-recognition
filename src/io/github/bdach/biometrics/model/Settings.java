package io.github.bdach.biometrics.model;

import lombok.Getter;
import lombok.Setter;

public class Settings {
    @Getter
    @Setter
    public static Settings instance = new Settings();

    @Getter
    @Setter
    private double gaborWaveletFrequency = 8 * Math.PI;
}
