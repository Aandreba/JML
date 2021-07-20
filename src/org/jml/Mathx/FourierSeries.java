package org.jml.Mathx;

import org.jml.Calculus.Integral;
import org.jml.Complex.Single.Comp;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Single.Veci;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;

public class FourierSeries {
    public static Veci calculate (int length, Integral.ComplexFunction function) {
        return Veci.foreach(2 * length + 1, (int j) -> {
            int n = j - length;
            return Integral.integ(0, 1, (float t) -> Comp.expi(-Mathf.PI2 * n * t).mul(function.apply(t)));
        });
    }

    public static Veci calculate (int length, PathIterator path) {
        ArrayList<Comp> pointsList = new ArrayList<>();
        float[] point = new float[2];

        while (!path.isDone()) {
            path.currentSegment(point);
            path.next();
            pointsList.add(new Comp(point[0], point[1]));
        }

        Veci points = new Veci(pointsList.toArray(Comp[]::new));
        return Veci.foreach(2 * length + 1, (int j) -> {
            int n = j - length;
            Veci alpha =  Veci.foreach(points.size, (int k) -> {
                float t = (float) k / points.size;
                return pointsList.get(k).mul(Comp.expi(-Mathf.PI2 * n * t));
            });

            return alpha.mean();
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
