import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.nort3x.atomic.utility.Utils.invert;

public class UtilityTest {
    @Test
    void mapInversionTest() {

        ConcurrentHashMap<String, Set<Integer>> map1 = new ConcurrentHashMap<>();
        map1.put("John", new HashSet<Integer>() {{
            add(1);
            add(2);
            add(3);
        }});
        map1.put("Ann", new HashSet<Integer>() {{
            add(2);
            add(3);
            add(4);
        }});
        map1.put("Lucy", new HashSet<Integer>() {{
            add(3);
            add(4);
            add(5);
        }});


        Map<Integer, Set<String>> dualOfMap1 = invert(map1);

        System.out.println(map1);
        System.out.println(dualOfMap1);

    }

    static class FatObj {
        byte[] data = new byte[1024];
    }

    private static Set<Object> alreadyCreatedObject = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<Object, Boolean>()));

    @Test
    void weakHashMapTest() {

        System.gc();
        System.runFinalization();
        System.out.println("MemoryBefore initialization: " + Runtime.getRuntime().freeMemory() / 1000000);

        FatObj aFatObj = new FatObj(); // 300 mb
        alreadyCreatedObject.add(aFatObj);
        System.out.println("MemoryAfter initialization: " + Runtime.getRuntime().freeMemory() / 1000000);
        System.out.println(alreadyCreatedObject.size());

        System.gc();
        System.runFinalization();
        System.out.println("MemoryAfter CallingGC(kinda) and hard-ref: " + Runtime.getRuntime().freeMemory() / 1000000);
        System.out.println(alreadyCreatedObject.size());

        aFatObj = null; // no strong ref anyMore
        System.gc();
        System.runFinalization();
        System.out.println("MemoryAfter CallingGC(kinda) and hard-ref: " + Runtime.getRuntime().freeMemory() / 1000000);
        System.out.println(alreadyCreatedObject.size());
    }


    @Test
    void shouldEncodeToBase64() {


    }


}
