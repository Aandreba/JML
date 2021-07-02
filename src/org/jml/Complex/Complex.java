package org.jml.Complex;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;

public interface Complex {
    Comp toFloat();
    Compd toDouble();
}
