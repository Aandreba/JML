package Mathx;

import Matrix.Mat;
import Vector.Vec;
import Vector.Vecf;

import java.util.Random;

final public class Rand {
    final private static Random random = new Random();

    private static boolean haveNextNextGaussian = false;
    private static float nextNextGaussian = 0;

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
        return Mathf.round(getFloat(from, to));
    }

    public static long getLong () {
        return random.nextLong();
    }

    public static long getLong (long from, long to) {
        return Math.round(getDouble(from, to));
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
        return Float.intBitsToFloat(random.nextInt());
    }

    public static double getFullDouble () {
        return Double.longBitsToDouble(random.nextLong());
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

    public static Vec getVec (int size) {
        Vec vector = new Vec(size);
        for (int i=0;i<size;i++) {
            vector.set(i, random.nextDouble());
        }

        return vector;
    }

    public static Vec getVec (int size, double from, double to) {
        Vec vector = new Vec(size);
        for (int i=0;i<size;i++) {
            vector.set(i, getDouble(from, to));
        }

        return vector;
    }

    public static Vecf getVecf (int size) {
        Vecf vector = new Vecf(size);
        for (int i=0;i<size;i++) {
            vector.set(i, random.nextFloat());
        }

        return vector;
    }

    public static Vecf getVecf (int size, float from, float to) {
        Vecf vector = new Vecf(size);
        for (int i=0;i<size;i++) {
            vector.set(i, getFloat(from, to));
        }

        return vector;
    }
}
