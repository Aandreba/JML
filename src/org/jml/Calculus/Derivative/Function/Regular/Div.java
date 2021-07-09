package org.jml.Calculus.Derivative.Function.Regular;

import org.jml.Calculus.Derivative.Func;

public class Div extends Func {
    final public double alpha;
    final public double deriv;

    public Div (double alpha) {
        this.alpha = alpha;
        this.deriv = 1 / alpha;
    }

    @Override
    public double applyTo(double x) {
        return x / alpha;
    }

    @Override
    public Func deriv(Func x) {
        return new Const(deriv);
    }

    @Override
    public String toString (Func x) {
        return "("+x.toString()+") / ("+alpha+")";
    }
}
