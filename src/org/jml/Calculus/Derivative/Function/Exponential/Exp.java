package org.jml.Calculus.Derivative.Function.Exponential;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Mathx.Mathf;

public class Exp extends Func {
    final public static Exp EXP = new Exp();
    final public static Log LOG = new Log();
    final public static Log LN = LOG;

    private Exp () {};

    @Override
    public double applyTo(double x) {
        return Math.exp(x);
    }

    @Override
    public float applyTo(float x) {
        return Mathf.exp(x);
    }

    @Override
    public Func deriv (Func x) {
        return this.applyTo(x);
    }

    @Override
    public String toString(Func x) {
        return "exp("+x.toString()+")";
    }
}
