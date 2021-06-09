package org.jml.Tuple;

public class Couple<A,B> {
    public A first;
    public B second;

    public Couple (A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Couple {" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
