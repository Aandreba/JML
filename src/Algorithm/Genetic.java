package Algorithm;

import Mathx.Rand;
import Matrix.Single.Mat;
import References.Single.Ref1D;
import References.Single.Ref2D;
import Vector.Single.Vec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public abstract class Genetic {
    final public Ref2D genomes;

    public Genetic(Ref2D genomes) {
        this.genomes = genomes;
    }

    public abstract float fitness (Vec genome);

    public ArrayList<Ref1D> sorted (boolean reversed) {
        ArrayList<Ref1D> genomes = new ArrayList<>();
        for (int i=0;i<this.genomes.getRows();i++) {
            genomes.add(this.genomes.get(i));
        }

        Collections.sort(genomes, (x,y) -> Float.compare(fitness(Vec.fromRef(x)), fitness(Vec.fromRef(y))));
        if (reversed) {
            Collections.reverse(genomes);
        }

        return genomes;
    }

    public void simulate (float crossover, float from, float to) {
        Vec[] genomes = new Vec[this.genomes.getRows()];
        Vec fitness = new Vec(genomes.length);

        for (int i=0;i<genomes.length;i++) {
            genomes[i] = new Vec(new float[0], this.genomes.get(i));
            fitness.set(i, fitness(genomes[i]));
        }

        for (int i=0;i<genomes.length;i++) {
            Vec parent1 = Rand.choice(genomes, fitness);
            Vec parent2 = Rand.choice(genomes, fitness);
            Vec child = new Vec(parent1.getSize());

            for (int j=0;j<child.getSize();j++) {
                if (Rand.getBool(crossover)) {
                    child.set(j, Rand.getFloat(from, to));
                } else if (Rand.getBool()) {
                    child.set(j, parent1.get(j));
                } else {
                    child.set(j, parent2.get(j));
                }
            }

            this.genomes.set(i, child);
        }
    }
}
