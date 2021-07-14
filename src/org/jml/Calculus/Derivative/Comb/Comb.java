package org.jml.Calculus.Derivative.Comb;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;
import org.jml.Calculus.Derivative.Function.Regular.Var;

public abstract class Comb extends Func {
    public abstract Func replace (Var var, Const constant);
    public Func replace (Var var, double constant) {
        return replace(var, new Const(constant));
    }
}
