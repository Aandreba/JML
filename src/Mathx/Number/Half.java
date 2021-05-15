package Mathx.Number;

public class Half extends Number {
    final private short bits;

    public Half (short bits) {
        this.bits = bits;
    }

    public Half (float value) {
        // Sign
        int bits = Float.floatToIntBits(value);
        short fbits = bits < 0 ? Short.MIN_VALUE : 0;

        // Exponent
        int exponent = (Math.getExponent(value) - 15) & 0x5;
        exponent = exponent; // TODO Convert exponent to Offset binary (FLip last bit)
        // TODO Shorten exponent from 8 bits to 5 bits
        fbits |= exponent << 10;

        // Significand
        int significand = (bits >> 13) & 0x3FF;
        fbits |= significand;

        // Print bits
        for (int i=15;i>=0;i--) {
            System.out.print((fbits >> i) & 1);
        }
        System.out.println();

        this.bits = fbits;
    }

    @Override
    public int intValue() {
        return (int) floatValue();
    }

    @Override
    public long longValue() {
        return (long) floatValue();
    }

    @Override
    public float floatValue() {
        // Sign
        int ibits = bits < 0 ? Integer.MIN_VALUE : 0;

        // Exponent
        int exponent = (bits >> 10) & 0x1F;
        ibits |= exponent << 26;

        // Significand
        int significand = (bits & 0x3FF);
        ibits |= significand << 13;

        return Float.intBitsToFloat(ibits);
    }

    @Override
    public double doubleValue() {
        return floatValue();
    }

    @Override
    public String toString() {
        return Float.toString(floatValue());
    }
}
