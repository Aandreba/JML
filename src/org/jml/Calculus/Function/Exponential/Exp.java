package org.jml.Calculus.Function.Exponential;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Exp implements Func {
    final public static Exp EXP = new Exp();

    private Exp (){}

    @Override
    public double apply(double x) {
        return Math.exp(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.exp(x);
    }

    @Override
    public double deriv(double x) {
        return Math.exp(x);
    }

    @Override
    public float deriv(float x) {
        return Mathf.exp(x);
    }
}
