package org.jml.Number;

import org.jml.Number.Type.Float64;
import org.jml.Number.Type.Int64;
import org.jml.Number.Type.Float32;
import org.jml.Number.Type.Int32;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class RealNumber extends Number implements Comparable<RealNumber> {
    public abstract RealNumber add (RealNumber b);
    public abstract RealNumber subtr (RealNumber b);
    public abstract RealNumber mul (RealNumber b);
    public abstract RealNumber div (RealNumber b);

    public abstract RealNumber neg ();
    public abstract RealNumber abs ();
    public abstract RealNumber inv ();

    public RealNumber toInt () {
        return new Int32(intValue());
    }
    public RealNumber toLong () {
        return new Int64(longValue());
    }
    public RealNumber toFloat () {
        return new Float32(floatValue());
    }
    public RealNumber toDouble () {
        return new Float64(doubleValue());
    }

    public abstract BigDecimal decimalValue ();
    public abstract BigInteger integerValue ();
}
