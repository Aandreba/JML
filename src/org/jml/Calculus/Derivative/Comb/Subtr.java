package org.jml.Calculus.Derivative.Comb;

import org.jml.Calculus.Derivative.Func;

public class Subtr extends Func {
    final public Func alpha, beta;

    public Subtr(Func alpha, Func beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public double applyTo(double x) {
        return alpha.applyTo(x) - beta.applyTo(x);
    }

    @Override
    public float applyTo(float x) {
        return alpha.applyTo(x) - beta.applyTo(x);
    }

    @Override
    public Func deriv (Func x) {
        return alpha.deriv(x).subtr(beta.deriv(x));
    }

    @Override
    public String toString(Func x) {
        return "("+ alpha.applyTo(x).toString() + ") - (" + beta.applyTo(x).toString() + ")";
    }
}
