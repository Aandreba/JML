package org.jml.Calculus.Function.Trigo.Hyper;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Sinh implements Func {
    final public static Sinh SINH = new Sinh();
    private Sinh() {}

    @Override
    public double apply (double x) {
        return Math.sinh(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.sinh(x);
    }

    @Override
    public double deriv(double x) {
        return Math.cosh(x);
    }

    @Override
    public float deriv(float x) {
        return Mathf.cosh(x);
    }
}
