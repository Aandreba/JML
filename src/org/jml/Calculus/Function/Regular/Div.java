package org.jml.Calculus.Function.Regular;

import org.jml.Calculus.Func;

public class Div implements Func {
    final public double alpha;

    public Div(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double apply (double x) {
        return x / alpha;
    }

    @Override
    public double deriv(double x) {
        return 1 / alpha;
    }
}
