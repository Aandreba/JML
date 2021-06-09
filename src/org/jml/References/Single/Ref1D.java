package org.jml.References.Single;

import org.jml.Complex.Single.Comp;
import org.jml.References.Double.Ref1Dd;
import org.jml.References.Single.Complex.Ref1Di;

import java.util.Iterator;

public interface Ref1D extends Iterable<Float> {
    int getSize();
    float get (int pos);
    void set (int pos, float val);

    default void set (Ref1D values) {
        int len = Math.min(getSize(), values.getSize());
        for (int i=0;i<len;i++) {
            set(i, values.get(i));
        }
    }

    default float[] toArray () {
        float[] array = new float[getSize()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i);
        }

        return array;
    }

    default Ref1Dd toDouble () {
        return new Ref1Dd() {
            @Override
            public int getSize() {
                return Ref1D.this.getSize();
            }

            @Override
            public double get(int pos) {
                return Ref1D.this.get(pos);
            }

            @Override
            public void set (int pos, double val) {
                Ref1D.this.set(pos, (float) val);
            }
        };
    }

    default Ref1Di toComplex () {
        return new Ref1Di() {
            @Override
            public int getSize() {
                return Ref1D.this.getSize();
            }

            @Override
            public Comp get(int pos) {
                return new Comp(Ref1D.this.get(pos), 0);
            }

            @Override
            public void set (int pos, Comp val) {
                Ref1D.this.set(pos, val.real);
            }
        };
    }

    default Ref2D rowMajor (int cols) {
        return new Ref2D() {
            @Override
            public int getRows() {
                return getSize() / cols;
            }

            @Override
            public int getCols() {
                return cols;
            }

            @Override
            public float get (int row, int col) {
                return Ref1D.this.get((row * cols) + col);
            }

            @Override
            public void set (int row, int col, float val) {
                Ref1D.this.set((row * cols) + col, val);
            }
        };
    }

    default Ref2D colMajor (int rows) {
        return new Ref2D() {
            @Override
            public int getRows() {
                return rows;
            }

            @Override
            public int getCols() {
                return getSize() / rows;
            }

            @Override
            public float get (int row, int col) {
                return Ref1D.this.get((col * rows) + row);
            }

            @Override
            public void set (int row, int col, float val) {
                Ref1D.this.set((col * rows) + row, val);
            }
        };
    }

    @Override
    default Iterator<Float> iterator() {
        return new Iterator<Float>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getSize();
            }

            @Override
            public Float next() {
                return get(i++);
            }
        };
    }
}
