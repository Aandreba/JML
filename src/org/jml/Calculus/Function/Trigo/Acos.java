package org.jml.Calculus.Function.Trigo;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Acos implements Func {
    final public static Acos ACOS = new Acos();
    private Acos() {}

    @Override
    public double apply (double x) {
        return Math.acos(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.acos(x);
    }

    @Override
    public double deriv(double x) {
        return -1 / Math.sqrt(1 - x * x);
    }

    @Override
    public float deriv(float x) {
        return -1 / Mathf.sqrt(1 - x * x);
    }
}
