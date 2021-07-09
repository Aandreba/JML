import Benchmarks.Benchmark;
import org.jml.Mathx.Rand;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.MatCL;
import org.jml.Vector.Single.Vec;
import org.jml.Vector.Single.VecCL;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Arrays;

public class Main {
    public static void main(String... args) throws IOException, ClassNotFoundException {
        File file = new File("test.mat");
        Mat a = Rand.getMat(2, 2);

        ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
        oos.writeObject(a);
        oos.flush();
        oos.close();


        ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file.toPath()));
        Mat b = (Mat) ois.readObject();
        ois.close();

        System.out.println(a);
        System.out.println();
        System.out.println(b);
    }
}