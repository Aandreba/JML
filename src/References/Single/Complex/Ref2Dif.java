package References.Single.Complex;

import Imaginary.Comp;
import Imaginary.Compf;
import References.Double.Complex.Ref2Di;

import java.util.Iterator;

public interface Ref2Dif extends Iterable<Ref1Dif> {
    int getRows();
    int getCols();

    Compf get (int row, int col);
    void set (int row, int col, Compf val);

    default Ref1Dif get (int row) {
        return new Ref1Dif() {
            @Override
            public int getSize() {
                return getCols();
            }

            @Override
            public Compf get(int pos) {
                return Ref2Dif.this.get(row, pos);
            }

            @Override
            public void set(int pos, Compf val) {
                Ref2Dif.this.set(row, pos, val);
            }
        };
    }

    default void set (int row, Ref1Dif values) {
        int len = Math.min(getCols(), values.getSize());
        for (int i=0;i<len;i++) {
            this.set(row, i, values.get(i));
        }
    }

    default void set (Ref2Dif values) {
        int rows = Math.min(getRows(), values.getRows());
        int cols = Math.min(getCols(), values.getCols());

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                set(i, j, values.get(i, j));
            }
        }
    }

    default Ref2Dif T () {
        return new Ref2Dif() {
            @Override
            public int getRows() {
                return Ref2Dif.this.getCols();
            }

            @Override
            public int getCols() {
                return Ref2Dif.this.getRows();
            }

            @Override
            public Compf get (int row, int col) {
                return Ref2Dif.this.get(col, row);
            }

            @Override
            public void set(int row, int col, Compf val) {
                Ref2Dif.this.set(col, row, val);
            }
        };
    }

    default Compf[][] toArray () {
        Compf[][] array = new Compf[getRows()][getCols()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i).toArray();
        }

        return array;
    }

    default Ref2Di toDouble () { // TODO
        return new Ref2Di() {
            @Override
            public int getRows() {
                return Ref2Dif.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2Dif.this.getCols();
            }

            @Override
            public Comp get(int row, int col) {
                return Ref2Dif.this.get(row, col).toDouble();
            }

            @Override
            public void set(int row, int col, Comp val) {
                Ref2Dif.this.set(row, col, val.toFloat());
            }
        };
    }

    default Ref1Dif rowMajor () { // TODO
        return new Ref1Dif() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public Compf get (int pos) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                return Ref2Dif.this.get(row, col);
            }

            @Override
            public void set (int pos, Compf val) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                Ref2Dif.this.set(row, col, val);
            }
        };
    }

    default Ref1Dif colMajor () { // TODO
        return new Ref1Dif() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public Compf get (int pos) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                return Ref2Dif.this.get(row, col);
            }

            @Override
            public void set (int pos, Compf val) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                Ref2Dif.this.set(row, col, val);
            }
        };
    }

    @Override
    default Iterator<Ref1Dif> iterator() {
        return new Iterator<Ref1Dif>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getRows();
            }

            @Override
            public Ref1Dif next() {
                return get(i++);
            }
        };
    }
}
