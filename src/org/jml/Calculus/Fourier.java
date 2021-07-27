package org.jml.Calculus;

import org.jml.Calculus.Integral;
import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.SingleComplex;
import org.jml.Function.Real.RealFunction;
import org.jml.Function.Real.SingleReal;
import org.jml.Mathx.Mathf;
import org.jml.MT.TaskManager;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

public class Fourier {
    final private static float ALPHA = -Mathf.PI2;

    /**
     * Performs Fast Fourier Transform (FTT) on specified values
     * @see <a href="http://rosettacode.org/wiki/Fast_Fourier_transform#Java">Original: Rosetta Code</a>
     */
    public static Veci fast (Veci values) {
        Veci buffer = values.clone();
        int bits = (int) Mathf.log2(buffer.size);

        for (int j = 1; j < buffer.size / 2; j++) {
            int swapPos = bitReverse(j, bits);
            Comp temp = buffer.get(j);
            buffer.set(j, buffer.get(swapPos));
            buffer.set(swapPos, temp);
        }

        TaskManager tasks = new TaskManager();
        for (int N = 2; N <= buffer.size; N <<= 1) {
            float alpha = ALPHA / N;
            int beta = N / 2;

            for (int i = 0; i < buffer.size; i += N) {
                int finalI = i;

                for (int k = 0; k < N / 2; k++) {
                    int finalK = k;

                    tasks.add(() -> {
                        int evenIndex = finalI + finalK;
                        int oddIndex = evenIndex + beta;
                        Comp even = buffer.get(evenIndex);
                        Comp odd = buffer.get(oddIndex);

                        float term = alpha * finalK;
                        Comp exp = (new Comp(Mathf.cos(term), Mathf.sin(term)).mul(odd));

                        buffer.set(evenIndex, even.add(exp));
                        buffer.set(oddIndex, even.subtr(exp));
                    });
                }
            }
        }

        tasks.run();
        return buffer;
    }

    public static Veci fast(Vec values) {
        return fast(values.toComplex());
    }

    public static Veci fast(Comp... values) {
        return fast(new Veci(values));
    }

    public static Veci fast(float... values) {
        return fast(new Veci(values));
    }

    public static Comp transf (float a, float b, float freq, RealFunction func) {
        float beta = ALPHA * freq;
        return Integral.integ(a, b, (SingleComplex) (float x) -> Comp.expi(beta * x).mul(func.apply(x)));
    }

    public static Comp transf (float a, float b, RealFunction func) {
        return Integral.integ(a, b, (SingleComplex) (float x) -> Comp.expi(ALPHA * x).mul(func.apply(x)));
    }

    private static int bitReverse (int n, int bits) {
        int reversedN = n;
        int count = bits - 1;

        n >>= 1;
        while (n > 0) {
            reversedN = (reversedN << 1) | (n & 1);
            count--;
            n >>= 1;
        }

        return ((reversedN << count) & ((1 << bits) - 1));
    }
}
