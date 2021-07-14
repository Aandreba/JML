package org.jml.Calculus.Derivative.Comb;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Var;

public class Chain extends Comb {
    final public Func input, output;

    public Chain (Func input, Func output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public double applyTo(double x) {
        return output.applyTo(input.applyTo(x));
    }

    @Override
    public float applyTo(float x) {
        return output.applyTo(input.applyTo(x));
    }

    @Override
    public Func deriv (Func x) {
        return output.deriv(input.applyTo(x)).mul(input.deriv(x));
    }

    @Override
    public Chain replace (Var var, Const constant) {
        return new Chain(input.equals(var) ? constant : input, output);
    }

    @Override
    public String toString (Func x) {
        return input instanceof Const ? output.toString(input) : output.toString(input.applyTo(x));
    }
}
