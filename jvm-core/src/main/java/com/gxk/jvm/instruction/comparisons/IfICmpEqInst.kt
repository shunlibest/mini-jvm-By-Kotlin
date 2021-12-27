package com.gxk.jvm.instruction.comparisons

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame

class IfICmpEqInst(val offset: Int) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        val val2 = frame.popInt()
        val val1 = frame.popInt()
        if (val1 == val2) {
            frame.nextPc = frame.pc + offset
        }
    }
}