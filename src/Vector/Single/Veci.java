package Vector.Single;

import GPGPU.OpenCL.Context;
import Complex.Comp;
import Mathx.Mathf;
import Matrix.Single.Mati;
import References.Single.Complex.Ref1Di;
import Vector.Double.Vecid;

import java.util.Arrays;

public class Veci implements Ref1Di {
    final protected Comp[] values;

    public Veci(int size) {
        this.values = new Comp[size];
    }

    public Veci(Comp... values) {
        this.values = values;
    }

    public Veci(float... values) {
        this(values.length);
        for (int i=0;i<values.length;i++) {
            set(i, new Comp(values[i], 0));
        }
    }

    public Veci(Veci initialValues, Comp... finalValues) {
        int vecSize = initialValues.getSize();
        this.values = new Comp[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Veci(Comp[] initialValues, Veci finalValues) {
        int vecSize = finalValues.getSize();
        this.values = new Comp[initialValues.length + vecSize];

        for (int i=0;i<initialValues.length;i++) {
            this.values[i] = initialValues[i];
        }

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

    public int getSize () {
        return values.length;
    }

    public Comp get (int pos) {
        return this.values[pos];
    }

    public void set (int pos, Comp value) {
        this.values[pos] = value;
    }

    private int finalLen (Veci b) {
        return Math.min(getSize(), b.getSize());
    }

    public Veci forEach (Veci b, VecifForEach forEach) {
        int size = finalLen(b);
        Veci vector = new Veci(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Veci forEach (Comp b, VecifForEach forEach) {
        int size = getSize();
        Veci vector = new Veci(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Veci forEach (float b, VecifForEach forEach) {
        return forEach(new Comp(b, 0), forEach);
    }

    public static Veci forEach (int size, VecifForEachIndex forEach) {
        Veci vector = new Veci(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Veci add (Veci b) {
        return forEach(b, Comp::add);
    }

    public Veci add (Comp b) {
        return forEach(b, Comp::add);
    }

    public Veci add (float b) {
        return forEach(b, Comp::add);
    }

    public Veci subtr (Veci b) {
        return forEach(b, Comp::subtr);
    }

    public Veci subtr (Comp b) {
        return forEach(b, Comp::subtr);
    }

    public Veci subtr (float b) {
        return forEach(b, Comp::subtr);
    }

    public Veci invSubtr (Comp b) {
        return forEach(b, (x, y) -> y.subtr(x));
    }

    public Veci invSubtr (float b) {
        return forEach(b, (x, y) -> y.subtr(x));
    }

    public Veci mul (Veci b) {
        return forEach(b, Comp::mul);
    }

    public Veci mul (Comp b) {
        return forEach(b, Comp::mul);
    }

    public Veci mul (float b) {
        return forEach(b, Comp::mul);
    }

    public Veci div (Veci b) {
        return forEach(b, Comp::div);
    }

    public Veci div (Comp b) {
        return forEach(b, Comp::div);
    }

    public Veci div (float b) {
        return forEach(b, Comp::div);
    }

    public Veci invDiv (Comp b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Veci invDiv (float b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public float magnitude2 () {
        float sum = 0;
        for (int i=0;i<getSize();i++) {
            float mod = get(i).modulus();
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
        for (int i=0;i<getSize();i++) {
            val = val.add(get(i));
        }

        return val;
    }

    public Comp mean () {
        return sum().div(getSize());
    }

    public Veci sub (int... pos) { // Subvector
        Veci vector = new Veci(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    public Vecid toDouble () {
        Vecid vector = new Vecid(getSize());
        for (int i=0;i<getSize();i++) {
            vector.set(i, get(i).toDouble());
        }

        return vector;
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

    public static Veci fromRef (Ref1Di ref) {
        return ref instanceof Veci ? (Veci) ref : forEach(ref.getSize(), ref::get);
    }

    @Override
    public Veci clone() {
        return new Veci(values.clone());
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
        Veci comps = (Veci) o;
        return Arrays.equals(values, comps.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
