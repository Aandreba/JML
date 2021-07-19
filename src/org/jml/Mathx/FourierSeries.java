package org.jml.Mathx;

import org.jml.Calculus.Integral;
import org.jml.Complex.Single.Comp;
import org.jml.Vector.Single.Veci;

public class FourierSeries {
    public static Veci calculate(int length, Integral.ComplexFunction function) {
        return Veci.foreach(2 * length + 1, (int j) -> {
            int n = j - length;
            return Integral.integ(0, 1, (float t) -> Comp.expi(-Mathf.PI2 * n * t).mul(function.apply(t)));
        });
    }

    public static Comp compute (float time, Veci rotators) {
        int len = (rotators.size - 1) / 2;
        Comp sum = Comp.ZERO;

        for (int i=0;i<rotators.size;i++) {
            int j = i - len;
            sum = sum.add(rotators.get(i).mul(Comp.expi(j * Mathf.PI2 * time)));
        }

        return sum;
    }
}
