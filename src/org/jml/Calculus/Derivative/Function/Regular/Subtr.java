package org.jml.Calculus.Derivative.Function.Regular;

import org.jml.Calculus.Derivative.Func;

public class Subtr extends Func {
    final public double alpha;

    public Subtr(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double applyTo(double x) {
        return x - alpha;
    }

    @Override
    public Func deriv (Func x) {
        return Const.ONE;
    }

    @Override
    public String toString (Func x) {
        return "("+x.toString()+") - ("+alpha+")";
    }
}
