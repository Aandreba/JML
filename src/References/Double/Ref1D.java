package References.Double;

import Imaginary.Comp;
import References.Double.Complex.Ref1Di;
import References.Single.Ref1Df;

import java.util.Iterator;

public interface Ref1D extends Iterable<Double> {
    int getSize();
    double get (int pos);
    void set (int pos, double val);

    default void set (Ref1D values) {
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

    default Ref1Df toFloat () {
        return new Ref1Df() {
            @Override
            public int getSize() {
                return Ref1D.this.getSize();
            }

            @Override
            public float get(int pos) {
                return (float) Ref1D.this.get(pos);
            }

            @Override
            public void set(int pos, float val) {
                Ref1D.this.set(pos, val);
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
            public double get (int row, int col) {
                return Ref1D.this.get((row * cols) + col);
            }

            @Override
            public void set (int row, int col, double val) {
                Ref1D.this.set((row * cols) + col, val);
            }
        };
    }

    default Ref2D colMajor (int rows) {
        return new Ref2D () {
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
                return Ref1D.this.get((col * rows) + row);
            }

            @Override
            public void set (int row, int col, double val) {
                Ref1D.this.set((col * rows) + row, val);
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
