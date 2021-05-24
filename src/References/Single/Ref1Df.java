package References.Single;

import References.Double.Ref1D;
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
