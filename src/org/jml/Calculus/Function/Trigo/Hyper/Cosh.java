package org.jml.Calculus.Function.Trigo.Hyper;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Cosh implements Func {
    final public static Cosh COSH = new Cosh();
    private Cosh() {}

    @Override
    public double apply (double x) {
        return Math.cosh(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.cosh(x);
    }

    @Override
    public double deriv(double x) {
        return Math.sinh(x);
    }

    @Override
    public float deriv(float x) {
        return Mathf.sinh(x);
    }
}
