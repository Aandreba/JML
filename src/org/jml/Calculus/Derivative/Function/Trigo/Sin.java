package org.jml.Calculus.Derivative.Function.Trigo;

import org.jml.Calculus.Derivative.Func;
import org.jml.Mathx.Mathf;

public class Sin extends Func {
    protected Sin () {}

    @Override
    public double applyTo (double x) {
        return Math.sin(x);
    }

    @Override
    public float applyTo(float x) {
        return Mathf.sin(x);
    }

    @Override
    public Func deriv (Func x) {
        return Trigo.COS.applyTo(x);
    }

    @Override
    public String toString (Func x) {
        return "sin("+x.toString()+")";
    }
}
