package org.jml.Mathx;

import org.jml.MT.TaskIterator;
import org.jml.Mathx.Extra.Intx;
import org.jml.Mathx.Extra.Pi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Mathb {
    final public static BigDecimal ZERO = BigDecimal.ZERO;
    final public static BigDecimal ONE = BigDecimal.ONE;
    final public static BigDecimal TWO = BigDecimal.valueOf(2);
    final public static BigDecimal THREE = BigDecimal.valueOf(3);
    final public static BigDecimal FOUR = BigDecimal.valueOf(4);
    final public static BigDecimal FIVE = BigDecimal.valueOf(5);
    final public static BigDecimal SIX = BigDecimal.valueOf(6);
    final public static BigDecimal SEVEN = BigDecimal.valueOf(7);
    final public static BigDecimal EIGHT = BigDecimal.valueOf(8);
    final public static BigDecimal NINE = BigDecimal.valueOf(9);
    final public static BigDecimal TEN = BigDecimal.TEN;
    final public static BigDecimal HALF = ONE.divide(TWO);

    public static boolean greaterThan (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    public static boolean greaterThan (BigDecimal a, double b) {
        return a.compareTo(BigDecimal.valueOf(b)) > 0;
    }

    public static boolean greaterThan (double a, BigDecimal b) {
        return BigDecimal.valueOf(a).compareTo(b) > 0;
    }

    public static boolean greaterOrEqual (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0;
    }

    public static boolean greaterOrEqual (BigDecimal a, double b) {
        return a.compareTo(BigDecimal.valueOf(b)) >= 0;
    }

    public static boolean greaterOrEqual (double a, BigDecimal b) {
        return BigDecimal.valueOf(a).compareTo(b) >= 0;
    }

    public static boolean lesserThan (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    public static boolean lesserThan (BigDecimal a, double b) {
        return a.compareTo(BigDecimal.valueOf(b)) < 0;
    }

    public static boolean lesserThan (double a, BigDecimal b) {
        return BigDecimal.valueOf(a).compareTo(b) < 0;
    }

    public static boolean lesserOrEqual (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0;
    }

    public static boolean lesserOrEqual (BigDecimal a, double b) {
        return a.compareTo(BigDecimal.valueOf(b)) <= 0;
    }

    public static boolean lesserOrEqual (double a, BigDecimal b) {
        return BigDecimal.valueOf(a).compareTo(b) <= 0;
    }

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
}
