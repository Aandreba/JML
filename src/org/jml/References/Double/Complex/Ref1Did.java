package org.jml.References.Double.Complex;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.References.Single.Complex.Ref1Di;

import java.util.Iterator;

public interface Ref1Did extends Iterable<Compd> {
    int getSize();
    Compd get (int pos);
    void set (int pos, Compd val);

    default void set (Ref1Did values) {
        int len = Math.min(getSize(), values.getSize());
        for (int i=0;i<len;i++) {
            set(i, values.get(i));
        }
    }

    default Ref1Did conj () {
        return new Ref1Did() {
            @Override
            public int getSize() {
                return Ref1Did.this.getSize();
            }

            @Override
            public Compd get (int pos) {
                return Ref1Did.this.get(pos).conj();
            }

            @Override
            public void set (int pos, Compd val) {
                Ref1Did.this.set(pos, val);
            }
        };
    }

    default Compd[] toArray () {
        Compd[] array = new Compd[getSize()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i);
        }

        return array;
    }

    default Ref1Di toFloat () {
        return new Ref1Di() {
            @Override
            public int getSize() {
                return Ref1Did.this.getSize();
            }

            @Override
            public Comp get(int pos) {
                return Ref1Did.this.get(pos).toFloat();
            }

            @Override
            public void set(int pos, Comp val) {
                Ref1Did.this.set(pos, val.toDouble());
            }
        };
    }

    default Ref2Did rowMajor (int cols) {
        return new Ref2Did() {
            @Override
            public int getRows() {
                return getSize() / cols;
            }

            @Override
            public int getCols() {
                return cols;
            }

            @Override
            public Compd get (int row, int col) {
                return Ref1Did.this.get((row * cols) + col);
            }

            @Override
            public void set (int row, int col, Compd val) {
                Ref1Did.this.set((row * cols) + col, val);
            }
        };
    }

    default Ref2Did colMajor (int rows) {
        return new Ref2Did() {
            @Override
            public int getRows() {
                return rows;
            }

            @Override
            public int getCols() {
                return getSize() / rows;
            }

            @Override
            public Compd get (int row, int col) {
                return Ref1Did.this.get((col * rows) + row);
            }

            @Override
            public void set (int row, int col, Compd val) {
                Ref1Did.this.set((col * rows) + row, val);
            }
        };
    }

    @Override
    default Iterator<Compd> iterator() {
        return new Iterator<Compd>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getSize();
            }

            @Override
            public Compd next() {
                return get(i++);
            }
        };
    }
}
