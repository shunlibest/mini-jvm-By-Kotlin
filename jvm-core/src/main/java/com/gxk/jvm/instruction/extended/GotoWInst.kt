package com.gxk.jvm.instruction.extended

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame

class GotoWInst(val offset: Int) : Instruction {
    override fun offset(): Int {
        return 5
    }

    override fun execute(frame: Frame) {
        frame.nextPc = frame.pc + offset
    }
}