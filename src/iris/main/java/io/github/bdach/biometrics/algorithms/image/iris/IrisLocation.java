package io.github.bdach.biometrics.algorithms.image.iris;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IrisLocation {
    public final int centerX;
    public final int centerY;
    public final int innerRadius;
    public final int outerRadius;
}
