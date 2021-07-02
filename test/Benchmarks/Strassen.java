package Benchmarks;

import org.jml.Mathx.Rand;
import org.jml.Mathx.TaskManager;
import org.jml.Matrix.Double.Matd;
import org.jml.Matrix.Single.Mat;
import org.jml.Vector.Single.Vec;

import java.util.Arrays;

public class Strassen {
    public static void main (String... args) {
        System.out.println();
        int epochs = 1000;

        for (int i=20;i<=300;i+=20) {
            Mat[] A = new Mat[epochs];
            Mat[] B = new Mat[epochs];

            for (int j=0;j<epochs;j++) {
                A[j] = Rand.getMat(i, i, -100, 100);
                B[j] = Rand.getMat(i, i, -100, 100);
            }

            long time = Benchmark.time(epochs, k -> strassen(A[k], B[k]));
            System.out.println(i+": "+(time * 0.000001f)+" ms");
        }
    }

    public static Mat mul (Mat a, Mat b) {
        int rows = a.rows();
        int cols = b.cols();
        int dig = Math.min(a.cols(), b.rows());

        return Mat.foreach(rows, cols, (i, j) -> {
            float sum = 0;
            for (int k=0;k<dig;k++) {
                sum += a.get(i, k) * b.get(k, j);
            }

            return sum;
        });
    }

    public static Mat mulMT (Mat a, Mat b) {
        int rows = a.rows();
        int cols = b.cols();
        int dig = Math.min(a.cols(), b.rows());

        TaskManager tasks = new TaskManager();
        Mat result = new Mat(rows, cols);

        for (int i=0;i<rows;i++) {
            int finalI = i;
            for (int j=0;j<cols;j++) {
                int finalJ = j;
                tasks.add(() -> {
                    float sum = 0;
                    for (int k=0;k<dig;k++) {
                        sum += a.get(finalI, k) * b.get(k, finalJ);
                    }

                    result.set(finalI, finalJ, sum);
                });
            }
        }

        tasks.run();
        return result;
    }

    public static Mat strassenFast (Mat a, Mat b) {
        float a11 = a.get(0, 0);
        float a12 = a.get(0, 1);
        float a21 = a.get(1,0);
        float a22 = a.get(1,1);

        float b11 = b.get(0, 0);
        float b12 = b.get(0, 1);
        float b21 = b.get(1,0);
        float b22 = b.get(1,1);

        float p1 = (a11 + a22) * (b11 + b22);
        float p2 = (a21 + a22) * b11;
        float p3 = a11 * (b12 - b22);
        float p4 = a22 * (b21 - b11);
        float p5 = (a11 + a12) * b22;
        float p6 = (a21 - a11) * (b11 + b12);
        float p7 = (a12 - a22) * (b21 + b22);

        return new Mat(new float[][]{
                {p1 + p4 - p5 + p7, p3 + p5},
                {p2 + p4, p1 - p2 + p3 + p6}
        });
    }

    public static Mat strassen (Mat a, Mat b) {
        int rows = a.rows();
        int cols = b.cols();
        int k = a.cols();

        if (rows == 1 && cols == 1 && k == 1) {
            return new Mat(new float[][]{ { a.get(0,0) * b.get(0,0) } });
        } else if (rows == 2 && cols == 2 && k == 2) {
            return strassenFast(a, b);
        }

        int rowsDiv = rows / 2;
        int colsDiv = cols / 2;
        int kDiv = k / 2;

        Mat[][] A = new Mat[2][2];
        Mat[][] B = new Mat[2][2];
        Mat[][] C = new Mat[2][2];

        // A
        A[0][0] = new Mat(rowsDiv, kDiv);
        A[0][1] = new Mat(rowsDiv, kDiv);
        A[1][0] = new Mat(rowsDiv, kDiv);
        A[1][1] = new Mat(rowsDiv, kDiv);

        for (int j=0;j<rowsDiv;j++) {
            int y = j + rowsDiv;

            A[0][0].set(j, 0, 0, kDiv, a.get(j));
            A[0][1].set(j, kDiv, 0, kDiv, a.get(j));

            A[1][0].set(j, 0, 0, kDiv, a.get(y));
            A[1][1].set(j, kDiv, 0, kDiv, a.get(y));
        }

        // B
        B[0][0] = new Mat(kDiv, colsDiv);
        B[0][1] = new Mat(kDiv, colsDiv);
        B[1][0] = new Mat(kDiv, colsDiv);
        B[1][1] = new Mat(kDiv, colsDiv);

        for (int j=0;j<kDiv;j++) {
            int y = j + kDiv;

            B[0][0].set(j, 0, 0, colsDiv, b.get(j));
            B[0][1].set(j, colsDiv, 0, colsDiv, b.get(j));

            B[1][0].set(j, 0, 0, colsDiv, b.get(y));
            B[1][1].set(j, colsDiv, 0, colsDiv, b.get(y));
        }

        // C
        C[0][0] = strassen(A[0][0], B[0][0]).add(strassen(A[0][1], B[1][0]));
        C[0][1] = strassen(A[0][0], B[0][1]).add(strassen(A[0][1], B[1][1]));

        C[1][0] = strassen(A[1][0], B[0][0]).add(strassen(A[1][1], B[1][0]));
        C[1][1] = strassen(A[1][0], B[0][1]).add(strassen(A[1][1], B[1][1]));

        // N
        Mat K = a.mul(b);
        Mat N = new Mat(rows, cols);

        for (int j=0;j<rowsDiv;j++) {
            int y = rowsDiv + j;

            Vec n1 = new Vec(cols);
            Vec n2 = new Vec(cols);

            n1.set(0, 0, C[0][0].get(j));
            n1.set(0, colsDiv, C[0][1].get(j));

            n2.set(0, 0, C[1][0].get(j));
            n2.set(0, colsDiv, C[1][1].get(j));

            N.set(j, n1);
            N.set(y, n2);
        }

        return N;
    }
}
