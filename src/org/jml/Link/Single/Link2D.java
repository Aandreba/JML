package org.jml.Link.Single;

import org.jml.Matrix.Single.Mat;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Link2D implements Iterable<Link1D> {
    final public int rows, cols;

    public Link2D (int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public static Link2D init (int rows, int cols, BiFunction<Integer, Integer, Float> func) {
        return new Link2D (rows, cols) {
            @Override
            public float get(int i, int j) {
                return func.apply(i, j);
            }
        };
    }

    public static Link2D init (Link2D a, Link2D b, BiFunction<Float, Float, Float> func) {
        if (a.rows != b.rows || a.cols != b.cols) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return new Link2D (a.rows, a.cols) {
            @Override
            public float get(int i, int j) {
                return func.apply(a.get(i, j), b.get(i, j));
            }
        };
    }

    public abstract float get (int i, int j);

    public Link1D get (int i) {
        return new Link1D(cols) {
            @Override
            public float get (int pos) {
                return Link2D.this.get(i, pos);
            }
        };
    }

    public Link1D getCol (int j) {
        return new Link1D(rows) {
            @Override
            public float get (int pos) {
                return Link2D.this.get(pos, j);
            }
        };
    }

    public Link2D add (Link2D b) {
        return init(this, b, Float::sum);
    }

    public Link2D subtr (Link2D b) {
        return init(this, b, (x, y) -> x - y);
    }

    public Link2D T () {
        return new Link2D(cols, rows) {
            @Override
            public float get(int i, int j) {
                return Link2D.this.get(j, i);
            }
        };
    }

    public Mat toMatrix () {
        return Mat.foreach(rows, cols, (Mat.MatfForEachIndex) this::get);
    }

    @Override
    public Iterator<Link1D> iterator() {
        return new Iterator<Link1D>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < rows;
            }

            @Override
            public Link1D next() {
                return get(i++);
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i< rows; i++) {
            builder.append(", ").append(get(i).toString());
        }

        return "{ "+builder.substring(2)+" }";
    }
}
