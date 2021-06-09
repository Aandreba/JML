package org.jml.Calculus.Function.Exponential;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Log implements Func {
    final public static Log LOG = new Log();
    private Log (){}

    @Override
    public double apply(double x) {
        return Math.log(x);
    }

    @Override
    public float apply(float x) {
        return Mathf.log(x);
    }

    @Override
    public double deriv(double x) {
        return 1 / x;
    }
}
