package org.jml.Calculus;

import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.Function.Real.RealFunction;
import org.jml.MT.TaskIterator;
import org.jml.MT.TaskManager;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicReference;

public class Integral {
    public static BigDecimal integ (BigDecimal a, BigDecimal b, RealFunction function, MathContext context) {
        BigDecimal delta = b.subtract(a);
        BigDecimal dx = BigDecimal.valueOf(1, context.getPrecision());
        BigDecimal div = delta.divide(dx, context);

        AtomicReference<BigDecimal> x = new AtomicReference<>(a);
        AtomicReference<BigDecimal> sum = new AtomicReference<>(BigDecimal.ZERO);

        TaskIterator iterator = new TaskIterator(() -> {
            BigDecimal X = x.getAndUpdate(z -> z.add(dx));
            BigDecimal Y = function.apply(X, context);

            sum.updateAndGet(z -> z.add(Y));
        }, i -> Mathb.lesserOrEqual(x.get(), b));

        iterator.run();
        return sum.get().divide(div, context);
    }

    public static float integ (float a, float b, RealFunction function) {
        return integ(BigDecimal.valueOf(a), BigDecimal.valueOf(b), function, MathContext.DECIMAL32).floatValue();
    }

    public static double integ (double a, double b, RealFunction function) {
        return integ(BigDecimal.valueOf(a), BigDecimal.valueOf(b), function, MathContext.DECIMAL64).doubleValue();
    }

    public static Comp integ (float a, float b, ComplexFunction func) {
        return null;
    }
}
