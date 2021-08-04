package org.jml.Vector.Decimal;

import org.jml.Mathx.Mathb;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Single.Vec;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public class Vecb implements Iterable<BigDecimal>, Serializable {
    final private static long serialVersionUID = -445436878L;
    final protected BigDecimal[] values;
    final public MathContext context;

    public Vecb (MathContext context, int size) {
        this.values = new BigDecimal[size];
        this.context = context;
    }

    public Vecb(MathContext context, BigDecimal... values) {
        this.values = values;
        this.context = context;
    }

    public Vecb (MathContext context, Vecb initialValues, BigDecimal... finalValues) {
        int size = initialValues.size() + finalValues.length;
        int vecSize = initialValues.size();
        this.values = new BigDecimal[size];

        for (int i=0;i<vecSize;i++) {
            this.values[i] = initialValues.get(i);
        }

        for (int i=0;i<finalValues.length;i++) {
            this.values[vecSize + i] = finalValues[i];
        }

        this.context = context;
    }

    public Vecb(MathContext context, BigDecimal[] initialValues, Vecb finalValues) {
        int size = initialValues.length + finalValues.size();
        int vecSize = finalValues.size();
        this.values = new BigDecimal[initialValues.length + vecSize];

        for (int i=0;i<initialValues.length;i++) {
            this.values[i] = initialValues[i];
        }

        for (int i=0;i<vecSize;i++) {
            this.values[initialValues.length + i] = finalValues.get(i);
        }

        this.context = context;
    }

    public interface VecbForEach {
        BigDecimal apply (BigDecimal a, BigDecimal b);
    }

    public interface VecbForEachIndex {
        BigDecimal apply (int pos);
    }

    public interface VecbForEachValue {
        BigDecimal apply (BigDecimal value);
    }

    public int size () {
        return this.values.length;
    }

    public BigDecimal get (int pos) {
        return this.values[pos];
    }

    public void set (Vecb values) {
        if (values.size() != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values.values, 0, this.values, 0, size());
    }

    public void set (BigDecimal... values) {
        if (values.length != size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values, 0, this.values, 0, size());
    }

    public void set (int offsetSrc, int offsetDest, int length, Vecb values) {
        if (offsetDest + length > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values.values, offsetSrc, this.values, offsetDest, length);
    }

    public void set (int offsetSrc, int offsetDest, Vecb values) {
        if (offsetDest + values.size() > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values.values, offsetSrc, this.values, offsetDest, values.size() - offsetSrc);
    }

    public void set (int offsetSrc, int offsetDest, int length, BigDecimal... values) {
        if (offsetDest + length > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values, offsetSrc, this.values, offsetDest, length);
    }

    public void set (int offsetSrc, int offsetDest, BigDecimal... values) {
        if (offsetDest + values.length > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        System.arraycopy(values, offsetSrc, this.values, offsetDest, values.length - offsetSrc);
    }

    public void set (int pos, BigDecimal value) {
        this.values[pos] = value;
    }

    private int finalLen (Vecb b) {
        return Math.min(size(), b.size());
    }

    public Vecb foreach (Vecb b, VecbForEach forEach) {
        int size = finalLen(b);
        Vecb vector = new Vecb(context, size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b.get(i)).round(context));
        }

        return vector;
    }

    public Vecb foreach (BigDecimal b, VecbForEach forEach) {
        int size = size();
        Vecb vector = new Vecb(context, size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i), b).round(context));
        }

        return vector;
    }

    public Vecb foreach (VecbForEachValue forEach) {
        int size = size();
        Vecb vector = new Vecb(context, size);

        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(get(i)).round(context));
        }

        return vector;
    }

    public static Vecb foreach (MathContext context, int size, VecbForEachIndex forEach) {
        Vecb vector = new Vecb(context, size);
        for (int i=0;i<size;i++) {
            vector.set(i, forEach.apply(i).round(context));
        }

        return vector;
    }

    public Vecb add (Vecb b) {
        return foreach(b, BigDecimal::add);
    }

    public Vecb add (BigDecimal b) {
        return foreach(b, BigDecimal::add);
    }

    public Vecb subtr (Vecb b) {
        return foreach(b, BigDecimal::subtract);
    }

    public Vecb subtr (BigDecimal b) {
        return foreach(b, BigDecimal::subtract);
    }

    public Vecb invSubtr (BigDecimal b) {
        return foreach(b, (x, y) -> y.subtract(x));
    }

    public Vecb mul (Vecb b) {
        return foreach(b, BigDecimal::multiply);
    }

    public Vecb mul (BigDecimal b) {
        return foreach(b, BigDecimal::multiply);
    }

    public Vecb div (Vecb b) {
        return foreach(b, (x, y) -> x.divide(y, context));
    }

    public Vecb div (BigDecimal b) {
        return foreach(b, (x, y) -> x.divide(y, context));
    }

    public Vecb invDiv(BigDecimal b) {
        return foreach(b, (x, y) -> y.divide(x, context));
    }

    public BigDecimal magnitude2 () {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < size(); i++) {
            sum = sum.add(get(i).pow(2));
        }

        return sum;
    }

    public BigDecimal magnitude () {
        return magnitude2().sqrt(context);
    }

    public Vecb unit () {
        return div(magnitude());
    }

    public BigDecimal dot (Vecb b) {
        int len = finalLen(b);
        BigDecimal sum = BigDecimal.ZERO;

        for (int i=0;i<len;i++) {
            sum = sum.add(get(i).multiply(b.get(i)));
        }

        return sum;
    }

    public Vecb cross (Vecb b) {
        if (finalLen(b) < 3) {
            throw new ArithmeticException("Tried to do cross product with vectors of size smaller than 3");
        }

        Vecb vector = new Vecb(context, 3);
        vector.values[0] = get(1).multiply(b.get(2)).subtract(get(2).multiply(b.get(1)));
        vector.values[1] = get(2).multiply(b.get(0)).subtract(get(0).multiply(b.get(2)));
        vector.values[2] = get(0).multiply(b.get(1)).subtract(get(1).multiply(b.get(0)));

        return vector;
    }

    public BigDecimal sum () {
        BigDecimal val = BigDecimal.ZERO;
        for (int i = 0; i < size(); i++) {
            val = val.add(get(i));
        }

        return val;
    }

    public Vecb abs () {
        return foreach(context, size(), i -> get(i).abs());
    }

    public BigDecimal mean () {
        return sum().divide(BigDecimal.valueOf(size()), context);
    }

    public int minIndex () {
        int min = 0;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || Mathb.lesserThan(get(i), get(min))) {
                min = i;
            }
        }

        return min;
    }

    public BigDecimal min () {
        BigDecimal min = BigDecimal.ZERO;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || Mathb.lesserThan(get(i), min)) {
                min = get(i);
            }
        }

        return min;
    }

    public int maxIndex () {
        int max = 0;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || Mathb.greaterThan(get(i), get(max))) {
                max = i;
            }
        }

        return max;
    }

    public BigDecimal max () {
        BigDecimal max = BigDecimal.ZERO;
        for (int i = 0; i< size(); i++) {
            if (i == 0 || Mathb.greaterThan(get(i), max)) {
                max = get(i);
            }
        }

        return max;
    }

    public Iterator<BigDecimal> iterator() {
        return new Iterator<BigDecimal>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public BigDecimal next() {
                return get(i++);
            }
        };
    }

    public boolean any (Function<BigDecimal, Boolean> cond) {
        for (BigDecimal value: this) {
            if (cond.apply(value)) {
                return true;
            }
        }

        return false;
    }

    public boolean all (Function<BigDecimal, Boolean> cond) {
        for (BigDecimal value: this) {
            if (!cond.apply(value)) {
                return false;
            }
        }

        return true;
    }

    public Vecb sub (int... pos) {
        Vecb vector = new Vecb(context, pos.length);
        for (int i=0;i<pos.length;i++) {
            vector.set(i, get(pos[i]));
        }

        return vector;
    }

    public Vecb sub (int offset, int length) {
        BigDecimal[] vals = new BigDecimal[length];
        System.arraycopy(values, offset, vals, 0, length);

        return new Vecb(context, vals);
    }

    public BigDecimal[] toArray() {
        return values.clone();
    }

    public Vecb toContext (MathContext context) {
        return foreach(context, size(), i -> get(i).round(context));
    }

    public Vecd toDouble () {
        Vecd vector = new Vecd(size());
        for (int i = 0; i< size(); i++) {
            vector.set(i, get(i).doubleValue());
        }

        return vector;
    }

    public Vec toFloat () {
        Vec vector = new Vec(size());
        for (int i = 0; i< size(); i++) {
            vector.set(i, get(i).floatValue());
        }

        return vector;
    }

    /*public Veci toComplex() {
        return new Veci(values);
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
    } */

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            builder.append(", ").append(get(i));
        }

        return "{ " + builder.substring(2) + " }";
    }

    public Vecb clone() {
        return new Vecb(context, values.clone());
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vecb floats = (Vecb) o;
        return Arrays.equals(values, floats.values);
    }

    
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
