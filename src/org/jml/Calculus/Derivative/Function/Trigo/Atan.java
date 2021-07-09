package org.jml.Calculus.Derivative.Function.Trigo;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Linear.Linear;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Pow;
import org.jml.Mathx.Mathf;

public class Atan extends Func {
    protected Atan() {}

    @Override
    public double applyTo (double x) {
        return Math.atan(x);
    }

    @Override
    public float applyTo(float x) {
        return Mathf.atan(x);
    }

    @Override
    public Func deriv (Func x) {
        return Const.ONE.div(Const.ONE.add(Pow.TWO.applyTo(x)));
    }

    @Override
    public String toString (Func x) {
        return "atan("+x.toString()+")";
    }
}
