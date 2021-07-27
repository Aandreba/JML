package org.jml.Mathx;

import org.jml.Calculus.Integral;
import org.jml.MT.TaskIterator;
import org.jml.Mathx.Extra.Intx;
import org.jml.Mathx.Extra.Pi;
import org.jml.Matrix.Single.Mat;

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

    final private static HashMap<MathContext, BigDecimal> PI = new HashMap<>();
    final private static HashMap<MathContext, BigDecimal> E = new HashMap<>();
    final private static HashMap<MathContext, BigDecimal> SQRT2 = new HashMap<>();

    public static boolean greaterThan (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    public static boolean greaterOrEqual (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0;
    }

    public static boolean lesserThan (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    public static boolean lesserOrEqual (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0;
    }

    public static boolean isOdd (BigInteger a) {
        return a.testBit(0);
    }

    public static boolean isEven (BigInteger a) {
        return !isOdd(a);
    }

    public static BigDecimal pi (MathContext context) {
        BigDecimal pi = PI.get(context);
        return pi == null ? PI.put(context, asin(ONE, context).multiply(TWO)) : pi;
    }

    public static BigDecimal e (MathContext context) {
        BigDecimal pi = E.get(context);
        return pi == null ? E.put(context, exp(ONE, context)) : pi;
    }

    public static BigDecimal sqrt (BigDecimal a, MathContext context) {
        if (a.equals(TWO)) {
            BigDecimal sqrt2 = SQRT2.get(context);
            return sqrt2 == null ? SQRT2.put(context, TWO.sqrt(context)) : sqrt2;
        }

        return a.sqrt(context);
    }

    public static BigDecimal cbrt (BigDecimal a, MathContext context) {
        BigDecimal cbrt = ONE;
        BigDecimal last = null;

        BigDecimal a2 = a.multiply(TWO);
        while (!cbrt.equals(last)) {
            BigDecimal pow = cbrt.pow(3);
            BigDecimal alpha = pow.add(a2).divide(TWO.multiply(pow).add(a), context);

            last = cbrt;
            cbrt = cbrt.multiply(alpha, context);
        }

        return cbrt;
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

    public static BigDecimal log (BigDecimal a, MathContext context) {
        BigDecimal log = a;
        BigDecimal last = null;

        while (!log.equals(last)) {
            BigDecimal exp = exp(log, context);
            BigDecimal expmt = exp.subtract(a);

            last = log;
            log = log.subtract(expmt.divide(exp.subtract(expmt.divide(TWO, context)), context), context);
        }

        return log;
    }

    public static BigDecimal pow (BigDecimal a, BigDecimal b, MathContext context) {
        // TODO
        return null;
    }

    public static BigDecimal sin (BigDecimal a, MathContext context) {
        BigDecimal sum = a;
        BigDecimal last = null;

        BigDecimal pow = a;
        BigDecimal fact = ONE;
        BigDecimal n = ONE;

        boolean add = false;
        while (!sum.equals(last)) {
            pow = pow.multiply(a).multiply(a);
            fact = fact.multiply(n = n.add(ONE));
            fact = fact.multiply(n = n.add(ONE));
            last = sum;

            BigDecimal alpha = pow.divide(fact, context);
            sum = add ? sum.add(alpha, context) : sum.subtract(alpha, context);
            add = !add;
        }

        return sum;
    }

    public static BigDecimal cos (BigDecimal a, MathContext context) {
        BigDecimal sum = ONE;
        BigDecimal last = null;

        BigDecimal pow = ONE;
        BigDecimal fact = ONE;
        BigDecimal n = ZERO;

        boolean add = false;
        while (!sum.equals(last)) {
            pow = pow.multiply(a).multiply(a);
            fact = fact.multiply(n = n.add(ONE));
            fact = fact.multiply(n = n.add(ONE));
            last = sum;

            BigDecimal alpha = pow.divide(fact, context);
            sum = add ? sum.add(alpha, context) : sum.subtract(alpha, context);
            add = !add;
        }

        return sum;
    }

    public static BigDecimal tan (BigDecimal a, MathContext context) {
        return sin(a, context).divide(cos(a, context), context);
    }

    public static BigDecimal asin (BigDecimal a, MathContext context) { // TODO to Haley's Method
        if (greaterThan(a.abs(), ONE)) {
            throw new ArithmeticException("Asin bounds between -1 and 1");
        }

        BigDecimal asin = ZERO;
        BigDecimal last = null;

        while (!asin.equals(last)) {
            last = asin;
            asin = asin.subtract(sin(asin, context).subtract(a).divide(cos(asin, context), context), context);
        }

        return asin;
    }

    public static BigDecimal acos (BigDecimal a, MathContext context) { // TODO to Haley's Method
        if (greaterThan(a.abs(), ONE)) {
            throw new ArithmeticException("Acos bounds between -1 and 1");
        }

        BigDecimal acos = ONE;
        BigDecimal last = null;

        while (!acos.equals(last)) {
            last = acos;
            acos = acos.subtract(cos(acos, context).subtract(a).divide(sin(acos, context), context).negate(), context);
        }

        return acos;
    }

    public static BigDecimal atan (BigDecimal a, MathContext context) {
        return asin(a.divide(a.multiply(a).add(ONE).sqrt(context), context), context);
    }

    public static BigDecimal atan2 (BigDecimal a, BigDecimal b, MathContext context) {
        return asin(b.divide(a.multiply(b.multiply(b).divide(a.multiply(a), context).add(ONE).sqrt(context)), context), context);
    }

    public static BigDecimal hypot (BigDecimal a, BigDecimal b, MathContext context) {
        return a.multiply(a).add(b.multiply(b)).sqrt(context);
    }

    public static BigDecimal agm (BigDecimal alpha, BigDecimal beta, MathContext context) {
        BigDecimal a = alpha;
        BigDecimal b = beta;

        while (!a.equals(b)) {
            a = a.add(b).divide(TWO, context);
            b = a.multiply(b).sqrt(context);
        }

        return a;
    }
}
