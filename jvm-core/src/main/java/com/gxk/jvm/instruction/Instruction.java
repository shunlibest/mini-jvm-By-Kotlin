package com.gxk.jvm.instruction

import com.gxk.jvm.rtda.Frame

//指令解析
public interface Instruction {
    //该条指令的偏移量
    default int offset() {
        return 1;
    }

    //在栈针中执行该条指令
    void execute(Frame frame);

    default String debug(String prefix) {
        return prefix.concat(this.format());
    }

    default String format() {
        return this.getClass().getSimpleName();
    }
}