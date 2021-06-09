package org.jml.Complex.Double;

import org.jml.Complex.Single.Quat;
import org.jml.Mathx.Mathd;
import org.jml.Matrix.Double.Matd;
import org.jml.Matrix.Double.Matid;
import org.jml.Vector.Double.Vecd;

import java.util.Objects;

public class Quatd {
    final public static Quatd ZERO = new Quatd();
    final public static Quatd ONE = new Quatd(1, 0, 0, 0);

    final public double a, i, j, k;

    public Quatd (double a, double i, double j, double k) {
        this.a = a;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    public Quatd (double roll, double pitch, double yaw) {
        double cy = Math.cos(yaw * 0.5f);
        double sy = Math.sin(yaw * 0.5f);
        double cp = Math.cos(pitch * 0.5f);
        double sp = Math.sin(pitch * 0.5f);
        double cr = Math.cos(roll * 0.5f);
        double sr = Math.sin(roll * 0.5f);

        this.a = cr * cp * cy + sr * sp * sy;
        this.i = sr * cp * cy - cr * sp * sy;
        this.j = cr * sp * cy + sr * cp * sy;
        this.k = cr * cp * sy - sr * sp * cy;
    }

    public Quatd() {
        this (0, 0, 0, 0);
    }

    public boolean isOnlyReal () {
        return a != 0 && i == 0 && j == 0 && k == 0;
    }

    public Quatd add (double y) {
        return new Quatd(a + y, i, j, k);
    }

    public Quatd add (Quatd y) {
        return new Quatd(a + y.a, i + y.i, j + y.j, k + y.k);
    }

    public Quatd subtr (double y) {
        return new Quatd(a - y, i, j, k);
    }

    public Quatd subtr (Quatd y) {
        return new Quatd(a - y.a, i - y.i, j - y.j, k - y.k);
    }

    public Quatd invSubtr (double y) {
        return new Quatd(y - a, i, j, k);
    }

    public Quatd mul (double y) {
        return new Quatd(a * y, i * y, j * y, k * y);
    }

    public Quatd mul (Quatd y) {
        double A = a*y.a - i*y.i - j*y.j - k*y.k;
        double I = a*y.i + i*y.a + j*y.k - k*y.j;
        double J = a*y.j - i*y.k + j*y.a + k*y.i;
        double K = a*y.k + i*y.j - j*y.i + k*y.a;

        return new Quatd(A, I, J, K);
    }

    public Quatd div (double y) {
        return new Quatd(a / y, i / y, j / y, k / y);
    }

    public Quatd div (Quatd y) {
        return inverse().mul(y);
    }

    public Quatd invDiv (double y) {
        return new Quatd(1 / y, 0, 0, 0).mul(this);
    }

    public Quatd inverse () {
        return conj().div(det());
    }

    public Quatd conj () {
        return new Quatd(a, -i, -j, -k);
    }

    public double det () {
        return a * a + i * i + j * j + k * k;
    }

    public double magnitude () {
        return Math.sqrt(det());
    }

    public Quatd unit () {
        return div(magnitude());
    }

    public Quatd exp () {
        double A = Math.exp(a);
        if (isOnlyReal()) {
            return new Quatd(A, 0, 0, 0);
        }

        Quatd vector = vectorPart();
        double mag = vector.magnitude();
        double sin = Math.sin(mag);
        double cos = Math.cos(mag);

        return vector.div(mag).mul(sin).add(cos).mul(A);
    }

    public Quatd log () {
        if (isOnlyReal()) {
            return new Quatd(Math.log(a), 0, 0, 0);
        }

        Quatd vector = vectorPart();
        double mag = magnitude();
        double B = Math.log(mag);

        return vector.unit().mul(Math.acos(a / mag)).add(B);
    }

    public Quatd pow (double y) {
        if (isOnlyReal()) {
            return new Quatd(Math.pow(a, y), 0, 0, 0);
        }

        return log().mul(y).exp();
    }

    public Quatd pow (Quatd y) {
        return log().mul(y).exp();
    }

    public Quatd sqrt () {
        if (isOnlyReal()) {
            return new Quatd(Math.sqrt(a), 0, 0, 0);
        }

        return pow(0.5);
    }

    public Quatd vectorPart () {
        return new Quatd(0, i, j, k);
    }

    public double[] toArray () {
        return new double[]{ a, i, j, k };
    }

    public Quat toFloat () {
        return new Quat((float) a, (float) i, (float) j, (float) k);
    }

    public Vecd toEulerRadians() {
        Vecd angles = new Vecd(3);

        double sinr_cosp = 2 * (a * i + j * k);
        double cosr_cosp = 1 - 2 * (i * i + j * j);
        angles.set(0, Math.atan2(sinr_cosp, cosr_cosp));

        double sinp = 2 * (a * j - k * i);
        if (Math.abs(sinp) >= 1) {
            angles.set(1, Math.copySign(Mathd.HALFPI, sinp)); // use 90 degrees if out of range
        } else {
            angles.set(1, Math.asin(sinp));
        }

        double siny_cosp = 2 * (a * k + i * j);
        double cosy_cosp = 1 - 2 * (j * j + k * k);
        angles.set(2, Math.atan2(siny_cosp, cosy_cosp));

        return angles;
    }

    public Vecd toEulerDegrees () {
        return toEulerRadians().mul(Mathd.TO_DEGREES);
    }

    public Matid toComplexMatrix () {
        return new Matid(new Compd[][]{ { new Compd(a, i), new Compd(j, k) }, { new Compd(-j, k), new Compd(a, -i) } });
    }

    public Matd toRealMatrix () {
        return new Matd(new double[][]{ {a, -i, -j, -k}, {i, a, -k, j}, {j, k, a, -i}, {k, -j, i, a} });
    }

    public static Quatd slerp (Quatd a, Quatd b, float t) {
        return b.mul(a.inverse()).pow(t).mul(a);
    }

    @Override
    public Quatd clone() {
        return new Quatd(a, i, j, k);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quatd quat = (Quatd) o;
        return Double.compare(quat.a, a) == 0 &&
                Double.compare(quat.i, i) == 0 &&
                Double.compare(quat.j, j) == 0 &&
                Double.compare(quat.k, k) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, i, j, k);
    }

    @Override
    public String toString() {
        final char[] symbols = new char[]{ (char)0, 'i', 'j', 'k' };
        StringBuilder builder = new StringBuilder();

        double[] vals = toArray();
        for (int i=0;i<4;i++) {
            if (Double.isInfinite(vals[i])) {
                return "Infinity";
            } else if (Double.isNaN(vals[i])) {
                return "NaN";
            } else if (vals[i] != 0) {
                builder.append(builder.length() == 0 ? (vals[i] < 0 ? "-" : "") : (vals[i] < 0 ? " - " : " + "));
                builder.append(Math.abs(vals[i])).append(symbols[i]);
            }
        }

        return builder.toString();
    }
}
