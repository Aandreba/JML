package org.jml.Function.Complex;

import org.jml.Complex.Double.Compd;

public interface FloatComplex extends ComplexFunction {
    default Compd apply (double x) {
        return apply((float) x).toDouble();
    }
}
