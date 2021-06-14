import org.jml.Matrix.Double.MatCLd;
import org.jml.Matrix.Double.Matd;
import org.jml.Matrix.Single.Mat;
import org.jml.Matrix.Single.MatCL;
import org.jml.Matrix.Single.Mati;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

public class Diffs {
    public static void main (String... args) {
        System.out.println();
        compareFunctions(Mat.class, Mati.class);
    }

    public static void compareFunctions (Class a, Class b) {
        ArrayList<Method> methodsA = new ArrayList<Method>();
        Collections.addAll(methodsA, a.getMethods());

        ArrayList<Method> methodsB = new ArrayList<Method>();
        Collections.addAll(methodsB, b.getMethods());

        for (Method methodA: methodsA) {
            boolean equivalent = false;
            for (Method methodB: methodsB) {
                if ((equivalent = methodA.getName().equals(methodB.getName()) && methodA.getParameterCount() == methodB.getParameterCount())) {
                    break;
                }
            }

            if (!equivalent) {
                System.out.println("No equivalent to '"+a.getSimpleName()+"."+methodA.getName()+arguments(methodA.getParameterTypes())+"' found on '"+b.getSimpleName()+"'");
            }
        }
    }

    private static String arguments (Class<?>... args) {
        StringBuilder builder = new StringBuilder();
        for (Class arg: args) {
            builder.append(", "+arg.getSimpleName());
        }

        if (builder.isEmpty()) {
            return "()";
        }

        return " ("+builder.substring(2)+")";
    }
}
