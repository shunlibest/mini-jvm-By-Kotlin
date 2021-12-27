package com.gxk.jvm.rtda;

import com.gxk.jvm.rtda.heap.Instance;

/**
 * 变量槽: 是局部变量表中最基本的存储单元
 * 每一个slot都是4个字节(Byte)大小
 * <p>
 * int、float和reference类型的值在数组中占据1个slot，即4个字节。
 * double和long的值在数组中占据2个连续的slot，即总共8个字节。
 * Byte、short和char的值在存储前会被转换为int类型，占据1个slot，即4个字节。
 */
public class Slot {
    public Integer num;
    public Instance ref;

    public Slot(int num) {
        this.num = num;
        this.ref = null;
    }

    public Slot(Instance ref) {
        this.num = null;
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "Slot{" + "num=" + num +
                ", ref=" + ref +
                '}';
    }
}
