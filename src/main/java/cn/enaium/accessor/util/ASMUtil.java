package cn.enaium.accessor.util;

import org.objectweb.asm.Opcodes;

/**
 * @author Enaium
 */
public class ASMUtil {
    public static int getLoadOpcode(String descriptor) {
        switch (descriptor.replace("[", "")) {
            case "Z":
            case "C":
            case "B":
            case "S":
            case "I":
                return Opcodes.ILOAD;
            case "J":
                return Opcodes.LLOAD;
            case "D":
                return Opcodes.DLOAD;
            case "F":
                return Opcodes.FLOAD;
            default:
                return Opcodes.ALOAD;
        }
    }

    public static int getReturnOpcode(String descriptor) {
        switch (descriptor.replace("[", "")) {
            case "Z":
            case "C":
            case "B":
            case "S":
            case "I":
                return Opcodes.IRETURN;
            case "J":
                return Opcodes.LRETURN;
            case "D":
                return Opcodes.DRETURN;
            case "F":
                return Opcodes.FRETURN;
            default:
                return Opcodes.ARETURN;
        }
    }
}
