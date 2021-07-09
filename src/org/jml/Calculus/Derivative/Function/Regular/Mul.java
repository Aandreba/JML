package org.jml.Calculus.Derivative.Function.Regular;

import org.jml.Calculus.Derivative.Func;

public class Mul extends Func {
    final public double alpha;

    public Mul(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double applyTo(double x) {
        return alpha * x;
    }

    @Override
    public Func deriv (Func x) {
        return new Const(alpha);
    }

    @Override
    public String toString (Func x) {
        return "("+x.toString()+") * ("+alpha+")";
    }
}
