package References.Single.Complex;

import Imaginary.Comp;
import Imaginary.Compf;
import References.Double.Complex.Ref1Di;

import java.util.Iterator;

public interface Ref1Dif extends Iterable<Compf> {
    int getSize();
    Compf get (int pos);
    void set (int pos, Compf val);

    default void set (Ref1Dif values) {
        int len = Math.min(getSize(), values.getSize());
        for (int i=0;i<len;i++) {
            set(i, values.get(i));
        }
    }

    default Compf[] toArray () {
        Compf[] array = new Compf[getSize()];
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
            public Comp get(int pos) {
                return Ref1Dif.this.get(pos).toDouble();
            }

            @Override
            public void set(int pos, Comp val) {
                Ref1Dif.this.set(pos, val.toFloat());
            }
        };
    }

    default Ref2Dif rowMajor (int cols) { // TODO
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
            public Compf get (int row, int col) {
                return Ref1Dif.this.get((row * cols) + col);
            }

            @Override
            public void set (int row, int col, Compf val) {
                Ref1Dif.this.set((row * cols) + col, val);
            }
        };
    }

    default Ref2Dif colMajor (int rows) { // TODO
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
            public Compf get (int row, int col) {
                return Ref1Dif.this.get((col * rows) + row);
            }

            @Override
            public void set (int row, int col, Compf val) {
                Ref1Dif.this.set((col * rows) + row, val);
            }
        };
    }

    @Override
    default Iterator<Compf> iterator() {
        return new Iterator<Compf>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getSize();
            }

            @Override
            public Compf next() {
                return get(i++);
            }
        };
    }
}
