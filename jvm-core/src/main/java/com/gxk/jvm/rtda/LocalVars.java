package com.gxk.jvm.rtda;

import com.gxk.jvm.rtda.heap.Instance;

/**
 * 局部变量表
 * 每个栈帧内部都包含一组称为局部变量表的变量列表。
 * 栈帧中局部变量表的长度由编译期决定，并且存储于类或接口的二进制表示之中，即通过方法的code属性保存及提供给栈帧使用。
 * 保存的都是基本数据类型(long和double占两位)
 * <p>
 * 局部变量使用索引来进行定位访问。首个局部变量的索引值为0。局部变量的索引值是个整数，它大于等于0，且小于局部变量表的长度。
 * <p>
 * Java 虚拟机使用局部变量表来完成方法调用时的参数传递。当调用类方法时，它的参数将会依次传递到局部变量表中从0开始的连续位置上。
 * 当调用实例方法时，第0个局部变量一定用来存储该实例方法所在对象的引用（即 Java 语言中的this关键宇)。
 * 后续的其他参数将会传递至局部变量表中从1开始的连续位置上。
 */
public class LocalVars {

    private final Slot[] slots;

    public LocalVars(int size) {
        this.slots = new Slot[size];
    }

    public LocalVars(Slot[] slots) {
        this.slots = slots;
    }

    public Slot[] getSlots() {
        return this.slots;
    }

    public void setInt(int index, int val) {
        slots[index] = new Slot(val);
    }

    public int getInt(int index) {
        return slots[index].num;
    }

    public void setFloat(int index, float val) {
        int tmp = Float.floatToIntBits(val);
        slots[index] = new Slot(tmp);
    }

    public float getFloat(int index) {
        int num = slots[index].num;
        return Float.intBitsToFloat(num);
    }

    public long getLong(int index) {
        int high = slots[index].num;
        int low = slots[index + 1].num;

        long l1 = (high & 0x000000ffffffffL) << 32;
        long l2 = low & 0x00000000ffffffffL;
        return l1 | l2;
    }

    public void setLong(int index, long val) {
        int high = (int) (val >> 32); //高32位
        int low = (int) (val & 0x000000ffffffffL); //低32位

        slots[index] = new Slot(high);
        slots[index + 1] = new Slot(low);
    }

    public void setDouble(int index, double val) {
        long tmp = Double.doubleToLongBits(val);

        int high = (int) (tmp >> 32); //高32位
        int low = (int) (tmp & 0x000000ffffffffL); //低32位

        slots[index] = new Slot(high);
        slots[index + 1] = new Slot(low);
    }

    public double getDouble(int index) {
        long tmp = this.getLong(index);
        return Double.longBitsToDouble(tmp);
    }

    public void setRef(int index, Instance ref) {
        slots[index] = new Slot(ref);
    }

    public Instance getRef(int index) {
        return slots[index].ref;
    }

    public String debug(String space) {
        StringBuilder sb = new StringBuilder();
        sb.append(space).append(String.format("LocalVars: %d", this.slots.length)).append("\n");
        for (int i = 0; i < this.slots.length; i++) {
            Slot slot = this.slots[i];
            if (slot == null) {
                sb.append(space).append(String.format("%d | null | null", i)).append("\n");
                continue;
            }
            if (slot.ref != null) {
                sb.append(space).append(String.format("%d | ref       | %s", i, slot.ref)).append("\n");
                continue;
            }
            sb.append(space).append(String.format("%d | primitive | %s", i, slot.num)).append("\n");
        }
        return sb.append("\n").toString();
    }

    public void set(int i, Slot val) {
        this.slots[i] = val;
    }
}
