package featuretest;

import java.util.Comparator;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class RandomTest {
    // pkg java.util.random java.util

    public static void main(String[] args) {
        RandomGeneratorFactory<RandomGenerator> factory = RandomGeneratorFactory.of("Random");

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                RandomGenerator random = factory.create(100L);
                System.out.println(random.getClass().toString());
                System.out.println(random.nextDouble());
            }).start();
        }


//        RandomGeneratorFactory<RandomGenerator> best = RandomGeneratorFactory.all()
//                .sorted(Comparator.comparingInt(RandomGenerator::stateBits).reversed())
//                .findFirst()
//                .orElse(RandomGeneratorFactory.of("Random"));
//        System.out.println(best.name() + " in " + best.group() + " was selected");
//
//        RandomGenerator rng = best.create();
//        System.out.println(rng.nextLong());
    }
}
