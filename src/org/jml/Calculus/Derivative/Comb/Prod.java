package org.jml.Calculus.Derivative.Comb;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Var;

public class Prod extends Comb {
    final public Func alpha, beta;

    public Prod (Func alpha, Func beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public double applyTo(double x) {
        return alpha.applyTo(x) * beta.applyTo(x);
    }

    @Override
    public float applyTo(float x) {
        return alpha.applyTo(x) * beta.applyTo(x);
    }

    @Override
    public Func deriv (Func x) {
        if (alpha instanceof Const && beta instanceof Const) {
            return Const.ZERO;
        } else if (beta instanceof Const) {
            return alpha.deriv(x).mul(beta);
        } else if (alpha instanceof Const) {
            return beta.deriv(x).mul(alpha);
        }

        return alpha.deriv(x).mul(beta.applyTo(x)).add(alpha.applyTo(x).mul(beta.deriv(x)));
    }

    @Override
    public Func replace (Var var, Const constant) {
        Func a = alpha.equals(var) ? constant : alpha;
        Func b = beta.equals(var) ? constant : alpha;

        if (a instanceof Const && b instanceof Const) {
            return new Const(((Const) a).alpha * ((Const) b).alpha);
        }

        return new Prod(a, b);
    }

    @Override
    public String toString (Func x) {
        if (alpha instanceof Const && beta instanceof Const) {
            Const a = (Const) alpha;
            Const b = (Const) beta;

            if (a.alpha == 0 || b.alpha == 0) {
                return "0";
            }

            return Double.toString(a.alpha * b.alpha);
        } else if ((beta instanceof Const && ((Const) beta).alpha == 0) || (alpha instanceof Const && ((Const) alpha).alpha == 0)) {
            return "0";
        }

        return "("+ alpha.applyTo(x).toString() + ") * (" + beta.applyTo(x).toString() + ")";
    }
}
