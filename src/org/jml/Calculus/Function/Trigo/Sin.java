package org.jml.Calculus.Function.Trigo;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Sin implements Func {
    final public static Sin SIN = new Sin();
    private Sin () {}

    @Override
    public double apply (double x) {
        return Math.sin(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.sin(x);
    }

    @Override
    public double deriv(double x) {
        return Math.cos(x);
    }

    @Override
    public float deriv(float x) {
        return Mathf.cos(x);
    }
}
