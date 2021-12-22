package com.gxk.jvm.instruction.constants;

import com.gxk.jvm.instruction.Instruction;
import com.gxk.jvm.rtda.Frame;

/**
 * 操作	将一个short类型数据入栈<br>
 * 格式	sipush bytel byte2<br>
 * 描述	无符号数byte1和byte2通过(byte1<<8)方式构造成一个short类型数值，
 * 然后此数值带符号扩展为一个int类型的值value,再将value入栈到操作数栈中
 */
public class SiPushInst implements Instruction {

    public final short val;

    public SiPushInst(short val) {
        this.val = val;
    }

    @Override
    public int offset() {
        return 3;
    }

    @Override
    public void execute(Frame frame) {
        frame.pushInt(this.val);
    }

    @Override
    public String format() {
        return "sipush " + val;
    }
}
