package org.jml.Calculus;

import java.util.function.Function;

public class Derivative {
    public static float deriv (float x, Function<Float, Float> func) {
        final float dx = 1e-5f;
        return (func.apply(x + dx) - func.apply(x)) / dx;
    }
}
