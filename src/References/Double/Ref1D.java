package References.Double;

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
