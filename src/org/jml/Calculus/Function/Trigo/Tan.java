package org.jml.Calculus.Function.Trigo;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Tan implements Func {
    final public static Tan TAN = new Tan();
    private Tan() {}

    @Override
    public double apply (double x) {
        return Math.tan(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.tan(x);
    }

    @Override
    public double deriv(double x) {
        double tan = Math.tan(x);
        return 1 + tan * tan;
    }

    @Override
    public float deriv(float x) {
        float tan = Mathf.tan(x);
        return 1 + tan * tan;
    }
}
