package org.jml.Calculus;

import org.jml.Complex.Single.Comp;
import org.jml.MT.TaskManager;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicReference;

public class Integral {
    final private static MathContext CONTEXT = MathContext.DECIMAL128;
    final private static BigDecimal ALPHA = BigDecimal.valueOf(10).pow(5);

    public interface RealFunction {
        float apply (float x);
    }

    public interface ComplexFunction {
        Comp apply (float x);
    }

    public static float integ (float a, float b, RealFunction function) {
        BigDecimal A = BigDecimal.valueOf(a);
        BigDecimal B = BigDecimal.valueOf(b);

        BigDecimal delta = B.subtract(A);
        BigDecimal dx = delta.sqrt(CONTEXT).divide(ALPHA, CONTEXT);
        int j = delta.divide(dx, CONTEXT).intValue();

        TaskManager manager = new TaskManager();
        AtomicReference<BigDecimal> sum = new AtomicReference<>(BigDecimal.ZERO);

        for (int i=0;i<j;i++) {
            int finalI = i;

            manager.add(() -> {
                BigDecimal x = BigDecimal.valueOf(finalI).multiply(dx).add(A);
                BigDecimal y = BigDecimal.valueOf(function.apply(x.floatValue()));
                sum.updateAndGet(k -> k.add(y));
            });
        }

        manager.run();
        return sum.get().multiply(dx).floatValue();
    }

    public static Comp integ (float a, float b, ComplexFunction function) {
        BigDecimal A = BigDecimal.valueOf(a);
        BigDecimal B = BigDecimal.valueOf(b);

        BigDecimal delta = B.subtract(A);
        BigDecimal dx = delta.sqrt(CONTEXT).divide(ALPHA, CONTEXT);
        int j = delta.divide(dx, CONTEXT).intValue();

        TaskManager tasks = new TaskManager();
        AtomicReference<BigDecimal> real = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> imaginary = new AtomicReference<>(BigDecimal.ZERO);

        for (int i=0;i<j;i++) {
            int finalI = i;

            tasks.add(() -> {
                BigDecimal x = BigDecimal.valueOf(finalI).multiply(dx).add(A);
                Comp y = function.apply(x.floatValue());
                real.getAndUpdate(k -> k.add(BigDecimal.valueOf(y.real)));
                imaginary.getAndUpdate(k -> k.add(BigDecimal.valueOf(y.imaginary)));
            });
        }

        tasks.run();
        return new Comp(real.get().multiply(dx).floatValue(), imaginary.get().multiply(dx).floatValue());
    }
}
