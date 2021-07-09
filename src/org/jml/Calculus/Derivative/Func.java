package org.jml.Calculus.Derivative;

import org.jml.Calculus.Derivative.Comb.*;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Var;

public abstract class Func {
    public abstract double applyTo (double x);
    public abstract Func deriv (Func x);
    public abstract String toString (Func x);

    public float applyTo(float x) {
        return (float) applyTo((double) x);
    }
    public double deriv (double x) {
        return deriv(Var.X).applyTo(x);
    }
    public float deriv (float x) {
        return deriv(Var.X).applyTo(x);
    }

    final public Func deriv () {
        return deriv(Var.X);
    }
    final public Sum add (Func beta) { return new Sum(this, beta); }
    final public Sum add (double beta) { return new Sum(this, new Const(beta)); }
    final public Subtr subtr (Func beta) { return new Subtr(this, beta); }
    final public Subtr subtr (double beta) { return new Subtr(this, new Const(beta)); }
    final public Subtr invSubtr (double alpha) { return new Subtr(new Const(alpha), this); }
    final public Prod mul (Func beta) { return new Prod(this, beta); }
    final public Prod mul (double beta) { return new Prod(this, new Const(beta)); }
    final public Quot div (Func beta) { return new Quot(this, beta); }
    final public Quot div (double beta) { return new Quot(this, new Const(beta)); }
    final public Quot invDiv (double alpha) { return new Quot(new Const(alpha), this); }
    final public Chain apply (Func output) {
        return new Chain(this, output);
    }
    final public Chain applyTo (Func output) {
        return new Chain(output, this);
    }

    @Override
    public String toString() {
        return toString(Var.X);
    }
}
