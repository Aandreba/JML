package References.Double;

import Complex.Compd;
import References.Double.Complex.Ref2Did;
import References.Single.Ref2D;

import java.util.Iterator;

public interface Ref2Dd extends Iterable<Ref1Dd> {
    int getRows();
    int getCols();

    double get (int row, int col);
    void set (int row, int col, double val);

    default Ref1Dd get (int row) {
        return new Ref1Dd() {
            @Override
            public int getSize() {
                return getCols();
            }

            @Override
            public double get(int pos) {
                return Ref2Dd.this.get(row, pos);
            }

            @Override
            public void set(int pos, double val) {
                Ref2Dd.this.set(row, pos, val);
            }
        };
    }

    default void set (int row, Ref1Dd values) {
        int len = Math.min(getCols(), values.getSize());
        for (int i=0;i<len;i++) {
            this.set(row, i, values.get(i));
        }
    }

    default void set (Ref2Dd values) {
        int rows = Math.min(getRows(), values.getRows());
        int cols = Math.min(getCols(), values.getCols());

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                set(i, j, values.get(i, j));
            }
        }
    }

    default Ref2Dd T () {
        return new Ref2Dd() {
            @Override
            public int getRows() {
                return Ref2Dd.this.getCols();
            }

            @Override
            public int getCols() {
                return Ref2Dd.this.getRows();
            }

            @Override
            public double get (int row, int col) {
                return Ref2Dd.this.get(col, row);
            }

            @Override
            public void set(int row, int col, double val) {
                Ref2Dd.this.set(col, row, val);
            }
        };
    }

    default double[][] toArray () {
        double[][] array = new double[getRows()][getCols()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i).toArray();
        }

        return array;
    }

    default Ref2D toFloat () {
        return new Ref2D() {
            @Override
            public int getRows() {
                return Ref2Dd.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2Dd.this.getCols();
            }

            @Override
            public float get(int row, int col) {
                return (float) Ref2Dd.this.get(row, col);
            }

            @Override
            public void set(int row, int col, float val) {
                Ref2Dd.this.set(row, col, val);
            }
        };
    }

    default Ref2Did toComplex () {
        return new Ref2Did() {
            @Override
            public int getRows() {
                return Ref2Dd.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2Dd.this.getCols();
            }

            @Override
            public Compd get (int row, int col) {
                return new Compd(Ref2Dd.this.get(row, col), 0);
            }

            @Override
            public void set(int row, int col, Compd val) {
                Ref2Dd.this.set(row, col, val.real);
            }
        };
    }

    default Ref1Dd rowMajor () {
        return new Ref1Dd() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public double get (int pos) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                return Ref2Dd.this.get(row, col);
            }

            @Override
            public void set (int pos, double val) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                Ref2Dd.this.set(row, col, val);
            }
        };
    }

    default Ref1Dd colMajor () {
        return new Ref1Dd() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public double get (int pos) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                return Ref2Dd.this.get(row, col);
            }

            @Override
            public void set (int pos, double val) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                Ref2Dd.this.set(row, col, val);
            }
        };
    }

    @Override
    default Iterator<Ref1Dd> iterator() {
        return new Iterator<Ref1Dd>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getRows();
            }

            @Override
            public Ref1Dd next() {
                return get(i++);
            }
        };
    }
}
