package com.gxk.jvm.instruction.constants;

import com.gxk.jvm.instruction.Instruction;
import com.gxk.jvm.rtda.Frame;

/**
 * 操作	将一个byte类型数据入栈<br>
 * 格式	bipush byte<br>
 * 描述	将立即数byte带符号扩展为一个int类型的值value,然后将value入栈到操作数栈中<br>
 */
public class BiPushInst implements Instruction {

    public final byte val;

    public BiPushInst(byte val) {
        this.val = val;
    }

    @Override
    public int offset() {
        return 2;
    }

    @Override
    public void execute(Frame frame) {
        frame.pushInt(this.val);
    }

    @Override
    public String format() {
        return "bipush " + val;
    }
}
