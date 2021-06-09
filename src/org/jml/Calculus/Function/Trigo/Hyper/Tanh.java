package org.jml.Calculus.Function.Trigo.Hyper;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Tanh implements Func {
    final public static Tanh TANH = new Tanh();
    private Tanh() {}

    @Override
    public double apply (double x) {
        return Math.tanh(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.tanh(x);
    }

    @Override
    public double deriv(double x) {
        double cosh = Math.cosh(x);
        return 1 / (cosh * cosh);
    }

    @Override
    public float deriv(float x) {
        float cosh = Mathf.cosh(x);
        return 1 / (cosh * cosh);
    }
}
