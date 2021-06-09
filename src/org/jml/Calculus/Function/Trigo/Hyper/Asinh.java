package org.jml.Calculus.Function.Trigo.Hyper;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Asinh implements Func {
    final public static Asinh ASIN = new Asinh();
    private Asinh() {}

    @Override
    public double apply (double x) {
        return Math.asin(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.asinh(x);
    }

    @Override
    public double deriv(double x) {
        return 1 / Math.sqrt(1 - x * x);
    }

    @Override
    public float deriv(float x) {
        return 1 / Mathf.sqrt(1 - x * x);
    }
}
