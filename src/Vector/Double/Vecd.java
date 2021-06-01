package Vector.Double;

import GPGPU.OpenCL.Context;
import Matrix.Double.Matd;
import References.Double.Ref1D;
import Vector.Single.Vec;

public class Vecd implements Ref1D {
    final protected double[] values;

    public Vecd(int size) {
        this.values = new double[size];
    }

    public Vecd(double... values) {
        this.values = values;
    }

    public Vecd(Vecd initialValues, double... finalValues) {
        int vecSize = initialValues.getSize();
        this.values = new double[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Vecd(double[] initialValues, Vecd finalValues) {
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

    public interface VecForEachValue {
        double apply (double value);
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

    private int finalLen (Vecd b) {
        return Math.min(getSize(), b.getSize());
    }

    public Vecd forEach (Vecd b, VecForEach forEach) {
        int size = finalLen(b);
        Vecd vector = new Vecd(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vecd forEach (double b, VecForEach forEach) {
        int size = getSize();
        Vecd vector = new Vecd(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Vecd forEach (VecForEachValue forEach) {
        int size = getSize();
        Vecd vector = new Vecd(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i)));
        }

        return vector;
    }

    public static Vecd forEach (int size, VecForEachIndex forEach) {
        Vecd vector = new Vecd(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vecd add (Vecd b) {
        return forEach(b, Double::sum);
    }

    public Vecd add (double b) {
        return forEach(b, Double::sum);
    }

    public Vecd subtr (Vecd b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Vecd subtr (double b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Vecd invSubtr (double b) {
        return forEach(b, (x, y) -> y - x);
    }

    public Vecd mul (Vecd b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Vecd mul (double b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Vecd div (Vecd b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Vecd div (double b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Vecd invDiv(double b) {
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
        for (int i=0;i<getSize();i++) {
            val += get(i);
        }

        return val;
    }

    public double mean () {
        return sum() / getSize();
    }

    public Vecd sub (int... pos) { // Subvector
        Vecd vector = new Vecd(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    @Override
    public double[] toArray() {
        return values.clone();
    }

    public Vec toFloat () {
        Vec vector = new Vec(getSize());
        for (int i=0;i<getSize();i++) {
            vector.set(i, (float) get(i));
        }

        return vector;
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

    public static Vecd fromRef (Ref1D ref) {
        return ref instanceof Vecd ? (Vecd) ref : forEach(ref.getSize(), ref::get);
    }

    @Override
    public Vecd clone() {
        return new Vecd(values.clone());
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