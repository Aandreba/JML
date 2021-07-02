package org.jml.Vector.Double;

import org.jml.Complex.Single.Comp;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.Mathx.Extra.Doublex;
import org.jml.Mathx.Mathf;
import org.jml.Matrix.Double.Matd;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public class Vecd implements Iterable<Double> {
    final protected double[] values;

    public Vecd(int size) {
        this.values = new double[size];
    }

    public Vecd(double... values) {
        this.values = values;
    }

    public Vecd(Vecd initialValues, double... finalValues) {
        int vecSize = initialValues.size();
        this.values = new double[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Vecd(double[] initialValues, Vecd finalValues) {
        int vecSize = finalValues.size();
        this.values = new double[initialValues.length + vecSize];

        for (int i=0;i<initialValues.length;i++) {
            this.values[i] = initialValues[i];
        }

        for (int i=0;i<vecSize;i++) {
            this.values[initialValues.length + i] = finalValues.get(i);
        }
    }

    public interface VecForEach {
        double apply (double a, double b);
    }

    public interface VecForEachIndex {
        double apply (int pos);
    }

    public interface VecForEachValue {
        double apply (double value);
    }

    public int size () {
        return values.length;
    }

    public double get (int pos) {
        return this.values[pos];
    }

    public void set (Vecd values) {
        if (values.size() != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i=0;i<size();i++) {
            this.values[i] = values.get(i);
        }
    }

    public void set (double... values) {
        if (values.length != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i=0;i<size();i++) {
            this.values[i] = values[i];
        }
    }

    public void set (int pos, double value) {
        this.values[pos] = value;
    }

    private int finalLen (Vecd b) {
        return Math.min(size(), b.size());
    }

    public Vecd foreach(Vecd b, VecForEach forEach) {
        int size = finalLen(b);
        Vecd vector = new Vecd(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vecd foreach(double b, VecForEach forEach) {
        int size = size();
        Vecd vector = new Vecd(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Vecd foreach(VecForEachValue forEach) {
        int size = size();
        Vecd vector = new Vecd(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i)));
        }

        return vector;
    }

    public static Vecd foreach(int size, VecForEachIndex forEach) {
        Vecd vector = new Vecd(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vecd add (Vecd b) {
        return foreach(b, Double::sum);
    }

    public Vecd add (double b) {
        return foreach(b, Double::sum);
    }

    public Vecd subtr (Vecd b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Vecd subtr (double b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Vecd invSubtr (double b) {
        return foreach(b, (x, y) -> y - x);
    }

    public Vecd mul (Vecd b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Vecd mul (double b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Vecd div (Vecd b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Vecd div (double b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Vecd invDiv(double b) {
        return foreach(b, (x, y) -> y / x);
    }

    public double magnitude2 () {
        double sum = 0;
        for (int i = 0; i< size(); i++) {
            sum += get(i) * get(i);
        }

        return sum;
    }

    public double magnitude () {
        return Math.sqrt(magnitude2());
    }

    public Vecd unit () {
        return div(magnitude());
    }

    public double dot (Vecd b) {
        int len = finalLen(b);
        double sum = 0;

        for (int i=0;i<len;i++) {
            sum += get(i) * b.get(i);
        }

        return sum;
    }

    public Vecd cross (Vecd b) {
        if (finalLen(b) < 3) {
            throw new ArithmeticException("Tried to do cross product with vectors of size smaller than 3");
        }

        Vecd vector = new Vecd(3);
        vector.values[0] = get(1) * b.get(2) - get(2) * b.get(1);
        vector.values[1] = get(2) * b.get(0) - get(0) * b.get(2);
        vector.values[2] = get(0) * b.get(1) - get(1) * b.get(0);

        return vector;
    }

    public double sum () {
        double val = 0;
        for (int i = 0; i< size(); i++) {
            val += get(i);
        }

        return val;
    }

    public double mean () {
        return sum() / size();
    }

    public Vecd abs () {
        return foreach(Math::abs);
    }

    public int minIndex () {
        int min = 0;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || get(i) < get(min)) {
                min = i;
            }
        }

        return min;
    }

    public double min () {
        double min = 0;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || get(i) < min) {
                min = get(i);
            }
        }

        return min;
    }

    public int maxIndex () {
        int max = 0;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || get(i) > get(max)) {
                max = i;
            }
        }

        return max;
    }

    public double max () {
        double max = 0;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || get(i) > max) {
                max = get(i);
            }
        }

        return max;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public Double next() {
                return get(i++);
            }
        };
    }

    public boolean any (Function<Double, Boolean> cond) {
        for (double value: this) {
            if (cond.apply(value)) {
                return true;
            }
        }

        return false;
    }

    public boolean all (Function<Double, Boolean> cond) {
        for (double value: this) {
            if (!cond.apply(value)) {
                return false;
            }
        }

        return true;
    }

    public Vecd sub (int... pos) { // Subvector
        Vecd vector = new Vecd(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    
    public double[] toArray() {
        return values.clone();
    }

    public Vec toFloat () {
        Vec vector = new Vec(size());
        for (int i = 0; i< size(); i++) {
            vector.set(i, (float) get(i));
        }

        return vector;
    }

    public Vecid toComplex() {
        return new Vecid(values);
    }

    public VecCLd toCL(Context context) {
        return new VecCLd(context, this);
    }

    public VecCLd toCL() {
        return toCL(Context.DEFAULT);
    }

    public Matd rowMatrix () {
        return new Matd(this);
    }

    public Matd colMatrix () {
        return rowMatrix().T();
    }

    public Matd rowMajor (int cols) {
        int rows = size() / cols;
        return Matd.foreach(rows, cols, (i,j) -> get((i * cols) + j));
    }

    public Matd colMajor (int rows) {
        int cols = size() / rows;
        return Matd.foreach(rows, cols, (i,j) -> get((j * rows) + 1));
    }
    
    public Vecd clone() {
        return new Vecd(values.clone());
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< size(); i++) {
            builder.append(", ").append(get(i));
        }

        return "{ " + builder.substring(2) + " }";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vecd doubles = (Vecd) o;
        return Arrays.equals(values, doubles.values);
    }

    
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
