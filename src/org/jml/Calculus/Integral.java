package org.jml.Calculus;

import org.jml.Complex.Decimal.Compb;
import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.Function.Real.RealFunction;
import org.jml.Function.Real.FloatReal;
import org.jml.MT.TaskIterator;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;
import org.jml.Vector.Decimal.Vecb;
import org.jml.Vector.Double.Vecd;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicReference;

public class Integral {
    final private static BigDecimal N945 = BigDecimal.valueOf(945);
    final private static BigDecimal N45 = BigDecimal.valueOf(45);
    final private static BigDecimal N32 = BigDecimal.valueOf(32);
    final private static BigDecimal N12 = BigDecimal.valueOf(12);
    final private static BigDecimal N14 = BigDecimal.valueOf(14);

    public static BigDecimal integ (BigDecimal a, BigDecimal b, RealFunction func, MathContext context) {
        BigDecimal delta = b.subtract(a);
        BigDecimal alpha = func.deriv(6).apply(delta.divide(Mathb.TWO), context).abs();
        alpha = Mathb.max(alpha, BigDecimal.valueOf(1, context.getPrecision()));

        BigDecimal N = Mathb.EIGHT.divide(BigDecimal.valueOf(945, context.getPrecision()), context);
        N = N.multiply(alpha).multiply(delta.pow(7));
        N = Mathb.sqrt(N, context);

        BigInteger n = N.toBigInteger();
        BigInteger nm1 = n.subtract(BigInteger.ONE);
        BigInteger nm3 = n.subtract(Mathb.INT_THREE);
        BigDecimal h = delta.divide(new BigDecimal(nm1), context);

        BigDecimal error = Mathb.EIGHT.negate().divide(N945, context).multiply(h.pow(7)).multiply(alpha, context);
        BigDecimal sum = Mathb.SEVEN.multiply(func.apply(a, context).add(func.apply(b, context)));

        AtomicReference<BigDecimal> sum32 = new AtomicReference<>(Mathb.ZERO);
        AtomicReference<BigDecimal> sum12 = new AtomicReference<>(Mathb.ZERO);
        AtomicReference<BigDecimal> sum14 = new AtomicReference<>(Mathb.ZERO);

        AtomicReference<BigInteger> I32 = new AtomicReference<>(Mathb.INT_ONE);
        AtomicReference<BigInteger> I12 = new AtomicReference<>(Mathb.INT_TWO);
        AtomicReference<BigInteger> I14 = new AtomicReference<>(Mathb.INT_FOUR);

        TaskIterator iter = new TaskIterator(() -> {
            BigInteger i32 = I32.getAndUpdate(z -> z.add(Mathb.INT_TWO));
            BigInteger i12 = I12.getAndUpdate(z -> z.add(Mathb.INT_FOUR));
            BigInteger i14 = I14.getAndUpdate(z -> z.add(Mathb.INT_FOUR));

            if (Mathb.lesserThan(i32, n)) {
                BigDecimal f = a.add(h.multiply(new BigDecimal(i32)));
                sum32.updateAndGet(z -> z.add(func.apply(f, context)));
            }

            if (Mathb.lesserThan(i12, nm1)) {
                BigDecimal f = a.add(h.multiply(new BigDecimal(i12)));
                sum12.updateAndGet(z -> z.add(func.apply(f, context)));
            }

            if (Mathb.lesserThan(i14, nm3)) {
                BigDecimal f = a.add(h.multiply(new BigDecimal(i14)));
                sum14.updateAndGet(z -> z.add(func.apply(f, context)));
            }

        }, q -> Mathb.lesserThan(I32.get(), n) | Mathb.lesserThan(I12.get(), nm1) | Mathb.lesserThan(I14.get(), nm3));

        iter.run();
        return sum.add(sum32.get().multiply(N32)).add(sum14.get().multiply(N14)).add(sum12.get().multiply(N12)).multiply(Mathb.TWO).multiply(h).divide(N45, context).add(error, context);
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
