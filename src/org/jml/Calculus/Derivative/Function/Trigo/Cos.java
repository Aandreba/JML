package org.jml.Calculus.Derivative.Function.Trigo;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Mathx.Mathf;

public class Cos extends Func {
    protected Cos() {}

    @Override
    public double applyTo (double x) {
        return Math.cos(x);
    }

    @Override
    public float applyTo (float x) {
        return Mathf.cos(x);
    }

    @Override
    public Func deriv (Func x) {
        return Trigo.SIN.applyTo(x).mul(Const.MONE);
    }

    @Override
    public String toString (Func x) {
        return "cos("+x.toString()+")";
    }
}
