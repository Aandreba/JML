package org.jml.Number;

import java.math.BigDecimal;

public abstract class IntegerNumber extends RealNumber {
    @Override
    public float floatValue() {
        return intValue();
    }

    @Override
    public double doubleValue() {
        return longValue();
    }

    @Override
    public BigDecimal decimalValue() {
        return new BigDecimal(integerValue());
    }

    @Override
    public abstract IntegerNumber neg ();

    @Override
    public abstract IntegerNumber abs ();
}
