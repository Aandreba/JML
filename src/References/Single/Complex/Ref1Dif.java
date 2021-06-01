package References.Single.Complex;

import Complex.Compd;
import Complex.Comp;
import References.Double.Complex.Ref1Di;

import java.util.Iterator;

public interface Ref1Dif extends Iterable<Comp> {
    int getSize();
    Comp get (int pos);
    void set (int pos, Comp val);

    default void set (Ref1Dif values) {
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

    default Ref1Di toDouble () {
        return new Ref1Di () {
            @Override
            public int getSize() {
                return Ref1Dif.this.getSize();
            }

            @Override
            public Compd get(int pos) {
                return Ref1Dif.this.get(pos).toDouble();
            }

            @Override
            public void set(int pos, Compd val) {
                Ref1Dif.this.set(pos, val.toFloat());
            }
        };
    }

    default Ref2Dif rowMajor (int cols) {
        return new Ref2Dif() {
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
                return Ref1Dif.this.get((row * cols) + col);
            }

            @Override
            public void set (int row, int col, Comp val) {
                Ref1Dif.this.set((row * cols) + col, val);
            }
        };
    }

    default Ref2Dif colMajor (int rows) {
        return new Ref2Dif () {
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
                return Ref1Dif.this.get((col * rows) + row);
            }

            @Override
            public void set (int row, int col, Comp val) {
                Ref1Dif.this.set((col * rows) + row, val);
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
