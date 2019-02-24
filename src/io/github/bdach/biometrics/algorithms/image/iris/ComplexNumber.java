package io.github.bdach.biometrics.algorithms.image.iris;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ComplexNumber {
    private double real;
    private double imaginary;

    public void add(ComplexNumber other) {
        this.real += other.real;
        this.imaginary += other.imaginary;
    }

    public void addRealPart(double real) {
        this.real += real;
    }

    public void addImaginaryPart(double imaginary) {
        this.imaginary += imaginary;
    }
}
