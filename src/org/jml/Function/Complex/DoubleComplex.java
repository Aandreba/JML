package org.jml.Function.Complex;

import org.jml.Complex.Single.Comp;

public interface DoubleComplex extends ComplexFunction {
    default Comp apply (float x) {
        return apply((double) x).toFloat();
    }
}
