package com.gxk.jvm.instruction.constants;

import com.gxk.jvm.instruction.Instruction;


import com.gxk.jvm.interpret.Interpreter;
import com.gxk.jvm.rtda.Frame;
import com.gxk.jvm.rtda.UnionSlot;
import com.gxk.jvm.rtda.heap.Heap;
import com.gxk.jvm.rtda.heap.Class;
import com.gxk.jvm.rtda.heap.Field;
import com.gxk.jvm.rtda.heap.Instance;
import com.gxk.jvm.rtda.heap.PrimitiveArray;

/**
 * index 是一个无符号byte类型数据，用作当前类的运行时常量池的素引。
 * index 指向的运行时常量池项必须是一个 int 或者float 类型的运行时常量、宇符串字面量，或者一个指向类、方法类型或方法句柄的符号引用
 * 如果运行时常量池成员是一个int或者float类型的运行时常量，那么这个常量所对应的数值 value 将人栈到操作数栈中
 * 否则，如果运行时常量池成员是一个代表字符串字面量的String类的引用，那么这个实例的引用所对应的reference 类型数据 value将人栈到操作数栈中
 * <p>
 * 否则，如果运行时常量池成员是一个指向类的符号引用，那么就解析这个符号引用，
 * 并把指向这个类的 Class对象的reference类型数据 valve 人栈到操作数栈中
 * <p>
 * 否则，运行时常量池成员必定是指向方法类型或方法句柄（见 5.1节）的一个符号引用。解析方法类型或方法句柄，
 * 并且把指向解析好的 java.lang.invoke.MethodType 或 java.lang.invoke.MethodHandle
 * 实例的引用所对应的reference类型数据 value 人栈到操作数栈中
 * <p>
 * 注意: lde指令只能用来处理单精度浮点集合中的float类型数据，因为常量池中 float 类型的常量必须从单精度浮点集合中选取
 */
public class LdcInst implements Instruction {
    public final String descriptor;
    public final Object val;

    @Override
    public int offset() {
        return 2;
    }

    public LdcInst(String descriptor, Object val) {
        this.descriptor = descriptor;
        this.val = val;
    }

    @Override
    public void execute(Frame frame) {
        switch (descriptor) {
            case "I":
                frame.pushInt(((Integer) val));
                break;
            case "F":
                frame.pushFloat(((float) val));
                break;
            case "Ljava/lang/String":
                Class klass = Heap.findClass("java/lang/String");
                if (klass == null) {
                    klass = frame.method.clazz.classLoader.loadClass("java/lang/String");
                }
                if (!klass.getStat()) {
                    klass.setStat(1);
                    Interpreter.execute(klass.getMethod("<clinit>", "()V"));
                    klass.setStat(2);
                }
                Instance object = klass.newInstance();
                Field field = object.getField("value", "[C");
                char[] chars = val.toString().toCharArray();

                final PrimitiveArray array = PrimitiveArray.charArray(chars.length);
                for (int i = 0; i < array.len; i++) {
                    array.ints[i] = chars[i];
                }

                field.val = UnionSlot.of(array);
                frame.pushRef(object);
                break;
            case "L":
                Class klass2 = Heap.findClass(val.toString());
                if (klass2 == null) {
                    klass2 = frame.method.clazz.classLoader.loadClass(val.toString());
                }
                frame.pushRef(klass2.getRuntimeClass());
                break;
            default:
                frame.pushRef((Instance) val);
                break;
        }
    }

    @Override
    public String format() {
        return "ldc " + descriptor + " " + val;
    }

    @Override
    public String toString() {
        return "LdcInst{" +
                "descriptor='" + descriptor + '\'' +
                ", val=" + val +
                '}';
    }
}
