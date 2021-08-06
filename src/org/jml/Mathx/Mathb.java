package org.jml.Mathx;

import org.jml.Calculus.Integral;
import org.jml.Complex.Decimal.Compb;
import org.jml.Extra.Intx;
import org.jml.Extra.Pi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.function.Function;

public class Mathb {
    final public static BigInteger INT_ZERO = BigInteger.ZERO;
    final public static BigInteger INT_ONE = BigInteger.ONE;
    final public static BigInteger INT_TWO = BigInteger.valueOf(2);
    final public static BigInteger INT_THREE = BigInteger.valueOf(3);
    final public static BigInteger INT_FOUR = BigInteger.valueOf(4);
    final public static BigInteger INT_FIVE = BigInteger.valueOf(5);
    final public static BigInteger INT_SIX = BigInteger.valueOf(6);
    final public static BigInteger INT_SEVEN = BigInteger.valueOf(7);
    final public static BigInteger INT_EIGHT = BigInteger.valueOf(8);
    final public static BigInteger INT_NINE = BigInteger.valueOf(9);
    final public static BigInteger INT_TEN = BigInteger.TEN;

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
    final private static BigDecimal C27 = BigDecimal.valueOf(27);
    final private static BigDecimal PI180 = BigDecimal.valueOf(180);

    final private static HashMap<MathContext, BigDecimal> PI = new HashMap<>();
    final private static HashMap<MathContext, BigDecimal> E = new HashMap<>();
    final private static HashMap<MathContext, BigDecimal> SQRT2 = new HashMap<>();
    final private static HashMap<MathContext, BigDecimal> LN2 = new HashMap<>();

    final private static HashMap<MathContext, BigDecimal> TO_RADIANS = new HashMap<>();
    final private static HashMap<MathContext, BigDecimal> TO_DEGREES = new HashMap<>();

    final private static BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
    final private static BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

    public static boolean greaterThan (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    public static boolean greaterThan (BigInteger a, BigInteger b) {
        return a.compareTo(b) > 0;
    }

    public static boolean greaterOrEqual (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0;
    }

    public static boolean greaterOrEqual (BigInteger a, BigInteger b) {
        return a.compareTo(b) >= 0;
    }

    public static boolean lesserThan (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    public static boolean lesserThan (BigInteger a, BigInteger b) {
        return a.compareTo(b) < 0;
    }

    public static boolean lesserOrEqual (BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0;
    }

    public static boolean lesserOrEqual (BigInteger a, BigInteger b) {
        return a.compareTo(b) <= 0;
    }

    public static BigDecimal min (BigDecimal a, BigDecimal b) {
        return lesserOrEqual(a, b) ? a : b;
    }

    public static BigDecimal max (BigDecimal a, BigDecimal b) {
        return greaterOrEqual(a, b) ? a : b;
    }

    public static boolean isOdd (BigInteger a) {
        return a.testBit(0);
    }

    public static boolean isEven (BigInteger a) {
        return !isOdd(a);
    }

    public static boolean isInteger (BigDecimal a) {
        return a.scale() == 0;
    }

    public static BigDecimal pi (MathContext context) {
        BigDecimal pi = PI.get(context);
        if (pi == null) {
            pi = Pi.viete(context);
            TO_RADIANS.put(context, pi.divide(PI180, context));
            TO_DEGREES.put(context, PI180.divide(pi, context));
        }

        return pi;
    }

    public static BigDecimal e (MathContext context) {
        return exp(ONE, context);
    }

    public static BigDecimal toRadians (BigDecimal a, MathContext context) {
        BigDecimal conv = TO_RADIANS.get(context);
        if (conv == null) {
            BigDecimal pi = PI.compute(context, (c,d) -> Pi.viete(c));
            conv = TO_RADIANS.compute(context, (c,d) -> pi.divide(PI180, c));
            TO_DEGREES.put(context, PI180.divide(pi, context));
        }

        return a.multiply(conv, context);
    }

    public static BigDecimal toDegrees (BigDecimal a, MathContext context) {
        BigDecimal conv = TO_DEGREES.get(context);
        if (conv == null) {
            BigDecimal pi = PI.compute(context, (c,d) -> Pi.viete(c));
            conv = TO_DEGREES.compute(context, (c,d) -> PI180.divide(pi, c));
            TO_RADIANS.put(context, pi.divide(PI180, context));
        }

        return a.multiply(conv, context);
    }

    public static BigDecimal sqrt (BigDecimal a, MathContext context) {
        if (a.equals(TWO)) {
            return SQRT2.computeIfAbsent(context, TWO::sqrt);
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
        boolean isOne = value.equals(ONE);
        if (isOne) {
            BigDecimal e = E.get(context);
            if (e != null) {
                return e;
            }
        }

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

        if (isOne) {
            E.put(context, exp);
        }

        return exp;
    }

    public static BigDecimal log (BigDecimal a, MathContext context) {
        boolean isTwo = a.equals(TWO);
        if (isTwo) {
            BigDecimal ln2 = LN2.get(context);
            if (ln2 != null) {
                return ln2;
            }
        }

        BigDecimal log = a;
        BigDecimal last = null;

        MathContext superCtx = new MathContext(context.getPrecision() * 2, context.getRoundingMode());

        while (!log.equals(last)) {
            BigDecimal exp = exp(log, superCtx);
            BigDecimal expmt = exp.subtract(a);

            last = log;
            log = log.subtract(expmt.divide(exp.subtract(expmt.divide(TWO, context)), context), context);
        }

        if (isTwo) {
            LN2.put(context, log);
        }

        return log;
    }

    public static BigDecimal pow (BigDecimal a, BigDecimal b, MathContext context) {
        if (isInteger(b)) {
            return a.pow(b.intValue());
        }

        BigDecimal log = Mathb.log(a, context);
        return Mathb.exp(b.multiply(log), context);
    }

    public static BigDecimal sin (BigDecimal a, MathContext context) {
        if (a.equals(ZERO) | a.equals(Mathb.pi(context))) {
            return ZERO;
        }

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
        if (a.equals(ZERO)) {
            return ONE;
        } else if (a.equals(Mathb.pi(context))) {
            return ONE.negate();
        }

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

    public static BigDecimal asin (BigDecimal a, MathContext context) {
        if (greaterThan(a.abs(), ONE)) {
            throw new ArithmeticException("Asin bounds between -1 and 1");
        }

        MathContext superCtx = new MathContext(context.getPrecision() * 2, context.getRoundingMode());

        BigDecimal asin = HALF;
        BigDecimal last = null;

        while (!asin.equals(last)) {
            last = asin;
            asin = asin.subtract(sin(asin, superCtx).subtract(a).divide(cos(asin, superCtx), context), context);
        }

        return asin;
    }

    public static BigDecimal acos (BigDecimal a, MathContext context) {
        if (greaterThan(a.abs(), ONE)) {
            throw new ArithmeticException("Acos bounds between -1 and 1");
        }

        MathContext superCtx = new MathContext(context.getPrecision() * 2, context.getRoundingMode());

        BigDecimal acos = ONE;
        BigDecimal last = null;

        while (!acos.equals(last)) {
            last = acos;
            acos = acos.subtract(cos(acos, superCtx).subtract(a).divide(sin(acos, superCtx), context).negate(), context);
        }

        return acos;
    }

    public static BigDecimal atan (BigDecimal a, MathContext context) {
        return asin(a.divide(a.multiply(a).add(ONE).sqrt(context), context), context);
    }

    public static BigDecimal atan2 (BigDecimal b, BigDecimal a, MathContext context) {
        MathContext superCtx = new MathContext(context.getPrecision() * 2, context.getRoundingMode());

        if (!a.equals(ZERO)) {
            BigDecimal atan = atan(b.divide(a, superCtx), superCtx);
            if (greaterThan(a, ZERO)) {
                return atan;
            } else if (greaterOrEqual(b, ZERO)) {
                return atan.add(pi(context), context);
            } else {
                return atan.subtract(pi(context), context);
            }
        } else if (greaterThan(b, ZERO)) {
            return pi(context).divide(TWO, context);
        } else if (lesserThan(b, ZERO)) {
            return pi(context).divide(TWO, context).negate();
        }

        throw new ArithmeticException("Division by zero");
    }

    public static BigDecimal hypot (BigDecimal a, BigDecimal b, MathContext context) {
        return a.multiply(a).add(b.multiply(b)).sqrt(context);
    }

    public static BigDecimal sum (int from, int to, Function<Integer, BigDecimal> function) {
        BigDecimal sum = ZERO;
        for (int i=from;i<=to;i++) {
            sum = sum.add(function.apply(i));
        }

        return sum;
    }

    public static BigInteger factorial (BigInteger x) {
        if (lesserThan(x, INT_ZERO)) {
            throw new ArithmeticException("Factorial x >= 0");
        }

        BigInteger y = INT_ONE;
        for (BigInteger i=INT_ONE;lesserOrEqual(i,x);i=i.add(INT_ONE)) {
            y = y.multiply(i);
        }

        return y;
    }

    public static BigDecimal factorial (BigDecimal x, MathContext context) {
        BigDecimal y = ONE;
        BigDecimal last = ZERO;

        BigDecimal k = ONE;
        while (!y.equals(last)) {
            BigDecimal kp1 = k.add(ONE);

            last = y;
            y = y.multiply(pow(kp1.divide(k, context), x, context)).multiply(k.divide(x.add(k), context), context);
            k = kp1;
        }

        return y;
    }

    public static BigDecimal binomial (BigDecimal n, BigDecimal k, MathContext context) {
        return factorial(n, context).divide(factorial(k, context).multiply(factorial(n.subtract(k), context)), context);
    }

    public static Compb[] quadratic (BigDecimal a, BigDecimal b, BigDecimal c, MathContext context) {
        Compb[] x = new Compb[2];
        Compb sqrt = Compb.sqrt(b.pow(2).subtract(FOUR.multiply(a).multiply(c)), context);

        BigDecimal a2 = TWO.multiply(a);
        BigDecimal nb = b.negate();

        x[0] = sqrt.add(nb).div(a2);
        x[1] = sqrt.invSubtr(nb).div(a2);
        return x;
    }

    public static Compb[] cubic (BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d, MathContext context) {
        final Compb zeta = Compb.sqrt(THREE.negate(), context).subtr(ONE).div(TWO);
        Compb[] x = new Compb[3];

        BigDecimal b2 = b.pow(2);
        BigDecimal d0 = b2.subtract(THREE.multiply(a).multiply(c));
        BigDecimal d1 = TWO.multiply(b2).multiply(b).subtract(NINE.multiply(a).multiply(b).multiply(c)).add(C27.multiply(a.pow(2)).multiply(d));

        Compb C = Compb.sqrt(d1.pow(2).subtract(FOUR.multiply(d0.pow(3))), context).add(d1).div(TWO).cbrt();
        Compb pow = Compb.ONE.toContext(context);
        BigDecimal k = ONE.negate().divide(THREE.multiply(a), context);

        for (int i=0;i<3;i++) {
            pow = pow.mul(zeta);
            Compb u = pow.mul(C);

            x[i] = u.add(b).add(u.invDiv(d0)).mul(k);
        }

        return x;
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
