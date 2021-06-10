package cn.enaium.accessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * @author Enaium
 */
public class AccessorClassLoader extends ClassLoader {
    public final HashMap<String, byte[]> list = new HashMap<>();

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (list.containsKey(name)) {
            byte[] bytes = list.get(name);
            return defineClass(name, bytes, 0, bytes.length);
        } else {
            return super.loadClass(name);
        }
    }
}
