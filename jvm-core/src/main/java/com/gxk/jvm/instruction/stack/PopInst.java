package com.gxk.jvm.instruction.stack;

import com.gxk.jvm.instruction.Instruction;


import com.gxk.jvm.rtda.Frame;

/**
 * 操作	将操作数栈的栈顶元素出栈<br>
 * 描述	pop指令只能用来操作2.11.1小节中定义的分类1运算类型的value
 */
public class PopInst implements Instruction {

    @Override
    public void execute(Frame frame) {
        frame.popRef();
    }

    @Override
    public String format() {
        return "pop";
    }
}
