package org.jml.Calculus.Derivative.Function.Linear;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Pow;
import org.jml.Mathx.Mathf;

public class Cbrt extends Func {
    final private static Pow root = new Pow(2d / 3);

    protected Cbrt() {}

    @Override
    public double applyTo(double x) {
        return Math.cbrt(x);
    }

    @Override
    public float applyTo(float x) {
        return Mathf.cbrt(x);
    }

    @Override
    public Func deriv (Func x) {
        return Const.ONE.div(Const.THREE.mul(root.applyTo(x)));
    }

    @Override
    public String toString(Func x) {
        return "cbrt("+x+")";
    }
}
