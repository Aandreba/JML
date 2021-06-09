package org.jml.Calculus.Function.Trigo;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Atan implements Func {
    final public static Atan ATAN = new Atan();
    private Atan() {}

    @Override
    public double apply (double x) {
        return Math.atan(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.atan(x);
    }

    @Override
    public double deriv(double x) {
        return 1 / (1 + x * x);
    }
}
