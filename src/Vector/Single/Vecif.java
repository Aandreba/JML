package Vector.Single;

import GPGPU.OpenCL.Context;
import Imaginary.Compf;
import Matrix.Single.Matif;
import References.Single.Complex.Ref1Dif;
import Vector.Double.VecCLi;
import Vector.Double.Veci;

public class Vecif implements Ref1Dif {
    final protected Compf[] values;

    public Vecif (int size) {
        this.values = new Compf[size];
    }

    public Vecif (Compf... values) {
        this.values = values;
    }

    public Vecif (float... values) {
        this(values.length);
        for (int i=0;i<values.length;i++) {
            set(i, new Compf(values[i], 0));
        }
    }

    public Vecif(Vecif initialValues, Compf... finalValues) {
        int vecSize = initialValues.getSize();
        this.values = new Compf[vecSize + finalValues.length];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }
    }

    public Vecif(Compf[] initialValues, Vecif finalValues) {
        int vecSize = finalValues.getSize();
        this.values = new Compf[initialValues.length + vecSize];

        for (int i=0;i<initialValues.length;i++) {
            this.values[i] = initialValues[i];
        }

        for (int i=0;i<vecSize;i++) {
            this.values[initialValues.length + i] = finalValues.get(i);
        }
    }

    public interface VecifForEach {
        Compf apply (Compf a, Compf b);
    }

    public interface VecifForEachIndex {
        Compf apply (int pos);
    }

    public int getSize () {
        return values.length;
    }

    public Compf get (int pos) {
        return this.values[pos];
    }

    public void set (int pos, Compf value) {
        this.values[pos] = value;
    }

    private int finalLen (Vecif b) {
        return Math.min(getSize(), b.getSize());
    }

    public Vecif forEach (Vecif b, VecifForEach forEach) {
        int size = finalLen(b);
        Vecif vector = new Vecif(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)));
        }

        return vector;
    }

    public Vecif forEach (Compf b, VecifForEach forEach) {
        int size = getSize();
        Vecif vector = new Vecif(size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b));
        }

        return vector;
    }

    public Vecif forEach (float b, VecifForEach forEach) {
        return forEach(new Compf(b, 0), forEach);
    }

    public static Vecif forEach (int size, VecifForEachIndex forEach) {
        Vecif vector = new Vecif(size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i));
        }

        return vector;
    }

    public Vecif add (Vecif b) {
        return forEach(b, Compf::add);
    }

    public Vecif add (Compf b) {
        return forEach(b, Compf::add);
    }

    public Vecif add (float b) {
        return forEach(b, Compf::add);
    }

    public Vecif subtr (Vecif b) {
        return forEach(b, Compf::subtr);
    }

    public Vecif subtr (Compf b) {
        return forEach(b, Compf::subtr);
    }

    public Vecif subtr (float b) {
        return forEach(b, Compf::subtr);
    }

    public Vecif invSubtr (Compf b) {
        return forEach(b, (x, y) -> y.subtr(x));
    }

    public Vecif invSubtr (float b) {
        return forEach(b, (x, y) -> y.subtr(x));
    }

    public Vecif mul (Vecif b) {
        return forEach(b, Compf::mul);
    }

    public Vecif mul (Compf b) {
        return forEach(b, Compf::mul);
    }

    public Vecif mul (float b) {
        return forEach(b, Compf::mul);
    }

    public Vecif div (Vecif b) {
        return forEach(b, Compf::div);
    }

    public Vecif div (Compf b) {
        return forEach(b, Compf::div);
    }

    public Vecif div (float b) {
        return forEach(b, Compf::div);
    }

    public Vecif invDiv (Compf b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Vecif invDiv (float b) {
        return forEach(b, (x, y) -> y.div(x));
    }

    public Compf magnitude2 () {
        Compf sum = new Compf();
        for (int i=0;i<getSize();i++) {
            sum = sum.add(get(i).mul(get(i)));
        }

        return sum;
    }

    public Compf magnitude () {
        return Compf.sqrt(magnitude2());
    }

    public Vecif unit () {
        return div(magnitude());
    }

    public Compf dot (Vecif b) {
        int len = finalLen(b);
        Compf sum = new Compf();

        for (int i=0;i<len;i++) {
            sum = sum.add(get(i).mul(b.get(i)));
        }

        return sum;
    }

    public Vecif cross (Vecif b) {
        if (finalLen(b) < 3) {
            throw new ArithmeticException("Tried to do cross product with vectors of size smaller than 3");
        }

        Vecif vector = new Vecif(3);
        vector.values[0] = get(1).mul(b.get(2)).subtr(get(2).mul(b.get(1)));
        vector.values[1] = get(2).mul(b.get(0)).subtr(get(0).mul(b.get(2)));
        vector.values[2] = get(0).mul(b.get(1)).subtr(get(1).mul(b.get(0)));

        return vector;
    }

    public Compf sum () {
        Compf val = new Compf();
        for (int i=0;i<getSize();i++) {
            val = val.add(get(i));
        }

        return val;
    }

    public Compf mean () {
        return sum().div(getSize());
    }

    public Vecif sub (int... pos) { // Subvector
        Vecif vector = new Vecif(pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    public Veci toDouble () {
        Veci vector = new Veci(getSize());
        for (int i=0;i<getSize();i++) {
            vector.set(i, get(i).toDouble());
        }

        return vector;
    }

    public VecCLif toCL(Context context) {
        return new VecCLif(context, toArray());
    }

    public VecCLif toCL() {
        return new VecCLif(Context.DEFAULT, toArray());
    }

    public Matif rowMatrix () {
        return new Matif(this);
    }

    public Matif colMatrix () {
        return rowMatrix().T();
    }

    public static Vecif fromRef (Ref1Dif ref) {
        return ref instanceof Vecif ? (Vecif) ref : forEach(ref.getSize(), ref::get);
    }

    @Override
    public Vecif clone() {
        return new Vecif(values.clone());
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
