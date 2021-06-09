package org.jml.Calculus.Comb;

import org.jml.Calculus.Func;

public class Sum implements Func {
    final public Func alpha, beta;
    public float gamma, delta;

    public Sum (float gamma, Func alpha, float delta, Func beta) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.delta = delta;
    }

    public Sum(Func alpha, Func beta) {
        this(1, alpha, 1, beta);
    }

    @Override
    public double apply(double x) {
        return gamma * alpha.apply(x) + delta * beta.apply(x);
    }

    @Override
    public float apply(float x) {
        return alpha.apply(x) + beta.apply(x);
    }

    @Override
    public double deriv(double x) {
        return gamma * alpha.deriv(x) + delta * beta.deriv(x);
    }

    @Override
    public float deriv(float x) {
        return gamma * alpha.deriv(x) + delta * beta.deriv(x);
    }
}
