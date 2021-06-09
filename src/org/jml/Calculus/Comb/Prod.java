package org.jml.Calculus.Comb;

import org.jml.Calculus.Func;

public class Prod implements Func {
    final public Func alpha, beta;

    public Prod (Func alpha, Func beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public double apply(double x) {
        return alpha.apply(x) * beta.apply(x);
    }

    @Override
    public float apply(float x) {
        return alpha.apply(x) * beta.apply(x);
    }

    @Override
    public double deriv(double x) {
        return alpha.deriv(x) * beta.apply(x) + alpha.apply(x) * beta.deriv(x);
    }

    @Override
    public float deriv(float x) {
        return alpha.deriv(x) * beta.apply(x) + alpha.apply(x) * beta.deriv(x);
    }
}
