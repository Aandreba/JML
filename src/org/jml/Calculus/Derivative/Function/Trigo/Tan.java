package org.jml.Calculus.Derivative.Function.Trigo;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Pow;
import org.jml.Mathx.Mathf;

public class Tan extends Func {
    protected Tan() {}

    @Override
    public double applyTo (double x) {
        return Math.tan(x);
    }

    @Override
    public float applyTo (float x) {
        return Mathf.tan(x);
    }

    @Override
    public Func deriv (Func x) {
        return Pow.TWO.applyTo(Trigo.SEC.applyTo(x));
    }

    @Override
    public String toString (Func x) {
        return "tan("+x.toString()+")";
    }
}
