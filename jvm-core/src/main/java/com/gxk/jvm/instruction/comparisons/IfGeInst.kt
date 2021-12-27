package com.gxk.jvm.instruction.comparisons

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame

class IfGeInst(val offset: Int) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        val `val` = frame.popInt()
        if (`val` >= 0) {
            frame.nextPc = frame.pc + offset
        }
    }

    override fun format(): String {
        return "if_ge $offset"
    }
}