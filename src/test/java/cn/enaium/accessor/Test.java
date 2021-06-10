package cn.enaium.accessor;

import java.util.Arrays;

/**
 * @author Enaium
 */
public class Test {
    private String name = "Enaium";

    private final String id = "EEE";

    private void render(String arg) {
        System.out.println(arg);
    }

    private void render(String[] strings) {
        System.out.println(strings[0]);
    }
}
