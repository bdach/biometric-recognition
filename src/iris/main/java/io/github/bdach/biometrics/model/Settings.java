package io.github.bdach.biometrics.model;

import io.github.bdach.biometrics.SettingChangeListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Settings {
    public static Settings instance = new Settings();

    private Settings(Settings instance) {
        this.gaborWaveletFrequency = instance.gaborWaveletFrequency;
    }

    public static Settings getInstance() {
        return new Settings(instance);
    }

    public static void setInstance(Settings newSettings, SettingChangeListener listener) {
        Settings oldSettings = instance;
        instance = newSettings;
        if (listener == null)
            return;
        if (newSettings.gaborWaveletFrequency != oldSettings.gaborWaveletFrequency) {
            listener.onGaborWaveletFrequencyChanged();
        }
    }

    @Getter
    @Setter
    private double gaborWaveletFrequency = Math.PI;
}
