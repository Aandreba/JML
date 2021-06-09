package org.jml.Calculus.Function.Regular;

import org.jml.Calculus.Func;
import org.jml.Mathx.Mathf;

public class Pow implements Func {
    final public double alpha;

    public Pow (double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double apply (double x) {
        return Math.pow(x, alpha);
    }

    @Override
    public float apply (float x) {
        return Mathf.pow(x, (float) alpha);
    }

    @Override
    public double deriv(double x) {
        return alpha * Math.pow(x, alpha - 1);
    }

    @Override
    public float deriv(float x) {
        float falpha = (float) alpha;
        return falpha * Mathf.pow(x, falpha - 1);
    }
}
