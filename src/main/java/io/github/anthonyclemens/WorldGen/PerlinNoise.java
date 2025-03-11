package io.github.anthonyclemens.WorldGen;

import java.util.Random;

public class PerlinNoise {
    private static final int PERMUTATION_SIZE = 256; // Size of the permutation array
    private final int[] permutation = new int[PERMUTATION_SIZE * 2]; // Double the size to avoid wrapping

    public PerlinNoise(int seed) {
        Random random = new Random(seed);

        // Fill the permutation array with values 0-255 in random order
        int[] p = new int[PERMUTATION_SIZE];
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            p[i] = i;
        }

        // Shuffle the array based on the seed
        for (int i = PERMUTATION_SIZE - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }

        // Duplicate the permutation array
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            permutation[i] = p[i];
            permutation[PERMUTATION_SIZE + i] = p[i];
        }
    }

    public double generate(double x, double y) {
        int x0 = (int) Math.floor(x) & 255;
        int y0 = (int) Math.floor(y) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int aa = permutation[permutation[x0] + y0];
        int ab = permutation[permutation[x0] + y0 + 1];
        int ba = permutation[permutation[x0 + 1] + y0];
        int bb = permutation[permutation[x0 + 1] + y0 + 1];

        return lerp(v,
                lerp(u, grad(aa, x, y), grad(ba, x - 1, y)),
                lerp(u, grad(ab, x, y - 1), grad(bb, x - 1, y - 1))
        );
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}
