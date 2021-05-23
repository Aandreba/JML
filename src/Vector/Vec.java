package Vector;

import Matrix.Mat;
import References.Double.Ref1D;
import java.util.Iterator;

public class Vec implements Ref1D {
    final protected double[] values;

    public Vec (int size) {
        this.values = new double[size];
    }

    public Vec (double... values) {
        this.values = values;
    }

    public Vec (Vec initialValues, double... finalValues) {
        int vecSize = initialValues.getSize();
        this.values = new double[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Vec (double[] initialValues, Vec finalValues) {
        int vecSize = finalValues.getSize();
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

    public int getSize () {
        return values.length;
    }

    public double get (int pos) {
        return this.values[pos];
    }

    public void set (int pos, double value) {
        this.values[pos] = value;
    }

    private int finalLen (Vec b) {
        return Math.min(getSize(), b.getSize());
    }

    public Vec forEach (Vec b, VecForEach forEach) {
        int size = finalLen(b);
        Vec vector = new Vec(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vec forEach (double b, VecForEach forEach) {
        int size = getSize();
        Vec vector = new Vec(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getSize();
            }

            @Override
            public Double next() {
                return get(i++);
            }
        };
    }

    public static Vec forEach (int size, VecForEachIndex forEach) {
        Vec vector = new Vec(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vec add (Vec b) {
        return forEach(b, Double::sum);
    }

    public Vec add (double b) {
        return forEach(b, Double::sum);
    }

    public Vec subtr (Vec b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Vec subtr (double b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Vec invSubtr (double b) {
        return forEach(b, (x, y) -> y - x);
    }

    public Vec mul (Vec b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Vec mul (double b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Vec div (Vec b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Vec div (double b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Vec invDiv(double b) {
        return forEach(b, (x, y) -> y / x);
    }

    public double magnitude2 () {
        double sum = 0;
        for (int i=0;i<getSize();i++) {
            sum += get(i) * get(i);
        }

        return sum;
    }

    public double magnitude () {
        return Math.sqrt(magnitude2());
    }

    public Vec unit () {
        return div(magnitude());
    }

    public double dot (Vec b) {
        int len = finalLen(b);
        double sum = 0;

        for (int i=0;i<len;i++) {
            sum += get(i) * b.get(i);
        }

        return sum;
    }

    public Vec cross (Vec b) {
        if (finalLen(b) < 3) {
            throw new ArithmeticException("Tried to do cross product with vectors of size smaller than 3");
        }

        Vec vector = new Vec(3);
        vector.values[0] = get(1) * b.get(2) - get(2) * b.get(1);
        vector.values[1] = get(2) * b.get(0) - get(0) * b.get(2);
        vector.values[2] = get(0) * b.get(1) - get(1) * b.get(0);

        return vector;
    }

    public double sum () {
        double val = 0;
        for (int i=0;i<getSize();i++) {
            val += get(i);
        }

        return val;
    }

    public double mean () {
        return sum() / getSize();
    }

    public Vec sub (int... pos) { // Subvector
        Vec vector = new Vec(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    public Vecf toFloat () {
        Vecf vector = new Vecf(getSize());
        for (int i=0;i<getSize();i++) {
            vector.set(i, (float) get(i));
        }

        return vector;
    }

    public Mat rowMatrix () {
        return new Mat(this);
    }

    public Mat colMatrix () {
        return rowMatrix().T();
    }

    public static Vec fromRef (Ref1D ref) {
        return ref instanceof Vec ? (Vec) ref : forEach(ref.getSize(), ref::get);
    }

    @Override
    public Vec clone() {
        return new Vec(values.clone());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<getSize();i++) {
            builder.append(", ").append(get(i));
        }

        return "{ " + builder.substring(2) + " }";
    }
}
