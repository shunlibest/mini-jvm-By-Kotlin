package com.gxk.jvm.instruction.stack;

import com.gxk.jvm.instruction.Instruction;


import com.gxk.jvm.rtda.Frame;

/**
 * 操作	将操作数栈栈顶的一个或两个元素出栈<br>
 * 描述	将操作数栈栈顶的一个或两个元素出栈
 */
public class Pop2Inst implements Instruction {

    @Override
    public void execute(Frame frame) {
        frame.popRef();
        frame.popRef();
    }
}
