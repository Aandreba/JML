package Vector.Single;

import GPGPU.OpenCL.Context;
import Mathx.Mathf;
import Matrix.Single.Matf;
import References.Single.Ref1Df;
import Vector.Double.Vec;

public class Vecf implements Ref1Df {
    final protected float[] values;

    public Vecf(int size) {
        this.values = new float[size];
    }

    public Vecf(float... values) {
        this.values = values;
    }

    public Vecf(Vecf initialValues, float... finalValues) {
        int vecSize = initialValues.getSize();
        this.values = new float[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Vecf(float[] initialValues, Vecf finalValues) {
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

    private int finalLen (Vecf b) {
        return Math.min(getSize(), b.getSize());
    }

    public Vecf forEach (Vecf b, VecfForEach forEach) {
        int size = finalLen(b);
        Vecf vector = new Vecf(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vecf forEach (float b, VecfForEach forEach) {
        int size = getSize();
        Vecf vector = new Vecf(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Vecf forEach (VecfForEachValue forEach) {
        int size = getSize();
        Vecf vector = new Vecf(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i)));
        }

        return vector;
    }

    public static Vecf forEach (int size, VecfForEachIndex forEach) {
        Vecf vector = new Vecf(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vecf add (Vecf b) {
        return forEach(b, Float::sum);
    }

    public Vecf add (float b) {
        return forEach(b, Float::sum);
    }

    public Vecf subtr (Vecf b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Vecf subtr (float b) {
        return forEach(b, (x, y) -> x - y);
    }

    public Vecf invSubtr (float b) {
        return forEach(b, (x, y) -> y - x);
    }

    public Vecf mul (Vecf b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Vecf mul (float b) {
        return forEach(b, (x, y) -> x * y);
    }

    public Vecf div (Vecf b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Vecf div (float b) {
        return forEach(b, (x, y) -> x / y);
    }

    public Vecf invDiv(float b) {
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

    public Vecf unit () {
        return div(magnitude());
    }

    public float dot (Vecf b) {
        int len = finalLen(b);
        float sum = 0;

        for (int i=0;i<len;i++) {
            sum += get(i) * b.get(i);
        }

        return sum;
    }

    public Vecf cross (Vecf b) {
        if (finalLen(b) < 3) {
            throw new ArithmeticException("Tried to do cross product with vectors of size smaller than 3");
        }

        Vecf vector = new Vecf(3);
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

    public Vecf sub (int... pos) {
        Vecf vector = new Vecf(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    @Override
    public float[] toArray() {
        return values.clone();
    }

    public Vec toDouble () {
        Vec vector = new Vec(getSize());
        for (int i=0;i<getSize();i++) {
            vector.set(i, get(i));
        }

        return vector;
    }

    public VecCLf toCL (Context context) {
        return new VecCLf(context, this);
    }

    public VecCLf toCL() {
        return toCL(Context.DEFAULT);
    }

    public VecCUDAf toCUDA () {
        return new VecCUDAf(this);
    }

    public Matf rowMatrix () {
        return new Matf(this);
    }

    public Matf colMatrix () {
        return rowMatrix().T();
    }

    public static Vecf fromRef (Ref1Df ref) {
        return ref instanceof Vecf ? (Vecf) ref : forEach(ref.getSize(), ref::get);
    }

    @Override
    public Vecf clone() {
        return new Vecf(values.clone());
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
