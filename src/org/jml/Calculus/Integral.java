package org.jml.Calculus;

import org.jml.Mathx.Mathf;
import org.jml.Mathx.Rand;
import org.jml.Mathx.TaskManager;
import org.jml.Vector.Single.Vec;
import java.util.function.Function;

public class Integral {
    public static float integ(float a, float b, Function<Float, Float> function) {
        final double dx = (b - a) / 1e5;
        double area = 0;

        float lastY = function.apply(a);
        for (double x=a+dx;x<=b;x+=dx) {
            float y = function.apply((float) x);
            area += dx * (lastY + y) / 2;
            lastY = y;
        }

        return (float) area;
    }
}
