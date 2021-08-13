package org.jml.Number.Type;

import org.jml.Extra.Intx;
import org.jml.Number.DecimalNumber;
import org.jml.Number.RealNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Half extends DecimalNumber {
    final public boolean sign;
    final private byte exponent;
    final private short mantissa;

    private Integer signum = null;
    private Short bits = null;

    private Half (short bits) {
        this.sign = bits < 0;
        this.exponent = (byte) ((bits >> 10) & 0x1f);
        this.mantissa = (short) (bits & 0x3ff);
    }

    private Half (boolean sign, byte exp, short mantissa) {
        this.sign = sign;
        this.exponent = exp;
        this.mantissa = mantissa;
    }

    public Half (float value) {
        this((short) fromFloat(value));
    }

    public int signum () {
        if (signum == null) {
            signum = (exponent == 0 && mantissa == 0) ? 0 : (sign ? -1 : 1);
        }

        return signum;
    }

    public Half add (Half b) {
        return absAdd(this, b);
    }

    public short getBits () {
        if (bits == null) {
            int bits = sign ? Short.MAX_VALUE : 0;
            bits |= exponent << 10;
            bits |= (mantissa & 0x3ff);

            this.bits = (short) bits;
        }

        return bits;
    }

    @Override
    public DecimalNumber neg() {
        return null;
    }

    @Override
    public DecimalNumber abs() {
        return null;
    }

    @Override
    public RealNumber add(RealNumber b) {
        return null;
    }

    @Override
    public RealNumber subtr(RealNumber b) {
        return null;
    }

    @Override
    public RealNumber mul(RealNumber b) {
        return null;
    }

    @Override
    public RealNumber div(RealNumber b) {
        return null;
    }

    @Override
    public RealNumber inv() {
        return null;
    }

    @Override
    public BigDecimal decimalValue() {
        return null;
    }

    @Override
    public int compareTo(RealNumber o) {
        return 0;
    }

    /**
     * @see <a href="https://stackoverflow.com/a/6162687/237321">Original code</a>
     */
    @Override
    public float floatValue () {
        int bits = getBits();

        int mant = bits & 0x03ff;            // 10 bits mantissa
        int exp =  bits & 0x7c00;            // 5 bits exponent
        if( exp == 0x7c00 )                   // NaN/Inf
            exp = 0x3fc00;                    // -> NaN/Inf
        else if( exp != 0 )                   // normalized value
        {
            exp += 0x1c000;                   // exp - 15 + 127
            if( mant == 0 && exp > 0x1c400 )  // smooth transition
                return Float.intBitsToFloat( ( bits & 0x8000 ) << 16
                        | exp << 13 | 0x3ff );
        }
        else if( mant != 0 )                  // && exp==0 -> subnormal
        {
            exp = 0x1c400;                    // make it normal
            do {
                mant <<= 1;                   // mantissa * 2
                exp -= 0x400;                 // decrease exp by 1
            } while( ( mant & 0x400 ) == 0 ); // while not normal
            mant &= 0x3ff;                    // discard subnormal bit
        }                                     // else +/-0 -> +/-0
        return Float.intBitsToFloat(          // combine all parts
                ( bits & 0x8000 ) << 16          // sign  << ( 31 - 15 )
                        | ( exp | mant ) << 13 );         // va
    }

    @Override
    public double doubleValue() {
        return 0;
    }

    /**
     * @see <a href="https://stackoverflow.com/a/6162687/237321">Original code</a>
     */
    private static int fromFloat( float fval ) {
        int fbits = Float.floatToIntBits( fval );
        int sign = fbits >>> 16 & 0x8000;          // sign only
        int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

        if( val >= 0x47800000 )               // might be or become NaN/Inf
        {                                     // avoid Inf due to rounding
            if( ( fbits & 0x7fffffff ) >= 0x47800000 )
            {                                 // is or must become NaN/Inf
                if( val < 0x7f800000 )        // was value but too large
                    return sign | 0x7c00;     // make it +/-Inf
                return sign | 0x7c00 |        // remains +/-Inf or NaN
                        ( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
            }
            return sign | 0x7bff;             // unrounded not quite Inf
        }
        if( val >= 0x38800000 )               // remains normalized value
            return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
        if( val < 0x33000000 )                // too small for subnormal
            return sign;                      // becomes +/-0
        val = ( fbits & 0x7fffffff ) >>> 23;  // tmp exp for subnormal calc
        return sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
                + ( 0x800000 >>> val - 102 )     // round depending on cut off
                >>> 126 - val );   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
    }

    private static Half absAdd (Half a, Half b) {
        byte exp1 = a.exponent;
        byte exp2 = b.exponent;

        int mant1 = a.mantissa;
        int mant2 = b.mantissa;

        int delta = exp1 - exp2;
        if (delta > 0) {
            mant2 >>>= delta;
        } else if (delta < 0) {
            exp1 = exp2;
            mant2 >>>= -delta;
        }

        byte exp = exp1;
        int mant = mant1 + mant2;

        int lmb = Intx.leftMostBit(mant);
        delta = lmb - 9;
        exp += delta;

        if (delta > 0) {
            mant >>>= delta;
        } else if (delta < 0) {
            mant <<= -delta;
        }

        return new Half(false, exp, (short) mant);
    }
}
