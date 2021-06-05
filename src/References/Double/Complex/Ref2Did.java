package References.Double.Complex;

import Complex.Compd;
import Complex.Comp;
import References.Single.Complex.Ref2Di;

import java.util.Iterator;

public interface Ref2Did extends Iterable<Ref1Did> {
    int getRows();
    int getCols();

    Compd get (int row, int col);
    void set (int row, int col, Compd val);

    default Ref1Did get (int row) {
        return new Ref1Did() {
            @Override
            public int getSize() {
                return getCols();
            }

            @Override
            public Compd get(int pos) {
                return Ref2Did.this.get(row, pos);
            }

            @Override
            public void set(int pos, Compd val) {
                Ref2Did.this.set(row, pos, val);
            }
        };
    }

    default void set (int row, Ref1Did values) {
        int len = Math.min(getCols(), values.getSize());
        for (int i=0;i<len;i++) {
            this.set(row, i, values.get(i));
        }
    }

    default void set (Ref2Did values) {
        int rows = Math.min(getRows(), values.getRows());
        int cols = Math.min(getCols(), values.getCols());

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                set(i, j, values.get(i, j));
            }
        }
    }

    default Ref2Did T () {
        return new Ref2Did() {
            @Override
            public int getRows() {
                return Ref2Did.this.getCols();
            }

            @Override
            public int getCols() {
                return Ref2Did.this.getRows();
            }

            @Override
            public Compd get (int row, int col) {
                return Ref2Did.this.get(col, row);
            }

            @Override
            public void set(int row, int col, Compd val) {
                Ref2Did.this.set(col, row, val);
            }
        };
    }

    default Compd[][] toArray () {
        Compd[][] array = new Compd[getRows()][getCols()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i).toArray();
        }

        return array;
    }

    default Ref2Di toFloat () {
        return new Ref2Di() {
            @Override
            public int getRows() {
                return Ref2Did.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2Did.this.getCols();
            }

            @Override
            public Comp get(int row, int col) {
                return Ref2Did.this.get(row, col).toFloat();
            }

            @Override
            public void set(int row, int col, Comp val) {
                Ref2Did.this.set(row, col, val.toDouble());
            }
        };
    }

    default Ref1Did rowMajor () {
        return new Ref1Did() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public Compd get (int pos) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                return Ref2Did.this.get(row, col);
            }

            @Override
            public void set (int pos, Compd val) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                Ref2Did.this.set(row, col, val);
            }
        };
    }

    default Ref1Did colMajor () {
        return new Ref1Did() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public Compd get (int pos) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                return Ref2Did.this.get(row, col);
            }

            @Override
            public void set (int pos, Compd val) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                Ref2Did.this.set(row, col, val);
            }
        };
    }

    @Override
    default Iterator<Ref1Did> iterator() {
        return new Iterator<Ref1Did>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getRows();
            }

            @Override
            public Ref1Did next() {
                return get(i++);
            }
        };
    }
}
