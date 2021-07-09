package org.jml.Calculus.Derivative.Function.Trigo;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Linear.Linear;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Pow;
import org.jml.Mathx.Mathf;

public class Asin extends Func {
    protected Asin() {}

    @Override
    public double applyTo (double x) {
        return Math.asin(x);
    }

    @Override
    public float applyTo(float x) {
        return Mathf.asin(x);
    }

    @Override
    public Func deriv (Func x) {
        return Const.ONE.div(Linear.SQRT.applyTo(Const.ONE.subtr(Pow.TWO.applyTo(x))));
    }

    @Override
    public String toString (Func x) {
        return "asin("+x.toString()+")";
    }
}
