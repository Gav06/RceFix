package me.gavin.rcefix;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ASMTransformer implements IClassTransformer {

    private static final HashMap<Integer, String> opcodes = new HashMap<>();

    static {
        for (Field f : Opcodes.class.getDeclaredFields()) {
            if (!f.isAccessible())
                f.setAccessible(true);

            if (f.getType() == int.class) {
                try {
                    opcodes.put((int) f.get(null), f.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isObfuscated = false;

    private boolean matches(MethodNode methodNode) {
        if (isObfuscated) {
            return methodNode.name.equals("a") && methodNode.desc.equals("(Lhh;I)V");
        } else {
            return methodNode.name.equals("printChatMessageWithOptionalDeletion") && methodNode.desc.equals("(Lnet/minecraft/util/text/ITextComponent;I)V");
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.gui.GuiNewChat")) {
            final ClassNode node = new ClassNode();
            final ClassReader reader = new ClassReader(basicClass);
            reader.accept(node, ClassReader.EXPAND_FRAMES);

            for (MethodNode methodNode : node.methods) {
                if (matches(methodNode)) {
                    // we are going to be removing a bunch of instructions

                    /**
                     * METHOD CODE BEFORE:
                     * this.setChatLine(...)
                     * LOGGER.info(...)
                     *
                     * METHOD CODE AFTER:
                     * this.setChatLine(...)
                     */

                    // we want to remove instructions from index 11 - 24

                    for (int i = 0; i < 13; i++) {
                        methodNode.instructions.remove(methodNode.instructions.get(11));
                    }
                }
            }

            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            return writer.toByteArray();
        } else {
            return basicClass;
        }
    }
}
