package com.gxk.jvm.instruction.extended

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame

class IfNullInst(val offset: Int) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        val ref: Any = frame.popRef()
        if (ref == null) {
            frame.nextPc = frame.pc + offset
        }
    }

    override fun format(): String {
        return "ifnull $offset"
    }
}