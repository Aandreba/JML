package org.jml.Function.Real;

import java.math.BigDecimal;
import java.math.MathContext;

public interface DoubleReal extends RealFunction {
    default float apply (float x) {
        return (float) apply ((double) x);
    }

    default BigDecimal apply(BigDecimal x, MathContext context) {
        return BigDecimal.valueOf(apply(x.doubleValue()));
    }
}
