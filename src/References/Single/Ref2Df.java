package References.Single;

import References.Double.Ref1D;
import References.Double.Ref2D;
import java.util.Iterator;

public interface Ref2Df extends Iterable<Ref1Df> {
    int getRows();
    int getCols();

    float get (int row, int col);
    void set (int row, int col, float val);

    default Ref1Df get (int row) {
        return new Ref1Df() {
            @Override
            public int getSize() {
                return getCols();
            }

            @Override
            public float get(int pos) {
                return Ref2Df.this.get(row, pos);
            }

            @Override
            public void set(int pos, float val) {
                Ref2Df.this.set(row, pos, val);
            }
        };
    }

    default void set (int row, Ref1Df values) {
        int len = Math.min(getCols(), values.getSize());
        for (int i=0;i<len;i++) {
            this.set(row, i, values.get(i));
        }
    }

    default void set (Ref2Df values) {
        int rows = Math.min(getRows(), values.getRows());
        int cols = Math.min(getCols(), values.getCols());

        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                set(i, j, values.get(i, j));
            }
        }
    }

    default Ref2Df T () {
        return new Ref2Df() {
            @Override
            public int getRows() {
                return Ref2Df.this.getCols();
            }

            @Override
            public int getCols() {
                return Ref2Df.this.getRows();
            }

            @Override
            public float get (int row, int col) {
                return Ref2Df.this.get(col, row);
            }

            @Override
            public void set(int row, int col, float val) {
                Ref2Df.this.set(col, row, val);
            }
        };
    }

    default float[][] toArray () {
        float[][] array = new float[getRows()][getCols()];
        for (int i=0;i<array.length;i++) {
            for (int j=0;j<array[i].length;j++) {
                array[i][j] = get(i, j);
            }
        }

        return array;
    }

    default Ref2D toDouble () {
        return new Ref2D() {
            @Override
            public int getRows() {
                return Ref2Df.this.getRows();
            }

            @Override
            public int getCols() {
                return Ref2Df.this.getRows();
            }

            @Override
            public double get(int row, int col) {
                return Ref2Df.this.get(row, col);
            }

            @Override
            public void set (int row, int col, double val) {
                Ref2Df.this.set(row, col, (float) val);
            }
        };
    }

    default Ref1Df rowMajor () {
        return new Ref1Df() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public float get (int pos) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                return Ref2Df.this.get(row, col);
            }

            @Override
            public void set (int pos, float val) {
                int cols = getCols();
                int row = pos / cols;
                int col = pos % cols;

                Ref2Df.this.set(row, col, val);
            }
        };
    }

    default Ref1Df colMajor () {
        return new Ref1Df() {
            @Override
            public int getSize() {
                return getRows() * getCols();
            }

            @Override
            public float get(int pos) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                return Ref2Df.this.get(row, col);
            }

            @Override
            public void set(int pos, float val) {
                int rows = getRows();
                int col = pos / rows;
                int row = pos % rows;

                Ref2Df.this.set(row, col, val);
            }
        };
    }

    @Override
    default Iterator<Ref1Df> iterator() {
        return new Iterator<Ref1Df>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getRows();
            }

            @Override
            public Ref1Df next() {
                return get(i++);
            }
        };
    }
}
