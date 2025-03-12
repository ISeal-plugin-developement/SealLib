package dev.iseal.sealLib.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DistributedRandomNumberGenerator {

    private final Map<Integer, Double> distribution;
    private final Random random;
    private double distSum;

    public DistributedRandomNumberGenerator() {
        distribution = new HashMap<>();
        random = new Random();
    }

    public DistributedRandomNumberGenerator(long seed) {
        distribution = new HashMap<>();
        random = new Random(seed);
    }

    public void addNumber(int value, double distribution) {
        if (this.distribution.get(value) != null) {
            distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        distSum += distribution;
    }

    public int getDistributedRandomNumber() {
        double rand = random.nextDouble(0, 1);
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return 0;
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }

}