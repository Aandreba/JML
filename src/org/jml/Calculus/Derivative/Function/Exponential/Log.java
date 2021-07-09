package org.jml.Calculus.Derivative.Function.Exponential;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Mathx.Mathf;

public class Log extends Func {
    protected Log() {};

    @Override
    public double applyTo(double x) {
        return Math.log(x);
    }

    @Override
    public float applyTo(float x) {
        return Mathf.log(x);
    }

    @Override
    public Func deriv (Func x) {
        return Const.ONE.div(x);
    }

    @Override
    public String toString(Func x) {
        return "ln("+x.toString()+")";
    }
}
