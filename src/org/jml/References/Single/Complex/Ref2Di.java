package org.jml.References.Single.Complex;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.References.Double.Complex.Ref2Did;

import java.util.Iterator;

public interface Ref2Di extends Iterable<Ref1Di> {
    int getRows();
    int getCols();

    Comp get (int row, int col);
    void set (int row, int col, Comp val);

    default Ref1Di get (int row) {
        return new Ref1Di() {
            @Override
            public int getSize() {
                return getCols();
            }

            @Override
            public Comp get(int pos) {
                return Ref2Di.this.get(row, pos);
            }

            @Override
            public void set(int pos, Comp val) {
                Ref2Di.this.set(row, pos, val);
            }
        };
    }

    default void set (int row, Ref1Di values) {
        int len = Math.min(getCols(), values.getSize());
        for (int i=0;i<len;i++) {
            this.set(row, i, values.get(i));
        }
    }

    default void set (Ref2Di values) {
        int rows = Math.min(getRows(), values.getRows());
        int cols = Math.min(getCols(), values.getCols());

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                set(i, j, values.get(i, j));
            }
        }
    }

    default Ref2Di conj () {
        return new Ref2Di() {
            @Override
            public int getRows() {
                return Ref2Di.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2Di.this.getCols();
            }

            @Override
            public Comp get(int row, int col) {
                return Ref2Di.this.get(row, col).conj();
            }

            @Override
            public void set(int row, int col, Comp val) {
                Ref2Di.this.set(row, col, val);
            }
        };
    }

    default Ref2Di T () {
        return new Ref2Di() {
            @Override
            public int getRows() {
                return Ref2Di.this.getCols();
            }

            @Override
            public int getCols() {
                return Ref2Di.this.getRows();
            }

            @Override
            public Comp get (int row, int col) {
                return Ref2Di.this.get(col, row);
            }

            @Override
            public void set(int row, int col, Comp val) {
                Ref2Di.this.set(col, row, val);
            }
        };
    }

    default Comp[][] toArray () {
        Comp[][] array = new Comp[getRows()][getCols()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i).toArray();
        }

        return array;
    }

    default Ref2Did toDouble () {
        return new Ref2Did() {
            @Override
            public int getRows() {
                return Ref2Di.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2Di.this.getCols();
            }

            @Override
            public Compd get(int row, int col) {
                return Ref2Di.this.get(row, col).toDouble();
            }

            @Override
            public void set(int row, int col, Compd val) {
                Ref2Di.this.set(row, col, val.toFloat());
            }
        };
    }

    default Ref1Di rowMajor () {
        return new Ref1Di() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public Comp get (int pos) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                return Ref2Di.this.get(row, col);
            }

            @Override
            public void set (int pos, Comp val) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                Ref2Di.this.set(row, col, val);
            }
        };
    }

    default Ref1Di colMajor () {
        return new Ref1Di() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public Comp get (int pos) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                return Ref2Di.this.get(row, col);
            }

            @Override
            public void set (int pos, Comp val) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                Ref2Di.this.set(row, col, val);
            }
        };
    }

    @Override
    default Iterator<Ref1Di> iterator() {
        return new Iterator<Ref1Di>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getRows();
            }

            @Override
            public Ref1Di next() {
                return get(i++);
            }
        };
    }
}
