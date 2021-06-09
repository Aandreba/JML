package org.jml.Calculus.Comb;

import org.jml.Calculus.Func;

public class Chain implements Func {
    final public Func input, output;

    public Chain (Func input, Func output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public double apply (double x) {
        return output.apply(input.apply(x));
    }

    @Override
    public float apply (float x) {
        return output.apply(input.apply(x));
    }

    @Override
    public double deriv(double x) {
        return output.deriv(input.apply(x)) * input.deriv(x);
    }

    @Override
    public float deriv(float x) {
        return output.deriv(input.apply(x)) * input.deriv(x);
    }
}
