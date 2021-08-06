package org.jml.Function.Real;

import java.math.BigDecimal;
import java.math.MathContext;

public interface FloatReal extends RealFunction {
    default double apply (double x) {
        return apply ((float) x);
    }
    default BigDecimal apply(BigDecimal x, MathContext context) {
        return BigDecimal.valueOf(apply(x.floatValue()));
    }
}
