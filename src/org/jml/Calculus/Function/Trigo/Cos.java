package org.jml.Calculus.Function.Trigo;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Cos implements Func {
    final public static Cos COS = new Cos();
    private Cos() {}

    @Override
    public double apply (double x) {
        return Math.cos(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.cos(x);
    }

    @Override
    public double deriv(double x) {
        return -Math.sin(x);
    }

    @Override
    public float deriv(float x) {
        return -Mathf.sin(x);
    }
}
