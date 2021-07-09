package org.jml.Complex.Single;

import org.jml.Complex.Double.Quatd;
import org.jml.Mathx.Mathf;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Single.Vec;

import java.io.Serializable;
import java.util.Objects;

public class Quat implements Serializable {
    final private static long serialVersionUID = 711289373379645576L;
    
    final public static Quat ZERO = new Quat();
    final public static Quat ONE = new Quat(1, 0, 0, 0);

    final public float a, i, j, k;

    public Quat (float a, float i, float j, float k) {
        this.a = a;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    public Quat (float roll, float pitch, float yaw) {
        float cy = Mathf.cos(yaw * 0.5f);
        float sy = Mathf.sin(yaw * 0.5f);
        float cp = Mathf.cos(pitch * 0.5f);
        float sp = Mathf.sin(pitch * 0.5f);
        float cr = Mathf.cos(roll * 0.5f);
        float sr = Mathf.sin(roll * 0.5f);

        this.a = cr * cp * cy + sr * sp * sy;
        this.i = sr * cp * cy - cr * sp * sy;
        this.j = cr * sp * cy + sr * cp * sy;
        this.k = cr * cp * sy - sr * sp * cy;
    }

    public Quat() {
        this (0, 0, 0, 0);
    }

    public boolean isOnlyReal () {
        return a != 0 && i == 0 && j == 0 && k == 0;
    }

    public Quat add (float y) {
        return new Quat(a + y, i, j, k);
    }

    public Quat add (Quat y) {
        return new Quat(a + y.a, i + y.i, j + y.j, k + y.k);
    }

    public Quat subtr (float y) {
        return new Quat(a - y, i, j, k);
    }

    public Quat subtr (Quat y) {
        return new Quat(a - y.a, i - y.i, j - y.j, k - y.k);
    }

    public Quat invSubtr (float y) {
        return new Quat(y - a, i, j, k);
    }

    public Quat mul (float y) {
        return new Quat(a * y, i * y, j * y, k * y);
    }

    public Quat mul (Quat y) {
        float A = a*y.a - i*y.i - j*y.j - k*y.k;
        float I = a*y.i + i*y.a + j*y.k - k*y.j;
        float J = a*y.j - i*y.k + j*y.a + k*y.i;
        float K = a*y.k + i*y.j - j*y.i + k*y.a;

        return new Quat(A, I, J, K);
    }

    public Quat div (float y) {
        return new Quat(a / y, i / y, j / y, k / y);
    }

    public Quat div (Quat y) {
        return inverse().mul(y);
    }

    public Quat invDiv (float y) {
        return new Quat(1 / y, 0, 0, 0).mul(this);
    }

    public Quat inverse () {
        return conj().div(det());
    }

    public Quat conj () {
        return new Quat(a, -i, -j, -k);
    }

    public float det () {
        return a * a + i * i + j * j + k * k;
    }

    public float magnitude () {
        return Mathf.sqrt(det());
    }

    public Quat unit () {
        return div(magnitude());
    }

    public Quat exp () {
        float A = Mathf.exp(a);
        if (isOnlyReal()) {
            return new Quat(A, 0, 0, 0);
        }

        Quat vector = vectorPart();
        float mag = vector.magnitude();
        float sin = Mathf.sin(mag);
        float cos = Mathf.cos(mag);

        return vector.div(mag).mul(sin).add(cos).mul(A);
    }

    public Quat log () {
        if (isOnlyReal()) {
            return new Quat(Mathf.log(a), 0, 0, 0);
        }

        Quat vector = vectorPart();
        float mag = magnitude();
        float B = Mathf.log(mag);

        return vector.unit().mul(Mathf.acos(a / mag)).add(B);
    }

    public Quat pow (float y) {
        if (isOnlyReal()) {
            return new Quat(Mathf.pow(a, y), 0, 0, 0);
        }

        return log().mul(y).exp();
    }

    public Quat pow (Quat y) {
        return log().mul(y).exp();
    }

    public Quat sqrt () {
        if (isOnlyReal()) {
            return new Quat(Mathf.sqrt(a), 0, 0, 0);
        }

        return pow(0.5f);
    }

    public Quat vectorPart () {
        return new Quat(0, i, j, k);
    }

    public float[] toArray () {
        return new float[]{ a, i, j, k };
    }

    public Quatd toDouble () {
        return new Quatd(a, i, j, k);
    }

    public Vec toEulerRadians() {
        Vec angles = new Vec(3);

        float sinr_cosp = 2 * (a * i + j * k);
        float cosr_cosp = 1 - 2 * (i * i + j * j);
        angles.set(0, Mathf.atan2(sinr_cosp, cosr_cosp));

        float sinp = 2 * (a * j - k * i);
        if (Mathf.abs(sinp) >= 1) {
            angles.set(1, Mathf.copySign(Mathf.HALFPI, sinp)); // use 90 degrees if out of range
        } else {
            angles.set(1, Mathf.asin(sinp));
        }

        float siny_cosp = 2 * (a * k + i * j);
        float cosy_cosp = 1 - 2 * (j * j + k * k);
        angles.set(2, Mathf.atan2(siny_cosp, cosy_cosp));

        return angles;
    }

    public Vec toEulerDegrees () {
        return toEulerRadians().mul(Mathf.TO_DEGREES);
    }

    public Mati toComplexMatrix () {
        return new Mati(new Comp[][]{ { new Comp(a, i), new Comp(j, k) }, { new Comp(-j, k), new Comp(a, -i) } });
    }

    public Mat toRealMatrix () {
        return new Mat(new float[][]{ {a, -i, -j, -k}, {i, a, -k, j}, {j, k, a, -i}, {k, -j, i, a} });
    }

    public static Quat slerp (Quat a, Quat b, float t) {
        return b.mul(a.inverse()).pow(t).mul(a);
    }

    @Override
    public Quat clone() {
        return new Quat(a, i, j, k);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quat quat = (Quat) o;
        return Float.compare(quat.a, a) == 0 &&
                Float.compare(quat.i, i) == 0 &&
                Float.compare(quat.j, j) == 0 &&
                Float.compare(quat.k, k) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, i, j, k);
    }

    @Override
    public String toString() {
        final char[] symbols = new char[]{ (char)0, 'i', 'j', 'k' };
        StringBuilder builder = new StringBuilder();

        float[] vals = toArray();
        for (int i=0;i<4;i++) {
            if (Float.isInfinite(vals[i])) {
                return "Infinity";
            } else if (Float.isNaN(vals[i])) {
                return "NaN";
            } else if (vals[i] != 0) {
                builder.append(builder.length() == 0 ? (vals[i] < 0 ? "-" : "") : (vals[i] < 0 ? " - " : " + "));
                builder.append(Mathf.abs(vals[i])).append(symbols[i]);
            }
        }

        return builder.toString();
    }
}
