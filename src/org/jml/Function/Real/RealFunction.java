package org.jml.Function.Real;

import org.jml.Calculus.Derivative;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.BigDecimal;
import java.math.MathContext;

public interface RealFunction {
    public static RealFunction EXP = new RealFunction() {
        @Override
        public float apply (float x) {
            return Mathf.exp(x);
        }

        @Override
        public double apply (double x) {
            return Math.exp(x);
        }

        @Override
        public BigDecimal apply(BigDecimal x, MathContext context) {
            return Mathb.exp(x, context);
        }
    };

    float apply (float x);
    double apply (double x);
    BigDecimal apply (BigDecimal x, MathContext context);

    default float deriv (float x) {
        return Derivative.deriv(x, this);
    }

    default double deriv(double x) {
        return Derivative.deriv(x, this);
    }

    default BigDecimal deriv (BigDecimal x, MathContext context) {
        return Derivative.deriv(x, context, this);
    }

    default SingleReal singleDeriv () {
        return this::deriv;
    }

    default DoubleReal doubleDeriv () {
        return this::deriv;
    }

    default BigReal bigDeriv (MathContext context) {
        return this::deriv;
    }
}
