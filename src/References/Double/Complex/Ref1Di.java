package References.Double.Complex;

import Imaginary.Comp;
import Imaginary.Compf;
import References.Single.Complex.Ref1Dif;

import java.util.Iterator;

public interface Ref1Di extends Iterable<Comp> {
    int getSize();
    Comp get (int pos);
    void set (int pos, Comp val);

    default void set (Ref1Di values) {
        int len = Math.min(getSize(), values.getSize());
        for (int i=0;i<len;i++) {
            set(i, values.get(i));
        }
    }

    default Comp[] toArray () {
        Comp[] array = new Comp[getSize()];
        for (int i=0;i<array.length;i++) {
            array[i] = get(i);
        }

        return array;
    }

    default Ref1Dif toFloat () {
        return new Ref1Dif () {
            @Override
            public int getSize() {
                return Ref1Di.this.getSize();
            }

            @Override
            public Compf get(int pos) {
                return Ref1Di.this.get(pos).toFloat();
            }

            @Override
            public void set(int pos, Compf val) {
                Ref1Di.this.set(pos, val.toDouble());
            }
        };
    }

    default Ref2Di rowMajor (int cols) {
        return new Ref2Di() {
            @Override
            public int getRows() {
                return getSize() / cols;
            }

            @Override
            public int getCols() {
                return cols;
            }

            @Override
            public Comp get (int row, int col) {
                return Ref1Di.this.get((row * cols) + col);
            }

            @Override
            public void set (int row, int col, Comp val) {
                Ref1Di.this.set((row * cols) + col, val);
            }
        };
    }

    default Ref2Di colMajor (int rows) {
        return new Ref2Di () {
            @Override
            public int getRows() {
                return rows;
            }

            @Override
            public int getCols() {
                return getSize() / rows;
            }

            @Override
            public Comp get (int row, int col) {
                return Ref1Di.this.get((col * rows) + row);
            }

            @Override
            public void set (int row, int col, Comp val) {
                Ref1Di.this.set((col * rows) + row, val);
            }
        };
    }

    @Override
    default Iterator<Comp> iterator() {
        return new Iterator<Comp>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getSize();
            }

            @Override
            public Comp next() {
                return get(i++);
            }
        };
    }
}
