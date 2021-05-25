package References.Single;

import Imaginary.Comp;
import Imaginary.Compf;
import References.Double.Ref1D;
import References.Single.Complex.Ref1Dif;

import java.util.Iterator;

public interface Ref1Df extends Iterable<Float> {
    int getSize();
    float get (int pos);
    void set (int pos, float val);

    default void set (Ref1Df values) {
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

    default Ref1D toDouble () {
        return new Ref1D() {
            @Override
            public int getSize() {
                return Ref1Df.this.getSize();
            }

            @Override
            public double get(int pos) {
                return Ref1Df.this.get(pos);
            }

            @Override
            public void set (int pos, double val) {
                Ref1Df.this.set(pos, (float) val);
            }
        };
    }

    default Ref1Dif toComplex () {
        return new Ref1Dif() {
            @Override
            public int getSize() {
                return Ref1Df.this.getSize();
            }

            @Override
            public Compf get(int pos) {
                return new Compf(Ref1Df.this.get(pos), 0);
            }

            @Override
            public void set (int pos, Compf val) {
                Ref1Df.this.set(pos, val.real);
            }
        };
    }

    default Ref2Df rowMajor (int cols) {
        return new Ref2Df() {
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
                return Ref1Df.this.get((row * cols) + col);
            }

            @Override
            public void set (int row, int col, float val) {
                Ref1Df.this.set((row * cols) + col, val);
            }
        };
    }

    default Ref2Df colMajor (int rows) {
        return new Ref2Df() {
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
                return Ref1Df.this.get((col * rows) + row);
            }

            @Override
            public void set (int row, int col, float val) {
                Ref1Df.this.set((col * rows) + row, val);
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
