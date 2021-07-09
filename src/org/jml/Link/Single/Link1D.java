package org.jml.Link.Single;

import org.jml.Vector.Single.Vec;

import java.io.Serializable;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Link1D implements Iterable<Float>, Serializable {
    final private static long serialVersionUID = -4959958410146648569L;
    final public int size;

    public Link1D (int size) {
        this.size = size;
    }

    public static Link1D init (int size, Function<Integer, Float> func) {
        return new Link1D(size) {
            @Override
            public float get(int pos) {
                return func.apply(pos);
            }
        };
    }

    public static Link1D init (Link1D a, Link1D b, BiFunction<Float, Float, Float> func) {
        if (a.size != b.size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return new Link1D (a.size) {
            @Override
            public float get(int i) {
                return func.apply(a.get(i), b.get(i));
            }
        };
    }

    public abstract float get (int pos);

    public Vec toVector () {
        return Vec.foreach(size, this::get);
    }

    @Override
    public Iterator<Float> iterator() {
        return new Iterator<Float>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Float next() {
                return get(i++);
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< size; i++) {
            builder.append(", ").append(get(i));
        }

        return "{ " + builder.substring(2) + " }";
    }
}
