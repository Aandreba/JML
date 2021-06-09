package org.jml.Tuple;

public class Triple<A,B,C> {
    public A first;
    public B second;
    public C third;

    public Triple (A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
