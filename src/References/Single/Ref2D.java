package References.Single;

import Complex.Comp;
import References.Double.Ref2Dd;
import References.Single.Complex.Ref2Di;

import java.util.Iterator;

public interface Ref2D extends Iterable<Ref1D> {
    int getRows();
    int getCols();

    float get (int row, int col);
    void set (int row, int col, float val);

    default Ref1D get (int row) {
        return new Ref1D() {
            @Override
            public int getSize() {
                return getCols();
            }

            @Override
            public float get(int pos) {
                return Ref2D.this.get(row, pos);
            }

            @Override
            public void set(int pos, float val) {
                Ref2D.this.set(row, pos, val);
            }
        };
    }

    default void set (int row, Ref1D values) {
        int len = Math.min(getCols(), values.getSize());
        for (int i=0;i<len;i++) {
            this.set(row, i, values.get(i));
        }
    }

    default void set (Ref2D values) {
        int rows = Math.min(getRows(), values.getRows());
        int cols = Math.min(getCols(), values.getCols());

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                set(i, j, values.get(i, j));
            }
        }
    }

    default Ref2D T () {
        return new Ref2D() {
            @Override
            public int getRows() {
                return Ref2D.this.getCols();
            }

            @Override
            public int getCols() {
                return Ref2D.this.getRows();
            }

            @Override
            public float get (int row, int col) {
                return Ref2D.this.get(col, row);
            }

            @Override
            public void set(int row, int col, float val) {
                Ref2D.this.set(col, row, val);
            }
        };
    }

    default float[][] toArray () {
        float[][] array = new float[getRows()][getCols()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i).toArray();
        }

        return array;
    }

    default Ref2Dd toDouble () {
        return new Ref2Dd() {
            @Override
            public int getRows() {
                return Ref2D.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2D.this.getRows();
            }

            @Override
            public double get(int row, int col) {
                return Ref2D.this.get(row, col);
            }

            @Override
            public void set (int row, int col, double val) {
                Ref2D.this.set(row, col, (float) val);
            }
        };
    }

    default Ref2Di toComplex () {
        return new Ref2Di() {
            @Override
            public int getRows() {
                return Ref2D.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2D.this.getCols();
            }

            @Override
            public Comp get (int row, int col) {
                return new Comp(Ref2D.this.get(row, col), 0);
            }

            @Override
            public void set(int row, int col, Comp val) {
                Ref2D.this.set(row, col, val.real);
            }
        };
    }

    default Ref1D rowMajor () {
        return new Ref1D() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public float get (int pos) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                return Ref2D.this.get(row, col);
            }

            @Override
            public void set (int pos, float val) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                Ref2D.this.set(row, col, val);
            }
        };
    }

    default Ref1D colMajor () {
        return new Ref1D() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public float get(int pos) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                return Ref2D.this.get(row, col);
            }

            @Override
            public void set(int pos, float val) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                Ref2D.this.set(row, col, val);
            }
        };
    }

    @Override
    default Iterator<Ref1D> iterator() {
        return new Iterator<Ref1D>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getRows();
            }

            @Override
            public Ref1D next() {
                return get(i++);
            }
        };
    }
}
