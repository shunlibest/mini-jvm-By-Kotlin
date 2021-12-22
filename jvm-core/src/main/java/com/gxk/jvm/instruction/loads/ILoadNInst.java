package com.gxk.jvm.instruction.loads;

import com.gxk.jvm.instruction.Instruction;
import com.gxk.jvm.rtda.Frame;

/**
 * 操作	从局部变量表加载一个int类型值到操作数栈中<br>
 */
public class ILoadNInst implements Instruction {
    public final int n;

    public ILoadNInst(int n) {
        this.n = n;
    }

    @Override
    public int offset() {
        return 2;
    }

    @Override
    public void execute(Frame frame) {
        Integer tmp = frame.getInt(n);
        frame.pushInt(tmp);
    }

    @Override
    public String format() {
        return "iload " + n;
    }
}
