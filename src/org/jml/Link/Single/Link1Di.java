package org.jml.Link.Single;

import org.jml.Complex.Single.Comp;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Link1Di implements Iterable<Comp> {
    final public int size;

    public Link1Di(int size) {
        this.size = size;
    }

    public static Link1Di init (int size, Function<Integer, Comp> func) {
        return new Link1Di(size) {
            @Override
            public Comp get(int pos) {
                return func.apply(pos);
            }
        };
    }

    public static Link1Di init (Link1Di a, Link1Di b, BiFunction<Comp, Comp, Comp> func) {
        if (a.size != b.size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return new Link1Di(a.size) {
            @Override
            public Comp get(int i) {
                return func.apply(a.get(i), b.get(i));
            }
        };
    }

    public abstract Comp get (int pos);

    public Veci toVector () {
        return Veci.foreach(size, this::get);
    }

    @Override
    public Iterator<Comp> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Comp next() {
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
