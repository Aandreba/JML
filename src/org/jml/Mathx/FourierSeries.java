package org.jml.Mathx;

import org.jml.Calculus.Integral;
import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.Function.Complex.SingleComplex;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class FourierSeries {
    public static Veci calculate (int length, ComplexFunction function) {
        return Veci.foreach(2 * length + 1, (int j) -> {
            int n = j - length;
            return Integral.integ(0, 1, (SingleComplex) (float t) -> Comp.expi(-Mathf.PI2 * n * t).mul(function.apply(t)));
        });
    }

    public static Veci calculate (int length, PathIterator path) {
        return calculate(length, pathToFunction(path));
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

    public static SingleComplex pathToFunction (PathIterator path) {
        ArrayList<float[]> points = new ArrayList<>();
        ArrayList<Integer> types = new ArrayList<>();

        while (!path.isDone()) {
            float[] point = new float[6];
            int type = path.currentSegment(point);
            path.next();

            points.add(point);
            types.add(type);
        }

        float[] fst = points.get(0);
        Comp first = new Comp(fst[0], fst[1]);

        return (float t) -> {
            float j = (points.size() * t);
            int n = (int) j; // To
            if (n == 0) {
                return first;
            }

            int k = n - 1; // From
            float _t = j - n;
            float _1mt = 1 - _t;

            float[] target = points.get(n);
            float[] prev = points.get(k);

            Comp lastPos = new Comp(prev[prev.length-2], prev[prev.length-1]);
            int type = types.get(n);

            return switch (type) {
                case PathIterator.SEG_CUBICTO -> {
                    float _1mt_2 = _1mt * _1mt;
                    float _t2 = _t * _t;

                    Comp alpha = new Comp(target[0], target[1]);
                    Comp beta = new Comp(target[2], target[3]);
                    Comp gamma = new Comp(target[4], target[5]);

                    yield lastPos.mul(_1mt_2 * _1mt)
                            .add(alpha.mul(3 * _1mt_2 * _t))
                            .add(beta.mul(3 * _1mt * _t2))
                            .add(gamma.mul(_t2 * _t));
                }

                case PathIterator.SEG_QUADTO -> {
                    Comp alpha = new Comp(target[0], target[1]);
                    Comp beta = new Comp(target[2], target[3]);

                    yield lastPos.mul(_1mt * _1mt)
                            .add(alpha.mul(2 * _1mt))
                            .add(beta.mul(_t * _t));
                }

                case PathIterator.SEG_MOVETO -> new Comp(target[0], target[1]);
                case PathIterator.SEG_LINETO -> lastPos.mul(j-k).add(new Comp(target[0], target[1]).mul(n-j));
                case PathIterator.SEG_CLOSE -> {
                    Comp lastMoveTo;
                    int q = n;

                    while (true) {
                        q--;
                        int qType = types.get(q);
                        if (qType == PathIterator.SEG_MOVETO) {
                            float[] qPoints = points.get(q);
                            lastMoveTo = new Comp(qPoints[0], qPoints[1]);
                            break;
                        }
                    }

                    yield lastPos.mul(j-k).add(lastMoveTo.mul(n-j));
                }

                default -> throw new IllegalArgumentException();
            };
        };
    }
}
