package org.jml.Mathx;

import org.jml.MT.TaskIterator;
import org.jml.Mathx.Extra.Intx;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicReference;

public class Mathb {
    public static BigDecimal exp (BigDecimal value, MathContext context) {
        BigDecimal exp = new BigDecimal(1, context);
        BigDecimal last = null;

        BigDecimal x = BigDecimal.ONE;
        BigDecimal fact = BigDecimal.ONE;

        while (!exp.equals(last)) {
            fact = fact.multiply(x);

            last = exp;
            exp = exp.add(value.pow(x.intValue(), context).divide(fact, context), context);

            x = x.add(BigDecimal.ONE);
        }

        return exp;
    }

    public static BigDecimal exp (double value, MathContext context) {
        return exp(BigDecimal.valueOf(value), context);
    }

    public static BigDecimal exp (long value, MathContext context) {
        return exp(BigDecimal.valueOf(value), context);
    }

    public static BigDecimal log (BigDecimal value, MathContext context) {
        AtomicReference<BigDecimal> log = new AtomicReference<>(new BigDecimal(0, context));
        AtomicReference<BigDecimal> last = new AtomicReference<>(null);

        value.scale();
        TaskIterator tasks = new TaskIterator(i -> {
            boolean plus = Intx.isOdd(i);
            BigDecimal k = BigDecimal.valueOf(i);


        }, i -> !log.equals(last));

        return log;
    }

    public static BigDecimal log (double value, MathContext context) {
        return log(BigDecimal.valueOf(value), context);
    }

    public static BigDecimal log (long value, MathContext context) {
        return log(BigDecimal.valueOf(value), context);
    }
}
