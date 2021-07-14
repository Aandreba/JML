package org.jml.Calculus.Derivative.Comb;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Pow;
import org.jml.Calculus.Derivative.Function.Regular.Var;

public class Quot extends Comb {
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
    public Func replace (Var var, Const constant) {
        Func a = alpha.equals(var) ? constant : alpha;
        Func b = beta.equals(var) ? constant : alpha;

        if (a instanceof Const && b instanceof Const) {
            return new Const(((Const) a).alpha / ((Const) b).alpha);
        }

        return new Quot(a, b);
    }

    @Override
    public String toString(Func x) {
        return "("+ alpha.applyTo(x).toString() + ") / (" + beta.applyTo(x).toString() + ")";
    }
}
