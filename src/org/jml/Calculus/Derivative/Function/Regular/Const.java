package org.jml.Calculus.Derivative.Function.Regular;

import org.jml.Calculus.Derivative.Func;

import java.util.Objects;

public class Const extends Func {
    final public static Const MONE = new Const(-1);
    final public static Const ZERO = new Const(0);
    final public static Const ONE = new Const(1);
    final public static Const TWO = new Const(2);
    final public static Const THREE = new Const(3);

    final public static Const E = new Const(Math.E);
    final public static Const PI = new Const(Math.PI);
    final public static Const HALF = new Const(0.5);

    final public double alpha;

    public Const (double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double applyTo(double x) {
        return alpha;
    }

    @Override
    public Func deriv (Func x) {
        return ZERO;
    }

    @Override
    public String toString(Func x) {
        return Double.toString(alpha);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Const aConst = (Const) o;
        return Double.compare(aConst.alpha, alpha) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alpha);
    }
}
