package org.jml.Calculus.Function.Trigo;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Asin implements Func {
    final public static Asin ASIN = new Asin();
    private Asin() {}

    @Override
    public double apply (double x) {
        return Math.asin(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.asin(x);
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
