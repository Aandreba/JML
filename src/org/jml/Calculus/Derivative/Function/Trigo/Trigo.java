package org.jml.Calculus.Derivative.Function.Trigo;

import org.jml.Calculus.Derivative.Func;
import org.jml.Calculus.Derivative.Function.Regular.Const;

public class Trigo {
    final public static Sin SIN = new Sin();
    final public static Cos COS = new Cos();
    final public static Tan TAN = new Tan();

    final public static Asin ASIN = new Asin();
    final public static Func SEC = Const.ONE.div(COS);
}
