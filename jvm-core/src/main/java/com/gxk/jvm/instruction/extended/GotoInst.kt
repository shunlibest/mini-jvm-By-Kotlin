package com.gxk.jvm.instruction.extended

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame

class GotoInst(val offset: Short) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        frame.nextPc = frame.pc + offset
    }

    override fun format(): String {
        return "goto $offset"
    }
}