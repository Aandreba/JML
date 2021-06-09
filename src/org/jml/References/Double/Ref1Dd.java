package org.jml.References.Double;

import org.jml.Complex.Double.Compd;
import org.jml.References.Double.Complex.Ref1Did;
import org.jml.References.Single.Ref1D;

import java.util.Iterator;

public interface Ref1Dd extends Iterable<Double> {
    int getSize();
    double get (int pos);
    void set (int pos, double val);

    default void set (Ref1Dd values) {
        int len = Math.min(getSize(), values.getSize());
        for (int i=0;i<len;i++) {
            set(i, values.get(i));
        }
    }

    default double[] toArray () {
        double[] array = new double[getSize()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i);
        }

        return array;
    }

    default Ref1D toFloat () {
        return new Ref1D() {
            @Override
            public int getSize() {
                return Ref1Dd.this.getSize();
            }

            @Override
            public float get(int pos) {
                return (float) Ref1Dd.this.get(pos);
            }

            @Override
            public void set(int pos, float val) {
                Ref1Dd.this.set(pos, val);
            }
        };
    }

    default Ref1Did toComplex () {
        return new Ref1Did() {
            @Override
            public int getSize() {
                return Ref1Dd.this.getSize();
            }

            @Override
            public Compd get(int pos) {
                return new Compd(Ref1Dd.this.get(pos), 0);
            }

            @Override
            public void set (int pos, Compd val) {
                Ref1Dd.this.set(pos, val.real);
            }
        };
    }

    default Ref2Dd rowMajor (int cols) {
        return new Ref2Dd() {
            @Override
            public int getRows() {
                return getSize() / cols;
            }

            @Override
            public int getCols() {
                return cols;
            }

            @Override
            public double get (int row, int col) {
                return Ref1Dd.this.get((row * cols) + col);
            }

            @Override
            public void set (int row, int col, double val) {
                Ref1Dd.this.set((row * cols) + col, val);
            }
        };
    }

    default Ref2Dd colMajor (int rows) {
        return new Ref2Dd() {
            @Override
            public int getRows() {
                return rows;
            }

            @Override
            public int getCols() {
                return getSize() / rows;
            }

            @Override
            public double get (int row, int col) {
                return Ref1Dd.this.get((col * rows) + row);
            }

            @Override
            public void set (int row, int col, double val) {
                Ref1Dd.this.set((col * rows) + row, val);
            }
        };
    }

    @Override
    default Iterator<Double> iterator() {
        return new Iterator<Double>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getSize();
            }

            @Override
            public Double next() {
                return get(i++);
            }
        };
    }
}
