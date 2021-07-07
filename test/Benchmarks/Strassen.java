package Benchmarks;

import org.jml.Link.Single.Link2D;
import org.jml.Mathx.Rand;
import org.jml.MT.TaskManager;
import org.jml.Matrix.Single.Mat;

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

            long strassen = Benchmark.time(epochs, k -> strassen(A[k], B[k]));
            long mul = Benchmark.time(epochs, k -> mul(A[k], B[k]));

            System.out.println(i);
            System.out.println("Normal: "+(mul * 0.000001f)+" ms");
            System.out.println("Strassen: "+(strassen * 0.000001f)+" ms");
            System.out.println();
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

    public static Link2D strassen (Link2D a, Link2D b) {
        int rows = a.rows;
        int cols = b.cols;
        int k = a.cols;

        if (rows == 1 && cols == 1 && k == 1) {
            return Link2D.init(1, 1, (i,j) -> a.get(0,0) * b.get(0,0));
        }

        int rowsDiv = rows / 2;
        int colsDiv = cols / 2;
        int kDiv = k / 2;

        Link2D A11 = Link2D.init(rowsDiv, kDiv, a::get);
        Link2D A12 = Link2D.init(rowsDiv, kDiv, (i,j) -> a.get(i, j + kDiv));
        Link2D A21 = Link2D.init(rowsDiv, kDiv, (i,j) -> a.get(i + rowsDiv, j));
        Link2D A22 = Link2D.init(rowsDiv, kDiv, (i,j) -> b.get(i + rowsDiv, j + kDiv));

        Link2D B11 = Link2D.init(kDiv, colsDiv, b::get);
        Link2D B12 = Link2D.init(kDiv, colsDiv, (i,j) -> b.get(i, j + colsDiv));
        Link2D B21 = Link2D.init(kDiv, colsDiv, (i,j) -> b.get(i + kDiv, j));
        Link2D B22 = Link2D.init(kDiv, colsDiv, (i,j) -> b.get(i + kDiv, j + colsDiv));

        // P
        Link2D P1 = strassen(A11.add(A22), B11.add(B22));
        Link2D P2 = strassen(A21.add(A22), B11);
        Link2D P3 = strassen(A11, B12.subtr(B22));
        Link2D P4 = strassen(A22, B21.subtr(B11));
        Link2D P5 = strassen(A11.add(A12), B22);
        Link2D P6 = strassen(A21.subtr(A11), B11.add(B12));
        Link2D P7 = strassen(A12.subtr(A22), B21.add(B22));

        // C
        Mat K = a.toMatrix().mul(b.toMatrix());
        Link2D[][] C = new Link2D[2][2];

        C[0][0] = P1.add(P4).subtr(P5).add(P7);
        C[0][1] = P3.add(P5);
        C[1][0] = P2.add(P4);
        C[1][1] = P1.subtr(P2).add(P3).subtr(P6);

        return Link2D.init(rows, cols, (i, j) -> {
            int x, y;

            if (i < rowsDiv) {
                x = 0;
            } else {
                x = 1;
                i -= rowsDiv;
            }

            if (j < colsDiv) {
                y = 0;
            } else {
                y = 1;
                j -= colsDiv;
            }

            return C[x][y].get(i, j);
        });
    }
}
