package org.jml.Calculus.Derivative.Function.Linear;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Mathx.Mathf;

public class Sqrt extends Func {
    protected Sqrt () {}

    @Override
    public double applyTo(double x) {
        return Math.sqrt(x);
    }

    @Override
    public float applyTo(float x) {
        return Mathf.sqrt(x);
    }

    @Override
    public Func deriv (Func x) {
        return Const.ONE.div(Const.TWO.mul(this.applyTo(x)));
    }

    @Override
    public String toString(Func x) {
        return "sqrt("+x+")";
    }
}
