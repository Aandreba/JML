package org.jml.Calculus.Derivative.Function.Regular;

import org.jml.Calculus.Derivative.Func;

public class Add extends Func {
    final public double alpha;

    public Add (double alpha) {
        this.alpha = alpha;
    }

    public double applyTo (double x) {
        return x + alpha;
    }

    public Func deriv (Func x) {
        return Const.ONE;
    }

    @Override
    public String toString (Func x) {
        return "("+x.toString()+") + ("+alpha+")";
    }
}
