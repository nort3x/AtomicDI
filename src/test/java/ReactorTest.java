import me.nort3x.atomic.reactor.AbstractSaverReactor;
import me.nort3x.atomic.reactor.LinearReactor;
import me.nort3x.atomic.reactor.ParallelReactor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReactorTest {

    @Test
    void shouldReactOnString() {

        LinearReactor<String> reactor1 = new LinearReactor<>();

        reactor1.addReaction(x -> { // lambda style
            if (x.contains("i"))
                System.out.println(x + " Contains i!");
        });

        reactor1.addReaction(x -> { // lambda style
            if (x.contains("ts"))
                System.out.println(x + " Contains ts!");
        });
        reactor1.addReaction(x -> { // lambda style
            if (x.contains("I"))
                System.out.println(x + " Contains I!");
        });


        // reactor is ready now lets test it, its just a visual test!


        reactor1.reactOn("ILovei"); // its linear! meaning first added reaction has first priority


    }

    // now lets get serious and test it in real way!

    static class Vector3 {
        int i;
        int j;
        int k;

        public Vector3(int i, int j, int k) {
            this.i = i;
            this.j = j;
            this.k = k;
        }
    }


    @Test
    void shouldAssembleSomethingForMe() {
        LinearReactor<Vector3> reactor1 = new LinearReactor<>();

        reactor1.addReaction(x -> { // lambda style
            x.i = 1;
        });

        reactor1.addReaction(x -> { // lambda style
            x.j = 2;
        });
        reactor1.addReaction(x -> { // lambda style
            x.k = 3;
        });


        // vector of zeros will be something as above:
        Vector3 reactedVec = reactor1.reactOn(new Vector3(0, 0, 0));

        Assertions.assertEquals(reactedVec.i, 1);
        Assertions.assertEquals(reactedVec.j, 2);
        Assertions.assertEquals(reactedVec.k, 3);


    }


    final AtomicBoolean linearReactorActedWired = new AtomicBoolean(false);

    @DisplayName("will test difference in linear and parallel reactors , might fail from linear behavior of parallel reactor")
    @Test
    void shouldAssembleSomethingForMeLinearVsParallel() {
        LinearReactor<Vector3> reactor1 = new LinearReactor<>();

        reactor1.addReaction(x -> { // lambda style
            if (x.j != 0 && x.k != 0) {
                System.out.println("wow who set them before me?"); // no
                linearReactorActedWired.set(true); // even if once triggered the value will be true
            }
            x.i = 1;
        });

        reactor1.addReaction(x -> { // lambda style
            x.j = 2;
        });
        reactor1.addReaction(x -> { // lambda style
            x.k = 3;
        });

        // lets make array of vectors for test!
        List<Vector3> myZeroVectors = IntStream.range(0, 1000) // count to 1000
                .mapToObj(x -> new Vector3(0, 0, 0)) // for each number make a new vector
                .collect(Collectors.toList()); // add all to list


        // all at once
        myZeroVectors.parallelStream()
                .forEach(reactor1::reactOn); // but reading console is not effective is it?!

        Assertions.assertFalse(linearReactorActedWired.get()); // good not triggered!
        // now Parallel


        ParallelReactor<Vector3> reactor2 = new ParallelReactor<>();
        AtomicBoolean parallelReactorIsBeingParallel = new AtomicBoolean(false);

        reactor2.addReaction(x -> { // lambda style
            if (x.j != 0 && x.k != 0) {
                System.out.println("wow who set them before me?"); // no
                parallelReactorIsBeingParallel.set(true); // even if once triggered the value will be true
            }
            x.i = 1;
        });

        reactor2.addReaction(x -> { // lambda style
            x.j = 2;
        });
        reactor2.addReaction(x -> { // lambda style
            x.k = 3;
        });

        // lets make array of vectors for test!
        List<Vector3> myZeroVectors2 = IntStream.range(0, 1000) // count to 1000
                .mapToObj(x -> new Vector3(0, 0, 0)) // for each number make a new vector
                .collect(Collectors.toList()); // add all to list


        // all at once
        myZeroVectors2.parallelStream()
                .forEach(reactor2::reactOn); // but reading console is not effective is it?!

        Assertions.assertTrue(parallelReactorIsBeingParallel.get()); // its not generally true but... so it did Work!!!!

    }


    @Test
    @DisplayName(" will not throw error while adding reaction concurrently to reactor, pray for not crashing :)")
    void shouldNotThrowExceptionReactionCreation() throws InterruptedException {

        AbstractSaverReactor<String> badReactor = new LinearReactor<>(); // or Parallel Reactor no one gives a shit in this test


        int totalMissed = 0;


        List<Thread> threads = new ArrayList<>();

        long nanos_1 = System.nanoTime();
        for (int j = 0; j < 100; j++) {

            // cleanup
            badReactor.getSetOfReactions().clear();
            threads.clear();

            // this will just add threads;
            for (int i = 0; i < 600; i++) { // lets first be `linear` its just 300 reactions adding to reactor one-by-one nothing!
                threads.add(new Thread(() -> {
                    // job of thread
                    badReactor.addReaction(s -> {
                        // nothing just adding!
                    });
                }));
            }
            // start all
            for (Thread thread : threads) {
                thread.start();
            }
            // wait for allofthem
            for (Thread thread : threads) {
                thread.join();
            }

            // now check number of reactions:
            totalMissed += 600 - badReactor.getSetOfReactions().size(); // good but remember we need to clean reactions each time! (its like running this test multiple times)
        }


        // sometimes i'm just stupid :) 11 sec without synchronized
        System.out.println(TimeUnit.MILLISECONDS.convert(System.nanoTime() - nanos_1, TimeUnit.NANOSECONDS)); // you should run this test multiple times and mean the value but because we have a bigger for here its almost good enough
        // should you suspect number 2? no you should not! because it doesnt make sense to any other number! and you test again now i'm getting happier!
        Assertions.assertEquals(totalMissed, 0); // so its not concurrent! you cant make reactor in parallel form! lets make it parallel
    }


}
