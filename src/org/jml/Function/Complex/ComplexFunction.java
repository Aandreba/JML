package org.jml.Function.Complex;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;

public interface ComplexFunction {
    Comp apply (float x);
    Compd apply (double x);
}
