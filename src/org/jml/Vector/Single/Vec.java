package org.jml.Vector.Single;
;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.Link.Single.Link1D;
import org.jml.Mathx.Mathf;
import org.jml.Matrix.Single.Mat;
import org.jml.Vector.Double.Vecd;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class Vec extends Link1D implements Serializable {
    final protected float[] values;

    public Vec(int size) {
        super(size);
        this.values = new float[size];
    }

    public Vec(float... values) {
        super(values.length);
        this.values = values;
    }

    public Vec(Vec initialValues, float... finalValues) {
        super(initialValues.size() + finalValues.length);

        int vecSize = initialValues.size();
        this.values = new float[size];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Vec(float[] initialValues, Vec finalValues) {
        super(initialValues.length + finalValues.size);

        int vecSize = finalValues.size();
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

    public int size () {
        return size;
    }

    public float get (int pos) {
        return this.values[pos];
    }

    public void set (Vec values) {
        if (values.size() != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values.values, 0, this.values, 0, size());
    }

    public void set (float... values) {
        if (values.length != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values, 0, this.values, 0, size());
    }

    public void set (int offsetSrc, int offsetDest, int length, Vec values) {
        if (offsetDest + length > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values.values, offsetSrc, this.values, offsetDest, length);
    }

    public void set (int offsetSrc, int offsetDest, Vec values) {
        if (offsetDest + values.size() > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values.values, offsetSrc, this.values, offsetDest, values.size() - offsetSrc);
    }

    public void set (int offsetSrc, int offsetDest, int length, float... values) {
        if (offsetDest + length > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values, offsetSrc, this.values, offsetDest, length);
    }

    public void set (int offsetSrc, int offsetDest, float... values) {
        if (offsetDest + values.length > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values, offsetSrc, this.values, offsetDest, values.length - offsetSrc);
    }

    public void set (int pos, float value) {
        this.values[pos] = value;
    }

    private int finalLen (Vec b) {
        return Math.min(size(), b.size());
    }

    public Vec foreach(Vec b, VecfForEach forEach) {
        int size = finalLen(b);
        Vec vector = new Vec(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vec foreach(float b, VecfForEach forEach) {
        int size = size();
        Vec vector = new Vec(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Vec foreach(VecfForEachValue forEach) {
        int size = size();
        Vec vector = new Vec(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i)));
        }

        return vector;
    }

    public static Vec foreach(int size, VecfForEachIndex forEach) {
        Vec vector = new Vec(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vec add (Vec b) {
        return foreach(b, Float::sum);
    }

    public Vec add (float b) {
        return foreach(b, Float::sum);
    }

    public Vec subtr (Vec b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Vec subtr (float b) {
        return foreach(b, (x, y) -> x - y);
    }

    public Vec invSubtr (float b) {
        return foreach(b, (x, y) -> y - x);
    }

    public Vec mul (Vec b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Vec mul (float b) {
        return foreach(b, (x, y) -> x * y);
    }

    public Vec div (Vec b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Vec div (float b) {
        return foreach(b, (x, y) -> x / y);
    }

    public Vec invDiv(float b) {
        return foreach(b, (x, y) -> y / x);
    }

    public float magnitude2 () {
        float sum = 0;
        for (int i = 0; i< size(); i++) {
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
        for (int i = 0; i< size(); i++) {
            val += get(i);
        }

        return val;
    }

    public Vec abs () {
        return foreach(size(), i -> Mathf.abs(get(i)));
    }

    public float mean () {
        return sum() / size();
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

    public float min () {
        float min = 0;
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

    public float max () {
        float max = 0;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || get(i) > max) {
                max = get(i);
            }
        }

        return max;
    }

    @Override
    public Iterator<Float> iterator() {
        return new Iterator<Float>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public Float next() {
                return get(i++);
            }
        };
    }

    public boolean any (Function<Float, Boolean> cond) {
        for (float value: this) {
            if (cond.apply(value)) {
                return true;
            }
        }

        return false;
    }

    public boolean all (Function<Float, Boolean> cond) {
        for (float value: this) {
            if (!cond.apply(value)) {
                return false;
            }
        }

        return true;
    }

    public Vec sub (int... pos) {
        Vec vector = new Vec(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    public Vec sub (int offset, int length) {
        float[] vals = new float[length];
        System.arraycopy(values, offset, vals, 0, length);

        return new Vec(vals);
    }

    public float[] toArray() {
        return values.clone();
    }

    public Vecd toDouble () {
        Vecd vector = new Vecd(size());
        for (int i = 0; i< size(); i++) {
            vector.set(i, get(i));
        }

        return vector;
    }

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

    public Mat rowMajor (int cols) {
        int rows = size() / cols;
        return Mat.foreach(rows, cols, (i,j) -> get((i * cols) + j));
    }

    public Mat colMajor (int rows) {
        int cols = size() / rows;
        return Mat.foreach(rows, cols, (i,j) -> get((j * rows) + 1));
    }

    public Vec clone() {
        return new Vec(values.clone());
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec floats = (Vec) o;
        return Arrays.equals(values, floats.values);
    }

    
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
