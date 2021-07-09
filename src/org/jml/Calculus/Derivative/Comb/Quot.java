package org.jml.Calculus.Derivative.Comb;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Pow;

public class Quot extends Func {
    final public Func alpha, beta;

    public Quot (Func alpha, Func beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public double applyTo(double x) {
        return alpha.applyTo(x) / beta.applyTo(x);
    }

    @Override
    public float applyTo(float x) {
        return alpha.applyTo(x) / beta.applyTo(x);
    }

    @Override
    public Func deriv (Func x) {
        Func g = beta.applyTo(x);
        return alpha.deriv(x).mul(g).subtr(alpha.applyTo(x).mul(beta.deriv(x))).div(Pow.TWO.applyTo(g));
    }

    @Override
    public String toString(Func x) {
        return "("+ alpha.applyTo(x).toString() + ") / (" + beta.applyTo(x).toString() + ")";
    }
}
