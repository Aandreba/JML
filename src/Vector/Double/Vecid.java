package Vector.Double;

import Complex.Comp;
import GPGPU.OpenCL.Context;
import Complex.Compd;
import Matrix.Double.Matid;
import References.Double.Complex.Ref1Did;
import Vector.Single.Veci;

import java.util.Arrays;

public class Vecid implements Ref1Did {
    final protected Compd[] values;

    public Vecid(int size) {
        this.values = new Compd[size];
        for (int i=0;i<size;i++) {
            this.values[i] = Compd.ZERO.clone();
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
        int vecSize = initialValues.getSize();
        this.values = new Compd[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Vecid(Compd[] initialValues, Vecid finalValues) {
        int vecSize = finalValues.getSize();
        this.values = new Compd[initialValues.length + vecSize];

        for (int i=0;i<initialValues.length;i++) {
            this.values[i] = initialValues[i];
        }

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

    public int getSize () {
        return values.length;
    }

    public Compd get (int pos) {
        return this.values[pos];
    }

    public void set (int pos, Compd value) {
        this.values[pos] = value;
    }

    private int finalLen (Vecid b) {
        return Math.min(getSize(), b.getSize());
    }

    public Vecid forEach (Vecid b, VeciForEach forEach) {
        int size = finalLen(b);
        Vecid vector = new Vecid(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vecid forEach (Compd b, VeciForEach forEach) {
        int size = getSize();
        Vecid vector = new Vecid(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Vecid forEach (double b, VeciForEach forEach) {
        return forEach(new Compd(b, 0), forEach);
    }

    public Vecid forEach (VeciForEachValue forEach) {
        int size = getSize();
        Vecid vector = new Vecid(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i)));
        }

        return vector;
    }

    public static Vecid forEach (int size, VeciForEachIndex forEach) {
        Vecid vector = new Vecid(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vecid add (Vecid b) {
        return forEach(b, Compd::add);
    }

    public Vecid add (Compd b) {
        return forEach(b, Compd::add);
    }

    public Vecid add (double b) {
        return forEach(b, Compd::add);
    }

    public Vecid subtr (Vecid b) {
        return forEach(b, Compd::subtr);
    }

    public Vecid subtr (Compd b) {
        return forEach(b, Compd::subtr);
    }

    public Vecid subtr (double b) {
        return forEach(b, Compd::subtr);
    }

    public Vecid invSubtr (Compd b) {
        return forEach(b, (x, y) -> y.subtr(x));
    }

    public Vecid invSubtr (double b) {
        return forEach(b, (x, y) -> y.subtr(x));
    }

    public Vecid mul (Vecid b) {
        return forEach(b, Compd::mul);
    }

    public Vecid mul (Compd b) {
        return forEach(b, Compd::mul);
    }

    public Vecid mul (double b) {
        return forEach(b, Compd::mul);
    }

    public Vecid div (Vecid b) {
        return forEach(b, Compd::div);
    }

    public Vecid div (Compd b) {
        return forEach(b, Compd::div);
    }

    public Vecid div (double b) {
        return forEach(b, Compd::div);
    }

    public Vecid invDiv (Compd b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Vecid invDiv (double b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public double magnitude2 () {
        double sum = 0;
        for (int i=0;i<getSize();i++) {
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
        for (int i=0;i<getSize();i++) {
            val = val.add(get(i));
        }

        return val;
    }

    public Compd mean () {
        return sum().div(getSize());
    }

    public Vecid sub (int... pos) { // Subvector
        Vecid vector = new Vecid(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    @Override
    public Compd[] toArray() {
        return values.clone();
    }

    public Veci toFloat () {
        Veci vector = new Veci(getSize());
        for (int i=0;i<getSize();i++) {
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

    public static Vecid fromRef (Ref1Did ref) {
        return ref instanceof Vecid ? (Vecid) ref : forEach(ref.getSize(), ref::get);
    }

    @Override
    public Vecid clone() {
        return new Vecid(values.clone());
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
        Vecid compds = (Vecid) o;
        return Arrays.equals(values, compds.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
