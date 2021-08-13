package org.jml.Number;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class DecimalNumber extends RealNumber {
    @Override
    public int intValue() {
        return (int) floatValue();
    }

    @Override
    public long longValue() {
        return (long) doubleValue();
    }

    @Override
    public BigInteger integerValue() {
        return BigInteger.valueOf(longValue());
    }

    @Override
    public abstract DecimalNumber neg();

    @Override
    public abstract DecimalNumber abs();
}
