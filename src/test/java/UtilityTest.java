import me.nort3x.atomic.core.internal.GreedyBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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


        Map<Integer, Set<String>> dualOfMap1 = GreedyBag.invert(map1);

        System.out.println(map1);
        System.out.println(dualOfMap1);

    }

    @Test
    void shouldNotStackOverFlow() {

        Set<String> h = new HashSet<>();
        h.add("123");
        Assertions.assertTrue(h.contains("123"));

    }
}
