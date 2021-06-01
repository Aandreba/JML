package Vector.Single;

import GPGPU.OpenCL.Context;
import Mathx.Mathf;
import Matrix.Single.Mat;
import References.Single.Ref1Df;
import Vector.Double.Vecd;
import Vector.Double.Vecid;

import java.util.Arrays;

public class Vec implements Ref1Df {
    final protected float[] values;

    public Vec(int size) {
        this.values = new float[size];
    }

    public Vec(float... values) {
        this.values = values;
    }

    public Vec(Vec initialValues, float... finalValues) {
        int vecSize = initialValues.getSize();
        this.values = new float[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Vec(float[] initialValues, Vec finalValues) {
        int vecSize = finalValues.getSize();
        this.values = new float[initialValues.length + vecSize];

        for (int i=0;i<initialValues.length;i++) {
            this.values[i] = initialValues[i];
        }

        for (int i=0;i<vecSize;i++) {
            this.values[initialValues.length + i] = finalValues.get(i);
        }
    }

    public interface VecfForEach {
        float apply (float a, float b);
    }

    public interface VecfForEachIndex {
        float apply (int pos);
    }

    public interface VecfForEachValue {
        float apply (float value);
    }

    public int getSize () {
        return values.length;
    }

    public float get (int pos) {
        return this.values[pos];
    }

    public void set (int pos, float value) {
        this.values[pos] = value;
    }

    private int finalLen (Vec b) {
        return Math.min(getSize(), b.getSize());
    }

    public Vec forEach (Vec b, VecfForEach forEach) {
        int size = finalLen(b);
        Vec vector = new Vec(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vec forEach (float b, VecfForEach forEach) {
        int size = getSize();
        Vec vector = new Vec(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Vec forEach (VecfForEachValue forEach) {
        int size = getSize();
        Vec vector = new Vec(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i)));
        }

        return vector;
    }

    public static Vec forEach (int size, VecfForEachIndex forEach) {
        Vec vector = new Vec(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vec add (Vec b) {
        return forEach(b, Float::sum);
    }

    public Vec add (float b) {
        return forEach(b, Float::sum);
    }

    public Vec subtr (Vec b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Vec subtr (float b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Vec invSubtr (float b) {
        return forEach(b, (x, y) -> y - x);
    }

    public Vec mul (Vec b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Vec mul (float b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Vec div (Vec b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Vec div (float b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Vec invDiv(float b) {
        return forEach(b, (x, y) -> y / x);
    }

    public float magnitude2 () {
        float sum = 0;
        for (int i=0;i<getSize();i++) {
            sum += get(i) * get(i);
        }

        return sum;
    }

    public float magnitude () {
        return Mathf.sqrt(magnitude2());
    }

    public Vec unit () {
        return div(magnitude());
    }

    public float dot (Vec b) {
        int len = finalLen(b);
        float sum = 0;

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

    public float sum () {
        float val = 0;
        for (int i=0;i<getSize();i++) {
            val += get(i);
        }

        return val;
    }

    public float mean () {
        return sum() / getSize();
    }

    public Vec sub (int... pos) {
        Vec vector = new Vec(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    @Override
    public float[] toArray() {
        return values.clone();
    }

    public Vecd toDouble () {
        Vecd vector = new Vecd(getSize());
        for (int i=0;i<getSize();i++) {
            vector.set(i, get(i));
        }

        return vector;
    }

    @Override
    public Veci toComplex() {
        return new Veci(values);
    }

    public VecCL toCL (Context context) {
        return new VecCL(context, this);
    }

    public VecCL toCL() {
        return toCL(Context.DEFAULT);
    }

    public VecCUDA toCUDA () {
        return new VecCUDA(this);
    }

    public Mat rowMatrix () {
        return new Mat(this);
    }

    public Mat colMatrix () {
        return rowMatrix().T();
    }

    public static Vec fromRef (Ref1Df ref) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec floats = (Vec) o;
        return Arrays.equals(values, floats.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
