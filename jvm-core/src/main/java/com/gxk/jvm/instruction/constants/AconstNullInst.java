package com.gxk.jvm.instruction.constants;

import com.gxk.jvm.instruction.Instruction;
import com.gxk.jvm.rtda.Frame;

/**
 * 将一个null值入栈到操作数栈中 <br>
 * 描述:	将一个null对象引用入栈到操作数栈中 <br>
 * 注意:	Java虚拟机并没有强制规定null值在虚拟机的内存中应该如何实际表示 <br>
 * <pre>
 * ... →
 * ..., null
 * </pre>
 */
public class AconstNullInst implements Instruction {

    @Override
    public void execute(Frame frame) {
        frame.pushRef(null);
    }

    @Override
    public String format() {
        return "aconst_null";
    }
}
