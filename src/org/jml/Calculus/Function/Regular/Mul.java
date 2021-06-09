package org.jml.Calculus.Function.Regular;

import org.jml.Calculus.Func;

public class Mul implements Func {
    final public double alpha;

    public Mul(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double apply (double x) {
        return alpha * x;
    }

    @Override
    public double deriv(double x) {
        return alpha;
    }
}
