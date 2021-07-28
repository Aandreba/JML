package org.jml.Mathx.Extra;

import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;
import org.jml.Mathx.Rand;
import org.jml.MT.TaskManager;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Pi {
    final public static float FLOAT = Mathf.PI;
    final public static double DOUBLE = Math.PI;

    final private static BigDecimal TWO = BigDecimal.valueOf(2);
    final private static BigDecimal THREE = BigDecimal.valueOf(3);
    final private static BigDecimal FOUR = BigDecimal.valueOf(4);

    final private static BigDecimal R396 = BigDecimal.valueOf(396);
    final private static BigDecimal R1103 = BigDecimal.valueOf(1103);
    final private static BigDecimal R9801 = BigDecimal.valueOf(9801);
    final private static BigDecimal R26390 = BigDecimal.valueOf(26390);

    /**
     * @see <a href="https://mathscholar.org/2019/02/simple-proofs-archimedes-calculation-of-pi/">Mathscholar</a>
     */
    public static BigDecimal archimedes (int points) {
        final MathContext context = new MathContext(points, RoundingMode.HALF_EVEN);

        BigDecimal a = TWO.multiply(THREE.sqrt(context));;
        BigDecimal b = THREE;

        for (int i=0;i<points;i++) {
            a = TWO.multiply(a).multiply(b).divide(a.add(b), context);
            b = a.multiply(b).sqrt(context);
        }

        return a.add(b).divide(TWO, context);
    }

    public static BigDecimal viete (MathContext context) {
        MathContext superCtx = new MathContext(context.getPrecision() * 2, context.getRoundingMode());

        BigDecimal sqrt = TWO.sqrt(superCtx);
        BigDecimal pi = TWO.divide(sqrt, superCtx);
        BigDecimal last = null;

        while (last == null || !pi.round(context).equals(last.round(context))) {
            sqrt = sqrt.add(TWO).sqrt(superCtx);

            last = pi;
            pi = pi.multiply(TWO.divide(sqrt, superCtx), superCtx);
        }

        return pi.multiply(TWO, context);
    }

    public static BigDecimal leibniz (int points) {
        final MathContext context = new MathContext(points, RoundingMode.HALF_EVEN);

        TaskManager tasks = new TaskManager();
        AtomicInteger k = new AtomicInteger(3);
        AtomicReference<BigDecimal> pi = new AtomicReference<>(BigDecimal.ONE);

        for (int i=0;i<points;i++) {
            int finalI = i;

            tasks.add(() -> {
                int j = k.getAndAdd(2);
                BigDecimal J = BigDecimal.valueOf(j);

                if (Intx.isEven(finalI)) {
                    pi.updateAndGet(q -> q.add(BigDecimal.ONE.divide(J, context)));
                } else {
                    pi.updateAndGet(q -> q.subtract(BigDecimal.ONE.divide(J, context)));
                }
            });
        }

        tasks.run();
        return pi.get().multiply(FOUR);
    }

    public static BigDecimal ramanujan (MathContext context) {
        final MathContext superCtx = new MathContext(context.getPrecision() * 2, context.getRoundingMode());
        final BigDecimal alpha = R9801.divide(TWO.multiply(TWO.sqrt(superCtx)), superCtx);

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal last = null;

        BigDecimal fact4k = BigDecimal.ONE;
        BigDecimal factk = BigDecimal.ONE;

        int i = 0;
        while (last == null || !sum.round(context).equals(last.round(context))) {
            int k4 = 4 * i;
            BigDecimal k = BigDecimal.valueOf(i);

            if (i > 0) {
                factk = factk.multiply(k);
                for (int j = 0; j <= 4; j++) {
                    fact4k = fact4k.multiply(k.add(BigDecimal.valueOf(j)));
                }
            }

            BigDecimal beta = fact4k.divide(factk.pow(4), superCtx);
            BigDecimal gamma = R26390.multiply(k).add(R1103).divide(R396.pow(k4), superCtx);

            last = sum;
            sum = sum.add(beta.multiply(gamma));
            i++;
        }

        sum = BigDecimal.ONE.divide(sum, context);
        return alpha.multiply(sum, context);
    }

    public static BigDecimal monteCarlo (int points) {
        final MathContext context = new MathContext(points, RoundingMode.HALF_EVEN);

        AtomicInteger inside = new AtomicInteger(0);
        TaskManager tasks = new TaskManager();

        for (int i=0;i<points;i++) {
            tasks.add(() -> {
                double x = 2 * Rand.getDouble() - 1;
                double y = Rand.getDouble();

                double alpha = y * y + x * x;
                if (alpha <= 1) {
                    inside.incrementAndGet();
                }
            });
        }

        tasks.run();
        return inside.get() == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(4L * inside.get()).divide(BigDecimal.valueOf(points), context);
    }
}
