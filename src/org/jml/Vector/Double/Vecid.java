package org.jml.Vector.Double;

import org.jml.Complex.Single.Comp;
import org.jml.GPGPU.OpenCL.Context;
import org.jml.Complex.Double.Compd;
import org.jml.Matrix.Double.Matd;
import org.jml.Matrix.Double.Matid;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public class Vecid implements Iterable<Compd>, Serializable {
    final protected Compd[] values;

    public Vecid(int size) {
        this.values = new Compd[size];
        for (int i=0;i<size;i++) {
            this.values[i] = Compd.ZERO;
        }
    }

    public Vecid(Compd... values) {
        this.values = values;
    }

    public Vecid(double... values) {
        this(values.length);
        for (int i=0;i<values.length;i++) {
            set(i, new Compd(values[i], 0));
        }
    }

    public Vecid(Vecid initialValues, Compd... finalValues) {
        int vecSize = initialValues.size();
        this.values = new Compd[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        System.arraycopy(finalValues, 0, this.values, vecSize, finalValues.length);
    }

    public Vecid(Compd[] initialValues, Vecid finalValues) {
        int vecSize = finalValues.size();
        this.values = new Compd[initialValues.length + vecSize];

        System.arraycopy(initialValues, 0, this.values, 0, initialValues.length);

        for (int i=0;i<vecSize;i++) {
            this.values[initialValues.length + i] = finalValues.get(i);
        }
    }

    public interface VeciForEach {
        Compd apply (Compd a, Compd b);
    }

    public interface VeciForEachIndex {
        Compd apply (int pos);
    }

    public interface VeciForEachValue {
        Compd apply (Compd pos);
    }

    public int size () {
        return values.length;
    }

    public Compd get (int pos) {
        return this.values[pos];
    }

    public void set (Vecid values) {
        if (values.size() != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i=0;i<size();i++) {
            this.values[i] = values.get(i);
        }
    }

    public void set (Compd... values) {
        if (values.length != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i=0;i<size();i++) {
            this.values[i] = values[i];
        }
    }

    public void set (int pos, Compd value) {
        this.values[pos] = value;
    }

    private int finalLen (Vecid b) {
        return Math.min(size(), b.size());
    }

    public Vecid foreach(Vecid b, VeciForEach forEach) {
        int size = finalLen(b);
        Vecid vector = new Vecid(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vecid foreach(Compd b, VeciForEach forEach) {
        int size = size();
        Vecid vector = new Vecid(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Vecid foreach(double b, VeciForEach forEach) {
        return foreach(new Compd(b, 0), forEach);
    }

    public Vecid foreach(VeciForEachValue forEach) {
        int size = size();
        Vecid vector = new Vecid(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i)));
        }

        return vector;
    }

    public static Vecid foreach(int size, VeciForEachIndex forEach) {
        Vecid vector = new Vecid(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vecid add (Vecid b) {
        return foreach(b, Compd::add);
    }

    public Vecid add (Compd b) {
        return foreach(b, Compd::add);
    }

    public Vecid add (double b) {
        return foreach(b, Compd::add);
    }

    public Vecid subtr (Vecid b) {
        return foreach(b, Compd::subtr);
    }

    public Vecid subtr (Compd b) {
        return foreach(b, Compd::subtr);
    }

    public Vecid subtr (double b) {
        return foreach(b, Compd::subtr);
    }

    public Vecid invSubtr (Compd b) {
        return foreach(b, (x, y) -> y.subtr(x));
    }

    public Vecid invSubtr (double b) {
        return foreach(b, (x, y) -> y.subtr(x));
    }

    public Vecid mul (Vecid b) {
        return foreach(b, Compd::mul);
    }

    public Vecid mul (Compd b) {
        return foreach(b, Compd::mul);
    }

    public Vecid mul (double b) {
        return foreach(b, Compd::mul);
    }

    public Vecid div (Vecid b) {
        return foreach(b, Compd::div);
    }

    public Vecid div (Compd b) {
        return foreach(b, Compd::div);
    }

    public Vecid div (double b) {
        return foreach(b, Compd::div);
    }

    public Vecid invDiv (Compd b) {
        return foreach(b, (x, y) -> y.div(x));
    }

    public Vecid invDiv (double b) {
        return foreach(b, (x, y) -> y.div(x));
    }

    
    public Vecid conj() {
        return Vecid.foreach(size(), i -> get(i).conj());
    }

    public double magnitude2 () {
        double sum = 0;
        for (int i = 0; i< size(); i++) {
            double mod = get(i).modulus();
            sum += mod * mod;
        }

        return sum;
    }

    public double magnitude () {
        return Math.sqrt(magnitude2());
    }

    public Vecid unit () {
        return div(magnitude());
    }

    public Compd dot (Vecid b) {
        int len = finalLen(b);
        Compd sum = new Compd();

        for (int i=0;i<len;i++) {
            sum = sum.add(get(i).mul(b.get(i)));
        }

        return sum;
    }

    public Compd inner (Vecid b) {
        return rowMatrix().foreach(1, (x, y) -> x.conj()).mul(b.colMatrix()).get(0,0);
    }

    public Vecid cross (Vecid b) {
        if (finalLen(b) < 3) {
            throw new ArithmeticException("Tried to do cross product with vectors of size smaller than 3");
        }

        Vecid vector = new Vecid(3);
        vector.values[0] = get(1).mul(b.get(2)).subtr(get(2).mul(b.get(1)));
        vector.values[1] = get(2).mul(b.get(0)).subtr(get(0).mul(b.get(2)));
        vector.values[2] = get(0).mul(b.get(1)).subtr(get(1).mul(b.get(0)));

        return vector;
    }

    public Compd sum () {
        Compd val = new Compd();
        for (int i = 0; i< size(); i++) {
            val = val.add(get(i));
        }

        return val;
    }

    public Vecd abs () {
        return Vecd.foreach(size(), i -> get(i).modulus());
    }

    public Compd mean () {
        return sum().div(size());
    }

    @Override
    public Iterator<Compd> iterator() {
        return new Iterator<Compd>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public Compd next() {
                return get(i++);
            }
        };
    }

    public boolean any (Function<Compd, Boolean> cond) {
        for (Compd value: this) {
            if (cond.apply(value)) {
                return true;
            }
        }

        return false;
    }

    public boolean all (Function<Compd, Boolean> cond) {
        for (Compd value: this) {
            if (!cond.apply(value)) {
                return false;
            }
        }

        return true;
    }

    public Vecid sub (int... pos) { // Subvector
        Vecid vector = new Vecid(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    public Compd[] toArray() {
        return values.clone();
    }

    public Veci toFloat () {
        Veci vector = new Veci(size());
        for (int i = 0; i< size(); i++) {
            vector.set(i, get(i).toFloat());
        }

        return vector;
    }

    public VecCLid toCL (Context context) {
        return new VecCLid(context, toArray());
    }

    public VecCLid toCL () {
        return toCL(Context.DEFAULT);
    }

    public VecCUDAid toCUDA() {
        return new VecCUDAid(this);
    }

    public Matid rowMatrix () {
        return new Matid(this);
    }

    public Matid colMatrix () {
        return rowMatrix().T();
    }

    public Matid rowMajor (int cols) {
        int rows = size() / cols;
        return Matid.foreach(rows, cols, (i,j) -> get((i * cols) + j));
    }

    public Matid colMajor (int rows) {
        int cols = size() / rows;
        return Matid.foreach(rows, cols, (i,j) -> get((j * rows) + 1));
    }

    public Vecid clone() {
        return new Vecid(values.clone());
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
        Vecid compds = (Vecid) o;
        return Arrays.equals(values, compds.values);
    }

    
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
