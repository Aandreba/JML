package org.jml.Calculus.Derivative.Function.Regular;

import org.jml.Calculus.Derivative.Func;
import org.jml.Mathx.Mathf;

public class Pow extends Func {
    final public static Pow TWO = new Pow(2);
    final public double alpha;

    public Pow (double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double applyTo (double x) {
        return Math.pow(x, alpha);
    }

    @Override
    public float applyTo (float x) {
        return Mathf.pow(x, (float) alpha);
    }

    @Override
    public Func deriv (Func x) {
        return alpha == 2 ? Const.TWO.mul(x) : new Const(alpha).mul(new Pow(alpha - 1).applyTo(x));
    }

    @Override
    public String toString (Func x) {
        return "("+x.toString()+") ^ ("+alpha+")";
    }
}
