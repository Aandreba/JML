package org.jml.Calculus;

import org.jml.Calculus.Comb.Chain;
import org.jml.Calculus.Comb.Prod;
import org.jml.Calculus.Comb.Quot;
import org.jml.Calculus.Comb.Sum;

public interface Func {
    double apply (double x);
    default float apply (float x) {
        return (float) apply((double) x);
    }

    double deriv(double x);
    default float deriv(float x) {
        return (float) deriv((double) x);
    }

    default Sum add (Func beta) { return new Sum(1, this, 1, beta); }
    default Sum add (float delta, Func beta) { return new Sum(1, this, delta, beta); }
    default Sum add (float gamma, float delta, Func beta) { return new Sum(gamma, this, delta, beta); }
    default Sum subtr (Func beta) { return new Sum(1, this, -1, beta); }
    default Sum subtr (float delta, Func beta) { return new Sum(1, this, -delta, beta); }
    default Sum subtr (float gamma, float delta, Func beta) { return new Sum(gamma, this, -delta, beta); }
    default Prod mul (Func beta) { return new Prod(this, beta); }
    default Quot div (Func beta) { return new Quot(this, beta); }
    default Chain apply (Func output) {
        return new Chain(this, output);
    }
}
