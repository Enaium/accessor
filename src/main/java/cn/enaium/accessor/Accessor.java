package cn.enaium.accessor;

import cn.enaium.accessor.util.ASMUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Enaium
 */
public class Accessor {
    private static final ArrayList<ClassNode> classNodes = new ArrayList<>();
    private static final HashMap<String, String> mapping = new HashMap<>();

    public static void addConfiguration(String name) {
        addConfiguration(Thread.currentThread().getContextClassLoader(), name);
    }

    public static void addConfiguration(Class<?> klass, String name) {
        addConfiguration(klass.getClassLoader(), name);
    }

    public static void addConfiguration(ClassLoader classLoader, String name) {
        try {
            Configuration configuration = new Gson().fromJson(IOUtils.toString(Objects.requireNonNull(classLoader.getResourceAsStream(name))), Configuration.class);
            for (String accessor : configuration.accessors) {
                ClassReader classReader = new ClassReader(IOUtils.toByteArray(Objects.requireNonNull(classLoader.getResourceAsStream(accessor.replace(".", "/") + ".class"))));
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);
                classNodes.add(classNode);
            }
            if (configuration.remapping != null) {
                mapping.putAll(new Gson().fromJson(IOUtils.toString(Objects.requireNonNull(classLoader.getResourceAsStream(configuration.remapping))), new TypeToken<HashMap<String, String>>() {
                }.getType()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Configuration {
        String[] accessors;
        String remapping;
    }

    public static byte[] transform(byte[] basic) {
        ClassReader classReader = new ClassReader(basic);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (ClassNode node : classNodes) {
            String className = null;
            for (AnnotationNode invisibleAnnotation : node.invisibleAnnotations) {
                className = getAnnotationValue(invisibleAnnotation, "value");
            }

            if (className != null) {
                className = mapping.getOrDefault(className, className).replace(".", "/");


                if (className.equals(classNode.name)) {
                    classNode.interfaces.add(node.name);
                } else {
                    continue;
                }


                for (MethodNode method : node.methods) {
                    String fieldName = null;
                    boolean fieldStatic = false;
                    String methodName = null;
                    boolean methodStatic = false;
                    for (AnnotationNode invisibleAnnotation : method.invisibleAnnotations) {
                        if (invisibleAnnotation.desc.equals("Lcn/enaium/accessor/annotaiton/Field;")) {
                            fieldName = getAnnotationValue(invisibleAnnotation, "value");
                            Boolean dynamic = getAnnotationValue(invisibleAnnotation, "dynamic");
                            if (dynamic != null) {
                                fieldStatic = !dynamic;
                            }
                        }

                        if (invisibleAnnotation.desc.equals("Lcn/enaium/accessor/annotaiton/Method;")) {
                            methodName = getAnnotationValue(invisibleAnnotation, "value");
                            Boolean dynamic = getAnnotationValue(invisibleAnnotation, "dynamic");
                            if (dynamic != null) {
                                methodStatic = !dynamic;
                            }
                        }
                    }

                    if (fieldName != null) {
                        fieldName = mapping.getOrDefault(fieldName, fieldName);
                        MethodNode methodNode = new MethodNode(ASM9, ACC_PUBLIC, method.name, method.desc, null, null);
                        if (!fieldStatic) {
                            methodNode.instructions.add(new VarInsnNode(ALOAD, 0));
                        }
                        if (!method.desc.endsWith(")V")) {
                            methodNode.instructions.add(new FieldInsnNode(fieldStatic ? GETSTATIC : GETFIELD, classNode.name, fieldName, method.desc.substring(method.desc.lastIndexOf(")") + 1)));
                            methodNode.instructions.add(new InsnNode(ASMUtil.getReturnOpcode(method.desc.substring(method.desc.lastIndexOf(")") + 1))));
                        } else {
                            methodNode.instructions.add(new VarInsnNode(ASMUtil.getLoadOpcode(methodNode.desc.substring(methodNode.desc.indexOf("(") + 1, methodNode.desc.lastIndexOf(")V"))), 1));
                            methodNode.instructions.add(new FieldInsnNode(fieldStatic ? PUTSTATIC : PUTFIELD, classNode.name, fieldName, method.desc.substring(method.desc.indexOf("(") + 1, method.desc.lastIndexOf(")"))));
                            methodNode.instructions.add(new InsnNode(RETURN));
                        }
                        classNode.methods.add(methodNode);
                    }

                    if (methodName != null) {
                        methodName = mapping.getOrDefault(methodName, methodName);
                        if (method.name.equals(methodName)) {
                            throw new RuntimeException("[" + node.name + ", " + methodName + "]" + "method name repeat!");
                        }

                        MethodNode methodNode = new MethodNode(ASM9, ACC_PUBLIC, method.name, method.desc, null, null);
                        if (!methodStatic) {
                            methodNode.instructions.add(new VarInsnNode(ALOAD, 0));
                        }

                        Type[] argumentTypes = Type.getMethodType(method.desc).getArgumentTypes();
                        for (int i = 0; i < argumentTypes.length; i++) {
                            Type argumentType = argumentTypes[i];
                            methodNode.instructions.add(new VarInsnNode(ASMUtil.getLoadOpcode(argumentType.getDescriptor()), i + 1));
                        }

                        //Invoke method
                        methodNode.instructions.add(new MethodInsnNode(methodStatic ? INVOKESTATIC : INVOKESPECIAL, classNode.name, methodName, method.desc));

                        //End
                        methodNode.instructions.add(new InsnNode(RETURN));
                        classNode.methods.add(methodNode);
                    }
                }
            }
        }

        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getAnnotationValue(AnnotationNode annotationNode, String key) {
        boolean getNextValue = false;

        if (annotationNode.values == null) {
            return null;
        }

        for (Object value : annotationNode.values) {
            if (getNextValue) {
                return (T) value;
            }
            if (value.equals(key)) {
                getNextValue = true;
            }
        }

        return null;
    }
}
