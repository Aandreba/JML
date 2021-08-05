package org.jml.Function.Real;

import java.math.BigDecimal;
import java.math.MathContext;

public interface DecimalReal extends RealFunction {
    default float apply (float x) {
        return apply(BigDecimal.valueOf(x), MathContext.DECIMAL32).floatValue();
    }

    default double apply (double x) {
        return apply(BigDecimal.valueOf(x), MathContext.DECIMAL64).doubleValue();
    }
}
