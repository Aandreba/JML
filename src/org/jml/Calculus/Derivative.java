package org.jml.Calculus;

import org.jml.Function.Real.RealFunction;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Derivative {
    final private static BigDecimal FLOAT_DX = BigDecimal.valueOf(1, 7);
    final private static BigDecimal DOUBLE_DX = BigDecimal.valueOf(1, 16);

    final private static MathContext FLOAT_CTX = new MathContext(16, RoundingMode.HALF_EVEN);
    final private static MathContext DOUBLE_CTX = new MathContext(33, RoundingMode.HALF_EVEN);

    public static float deriv (float a, RealFunction function) {
        BigDecimal x = BigDecimal.valueOf(a);
        BigDecimal alpha = function.apply(x.add(FLOAT_DX), FLOAT_CTX);
        BigDecimal beta = function.apply(x, FLOAT_CTX);

        return alpha.subtract(beta).divide(FLOAT_DX, MathContext.DECIMAL32).floatValue();
    }

    public static double deriv (double a, RealFunction function) {
        BigDecimal x = BigDecimal.valueOf(a);
        BigDecimal alpha = function.apply(x.add(DOUBLE_DX), DOUBLE_CTX);
        BigDecimal beta = function.apply(x, DOUBLE_CTX);

        return alpha.subtract(beta).divide(DOUBLE_DX, MathContext.DECIMAL64).doubleValue();
    }

    public static BigDecimal deriv (BigDecimal a, MathContext context, RealFunction function) {
        BigDecimal dx = BigDecimal.valueOf(1, context.getPrecision());
        MathContext ctx = new MathContext(2 * context.getPrecision(), context.getRoundingMode());

        BigDecimal alpha = function.apply(a.add(dx), ctx);
        BigDecimal beta = function.apply(a, ctx);
        return alpha.subtract(beta).divide(dx, context);
    }
}
