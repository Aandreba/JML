package org.jml.Vector.Single;

import org.jml.GPGPU.OpenCL.Context;
import org.jml.Complex.Single.Comp;
import org.jml.Link.Single.Link1Di;
import org.jml.Mathx.Mathf;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Double.Vecid;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public class Veci extends Link1Di {
    final protected Comp[] values;

    public Veci(int size) {
        super(size);
        this.values = new Comp[size];

        for (int i=0;i<size;i++) {
            this.values[i] = Comp.ZERO.clone();
        }
    }

    public Veci(Comp... values) {
        super(values.length);
        this.values = values;
    }

    public Veci(float... values) {
        this(values.length);
        for (int i=0;i<values.length;i++) {
            set(i, new Comp(values[i], 0));
        }
    }

    public Veci(Veci initialValues, Comp... finalValues) {
        super(initialValues.size + finalValues.length);
        int vecSize = initialValues.size();
        this.values = new Comp[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        System.arraycopy(finalValues, 0, this.values, vecSize, finalValues.length);
    }

    public Veci(Comp[] initialValues, Veci finalValues) {
        super(initialValues.length + finalValues.size);
        int vecSize = finalValues.size();
        this.values = new Comp[initialValues.length + vecSize];

        System.arraycopy(initialValues, 0, this.values, 0, initialValues.length);

        for (int i=0;i<vecSize;i++) {
            this.values[initialValues.length + i] = finalValues.get(i);
        }
    }

    public interface VecifForEach {
        Comp apply (Comp a, Comp b);
    }

    public interface VecifForEachIndex {
        Comp apply (int pos);
    }

    public int size () {
        return values.length;
    }

    public Comp get (int pos) {
        return this.values[pos];
    }

    public void set (Veci values) {
        if (values.size() != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i=0;i<size();i++) {
            this.values[i] = values.get(i);
        }
    }

    public void set (Comp... values) {
        if (values.length != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i=0;i<size();i++) {
            this.values[i] = values[i];
        }
    }

    public void set (int pos, Comp value) {
        this.values[pos] = value;
    }

    private int finalLen (Veci b) {
        return Math.min(size(), b.size());
    }

    public Veci foreach(Veci b, VecifForEach forEach) {
        int size = finalLen(b);
        Veci vector = new Veci(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Veci foreach(Comp b, VecifForEach forEach) {
        int size = size();
        Veci vector = new Veci(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Veci foreach(float b, VecifForEach forEach) {
        return foreach(new Comp(b, 0), forEach);
    }

    public static Veci foreach(int size, VecifForEachIndex forEach) {
        Veci vector = new Veci(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Veci add (Veci b) {
        return foreach(b, Comp::add);
    }

    public Veci add (Comp b) {
        return foreach(b, Comp::add);
    }

    public Veci add (float b) {
        return foreach(b, Comp::add);
    }

    public Veci subtr (Veci b) {
        return foreach(b, Comp::subtr);
    }

    public Veci subtr (Comp b) {
        return foreach(b, Comp::subtr);
    }

    public Veci subtr (float b) {
        return foreach(b, Comp::subtr);
    }

    public Veci invSubtr (Comp b) {
        return foreach(b, (x, y) -> y.subtr(x));
    }

    public Veci invSubtr (float b) {
        return foreach(b, (x, y) -> y.subtr(x));
    }

    public Veci mul (Veci b) {
        return foreach(b, Comp::mul);
    }

    public Veci mul (Comp b) {
        return foreach(b, Comp::mul);
    }

    public Veci mul (float b) {
        return foreach(b, Comp::mul);
    }

    public Veci div (Veci b) {
        return foreach(b, Comp::div);
    }

    public Veci div (Comp b) {
        return foreach(b, Comp::div);
    }

    public Veci div (float b) {
        return foreach(b, Comp::div);
    }

    public Veci invDiv (Comp b) {
        return foreach(b, (x, y) -> y.div(x));
    }

    public Veci invDiv (float b) {
        return foreach(b, (x, y) -> y.div(x));
    }

    public Veci conj() {
        return Veci.foreach(size(), i -> get(i).conj());
    }

    public float magnitude2 () {
        float sum = 0;
        for (int i = 0; i< size(); i++) {
            float mod = get(i).abs();
            sum += mod * mod;
        }

        return sum;
    }

    public float magnitude () {
        return Mathf.sqrt(magnitude2());
    }

    public Veci unit () {
        return div(magnitude());
    }

    public Comp dot (Veci b) {
        int len = finalLen(b);
        Comp sum = new Comp();

        for (int i=0;i<len;i++) {
            sum = sum.add(get(i).mul(b.get(i)));
        }

        return sum;
    }

    public Comp inner (Veci b) {
        return rowMatrix().foreach(1, (x, y) -> x.conj()).mul(b.colMatrix()).get(0,0);
    }

    public Veci cross (Veci b) {
        if (finalLen(b) < 3) {
            throw new ArithmeticException("Tried to do cross product with vectors of size smaller than 3");
        }

        Veci vector = new Veci(3);
        vector.values[0] = get(1).mul(b.get(2)).subtr(get(2).mul(b.get(1)));
        vector.values[1] = get(2).mul(b.get(0)).subtr(get(0).mul(b.get(2)));
        vector.values[2] = get(0).mul(b.get(1)).subtr(get(1).mul(b.get(0)));

        return vector;
    }

    public Comp sum () {
        Comp val = new Comp();
        for (int i = 0; i< size(); i++) {
            val = val.add(get(i));
        }

        return val;
    }

    public Vec abs () {
        return Vec.foreach(size(), i -> get(i).abs());
    }

    public Comp mean () {
        return sum().div(size());
    }

    @Override
    public Iterator<Comp> iterator() {
        return new Iterator<Comp>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public Comp next() {
                return get(i++);
            }
        };
    }

    public boolean any (Function<Comp, Boolean> cond) {
        for (Comp value: this) {
            if (cond.apply(value)) {
                return true;
            }
        }

        return false;
    }

    public boolean all (Function<Comp, Boolean> cond) {
        for (Comp value: this) {
            if (!cond.apply(value)) {
                return false;
            }
        }

        return true;
    }

    public Veci sub (int... pos) {
        Veci vector = new Veci(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    public Veci sub (int offset, int length) {
        Comp[] vals = new Comp[length];
        System.arraycopy(values, offset, vals, 0, length);

        return new Veci(vals);
    }

    public Vecid toDouble () {
        Vecid vector = new Vecid(size());
        for (int i = 0; i< size(); i++) {
            vector.set(i, get(i).toDouble());
        }

        return vector;
    }

    public Comp[] toArray () {
        return values.clone();
    }

    public VecCLi toCL(Context context) {
        return new VecCLi(context, toArray());
    }

    public VecCLi toCL() {
        return new VecCLi(Context.DEFAULT, toArray());
    }

    public VecCUDAi toCUDA() {
        return new VecCUDAi(this);
    }

    public Mati rowMatrix () {
        return new Mati(this);
    }

    public Mati colMatrix () {
        return rowMatrix().T();
    }

    public Mati rowMajor (int cols) {
        int rows = size() / cols;
        return Mati.foreach(rows, cols, (i,j) -> get((i * cols) + j));
    }

    public Mati colMajor (int rows) {
        int cols = size() / rows;
        return Mati.foreach(rows, cols, (i,j) -> get((j * rows) + 1));
    }

    public Veci clone() {
        return new Veci(values.clone());
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
        Veci comps = (Veci) o;
        return Arrays.equals(values, comps.values);
    }

    
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
