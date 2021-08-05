package org.jml.Function.Real;

import org.jml.Calculus.Derivative;
import org.jml.Calculus.Integral;
import org.jml.Mathx.Mathb;
import org.jml.Mathx.Mathf;

import java.math.BigDecimal;
import java.math.MathContext;

public interface RealFunction {
    RealFunction EXP = new RealFunction() {
        @Override
        public float apply (float x) {
            return Mathf.exp(x);
        }

        @Override
        public double apply (double x) {
            return Math.exp(x);
        }

        @Override
        public BigDecimal apply (BigDecimal x, MathContext context) {
            return Mathb.exp(x, context);
        }
    };

    RealFunction SQRT = new RealFunction() {
        @Override
        public float apply(float x) {
            return Mathf.sqrt(x);
        }

        @Override
        public double apply(double x) {
            return Math.sqrt(x);
        }

        @Override
        public BigDecimal apply(BigDecimal x, MathContext context) {
            return x.sqrt(context);
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
    default RealFunction deriv () {
        return new RealFunction() {
            @Override
            public float apply(float x) {
                return RealFunction.this.deriv(x);
            }

            @Override
            public double apply(double x) {
                return RealFunction.this.deriv(x);
            }

            @Override
            public BigDecimal apply(BigDecimal x, MathContext context) {
                return RealFunction.this.deriv(x, context);
            }
        };
    }

    default float integ (float a, float b) { return Integral.integ(a, b, this); }
    default double integ (double a, double b) { return Integral.integ(a, b, this); }
    default BigDecimal integ (BigDecimal a, BigDecimal b, MathContext context) { return Integral.integ(a, b, this, context); }
    default RealFunction integ () {
        return new RealFunction() {
            @Override
            public float apply(float x) {
                return RealFunction.this.integ(0, x);
            }

            @Override
            public double apply(double x) {
                return RealFunction.this.integ(0, x);
            }

            @Override
            public BigDecimal apply(BigDecimal x, MathContext context) {
                return RealFunction.this.integ(Mathb.ZERO, x, context);
            }
        };
    }
}
