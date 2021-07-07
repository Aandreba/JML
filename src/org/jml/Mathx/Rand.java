package org.jml.Mathx;

import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.Complex.Single.Quat;
import org.jml.Mathx.Extra.Intx;
import org.jml.Mathx.Extra.Longx;
import org.jml.Matrix.Double.Matd;
import org.jml.Matrix.Double.Matid;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.Mati;
import org.jml.Vector.Double.Vecd;
import org.jml.Vector.Double.Vecid;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.Veci;

import java.util.List;
import java.util.Random;

final public class Rand {
    final private static Random random = new Random();
    private static boolean haveNextNextGaussian = false;
    private static float nextNextGaussian = 0;

    public static boolean getBool() {
        return random.nextBoolean();
    }

    public static boolean getBool(float pro) {
        return random.nextFloat() <= pro;
    }

    public static byte[] getBytes (int bytes) {
        byte[] result = new byte[bytes];
        random.nextBytes(result);

        return result;
    }

    public static byte getByte () {
        return getBytes(1)[0];
    }

    public static short getShort () {
        byte[] bytes = getBytes(2);
        return (short) ((bytes[1] << 8) | bytes[0]);
    }

    public static short getShort (short from, short to) {
        return (short) getInt(from, to);
    }

    public static int getInt () {
        return random.nextInt();
    }

    public static int getInt (int from, int to) {
        return (int) getFloat(from, to+1);
    }

    public static long getLong () {
        return random.nextLong();
    }

    public static long getLong (long from, long to) {
        return (long) getDouble(from, to+1);
    }

    public static float getFloat () {
        return random.nextFloat();
    }

    public static float getFloat (float from, float to) {
        return (to - from) * random.nextFloat() + from;
    }

    public static double getDouble () {
        return random.nextDouble();
    }

    public static double getDouble (double from, double to) {
        return (to - from) * random.nextDouble() + from;
    }

    public static float getFullFloat () {
        float val;
        do {
            val = Float.intBitsToFloat(random.nextInt());
        } while (Float.isNaN(val) | Float.isInfinite(val));

        return val;
    }

    public static double getFullDouble () {
        double val;
        do {
            val = Double.longBitsToDouble(random.nextLong());
        } while (Double.isNaN(val) | Double.isInfinite(val));

        return val;
    }

    public static Compd getCompd() {
        return Compd.fromPolar(random.nextDouble(), getDouble(0, Mathd.PI2));
    }

    public static Compd getCompd(Compd from, Compd to) {
        return to.subtr(from).mul(getCompd()).add(from);
    }

    public static Comp getComp() {
        return Comp.fromPolar(random.nextFloat(), getFloat(0, Mathf.PI2));
    }

    public static Comp getComp(Comp from, Comp to) {
        return to.subtr(from).mul(getComp()).add(from);
    }

    public static Quat getQuat () {
        return new Quat(getFloat(0, Mathf.PI2), getFloat(0, Mathf.PI2), getFloat(0, Mathf.PI2));
    }

    public static double getGaussian () {
        return random.nextGaussian();
    }

    public static double getGaussian (double mean, double std) {
        return std * random.nextGaussian() + mean;
    }

    public static float getFloatGaussian () {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false;
            return nextNextGaussian;
        } else {
            float v1, v2, s;
            do {
                v1 = 2 * random.nextFloat() - 1; // between -1 and 1
                v2 = 2 * random.nextFloat() - 1; // between -1 and 1
                s = v1 * v1 + v2 * v2;
            } while (s >= 1 || s == 0);
            float multiplier = Mathf.sqrt(-2 * Mathf.log(s)/s);
            nextNextGaussian = v2 * multiplier;
            haveNextNextGaussian = true;
            return v1 * multiplier;
        }
    }

    public static float getFloatGaussian (float mean, float std) {
        return std * getFloatGaussian() + mean;
    }

    public static Vecd getVecd (int size) {
        Vecd vector = new Vecd(size);
        for (int i=0;i<size;i++) {
            vector.set(i, random.nextDouble());
        }

        return vector;
    }

    public static Vecd getVecd (int size, double from, double to) {
        Vecd vector = new Vecd(size);
        for (int i=0;i<size;i++) {
            vector.set(i, getDouble(from, to));
        }

        return vector;
    }

    public static Vec getVec (int size) {
        Vec vector = new Vec(size);
        for (int i=0;i<size;i++) {
            vector.set(i, random.nextFloat());
        }

        return vector;
    }

    public static Vec getVec (int size, float from, float to) {
        Vec vector = new Vec(size);
        for (int i=0;i<size;i++) {
            vector.set(i, getFloat(from, to));
        }

        return vector;
    }

    public static Vecid getVecid(int size) {
        Vecid vector = new Vecid(size);
        for (int i=0;i<size;i++) {
            vector.set(i, getCompd());
        }

        return vector;
    }

    public static Vecid getVecid(int size, Compd from, Compd to) {
        Vecid vector = new Vecid(size);
        for (int i=0;i<size;i++) {
            vector.set(i, getCompd(from, to));
        }

        return vector;
    }

    public static Veci getVeci (int size) {
        Veci vector = new Veci(size);
        for (int i=0;i<size;i++) {
            vector.set(i, getComp());
        }

        return vector;
    }

    public static Veci getVeci (int size, Comp from, Comp to) {
        Veci vector = new Veci(size);
        for (int i=0;i<size;i++) {
            vector.set(i, getComp(from, to));
        }

        return vector;
    }

    public static Matd getMatd(int rows, int cols) {
        return Matd.foreach(rows, cols, (i, j) -> Rand.getDouble());
    }

    public static Matd getMatd(int rows, int cols, double from, double to) {
        return Matd.foreach(rows, cols, (i, j) -> Rand.getDouble(from, to));
    }

    public static Mat getMat(int rows, int cols) {
        return Mat.foreach(rows, cols, (i, j) -> Rand.getFloat());
    }

    public static Mat getMat(int rows, int cols, float from, float to) {
        return Mat.foreach(rows, cols, (i, j) -> Rand.getFloat(from, to));
    }

    public static Mati getMati (int rows, int cols) {
        return Mati.foreach(rows, cols, (i, j) -> Rand.getComp());
    }

    public static Mati getMati (int rows, int cols, Comp from, Comp to) {
        return Mati.foreach(rows, cols, (i, j) -> Rand.getComp(from, to));
    }

    public static Matid getMatid (int rows, int cols) {
        return Matid.foreach(rows, cols, (i, j) -> Rand.getCompd());
    }

    public static Matid getMatid (int rows, int cols, Compd from, Compd to) {
        return Matid.foreach(rows, cols, (i, j) -> Rand.getCompd(from, to));
    }

    public static int choiceIndex(Vec weights) {
        weights = weights.div(weights.sum());

        float x = Rand.getFloat();
        float y = 0;

        for (int i = 0; i<weights.size(); i++) {
            y += weights.get(i);
            if (y >= x) {
                return i;
            }
        }

        return -1;
    }

    public static int choiceIndex (float... weights) {
        return choiceIndex(new Vec(weights));
    }

    public static <T> T choice (T[] values, Vec weights) {
        return values[choiceIndex(weights)];
    }

    public static <T> T choice (T[] values, float... weights) {
        return values[choiceIndex(weights)];
    }

    public static <T> T choice (List<T> values, Vec weights) {
        return values.get(choiceIndex(weights));
    }

    public static <T> T choice (List<T> values, float... weights) {
        return values.get(choiceIndex(weights));
    }
}
