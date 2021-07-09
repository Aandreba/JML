package org.jml.Calculus.Derivative.Comb;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;

public class Sum extends Func {
    final public Func alpha, beta;

    public Sum (Func alpha, Func beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public double applyTo(double x) {
        return alpha.applyTo(x) + beta.applyTo(x);
    }

    @Override
    public float applyTo(float x) {
        return alpha.applyTo(x) + beta.applyTo(x);
    }

    @Override
    public Func deriv (Func x) {
        return alpha.deriv(x).add(beta.deriv(x));
    }

    @Override
    public String toString(Func x) {
        return "("+ alpha.applyTo(x).toString() + ") + (" + beta.applyTo(x).toString() + ")";
    }
}
