package org.jml.Calculus.Comb;

import org.jml.Calculus.Func;

public class Quot implements Func {
    final public Func alpha, beta;

    public Quot (Func alpha, Func beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public double apply(double x) {
        return alpha.apply(x) / beta.apply(x);
    }

    @Override
    public float apply(float x) {
        return alpha.apply(x) / beta.apply(x);
    }

    @Override
    public double deriv(double x) {
        double g = beta.apply(x);
        return (alpha.deriv(x) * g - alpha.apply(x) * beta.deriv(x)) / (g * g);
    }

    @Override
    public float deriv(float x) {
        float g = beta.apply(x);
        return (alpha.deriv(x) * g - alpha.apply(x) * beta.deriv(x)) / (g * g);
    }
}
