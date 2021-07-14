package org.jml.Calculus.Derivative.Function.Regular;

import org.jml.Calculus.Derivative.Func;

public class Var extends Func {
    final public static Var X = new Var("x");
    final public String symbol;

    public Var (String symbol) {
        this.symbol = symbol;
    }

    @Override
    public double applyTo (double x) {
        return x;
    }

    @Override
    public Func deriv (Func x) {
        return Const.ONE;
    }

    @Override
    public String toString(Func x) {
        return symbol;
    }
}
