package com.gxk.jvm.instruction.comparisons

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame

class IfACmpNeInst(val offset: Int) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        val val2: Any = frame.popRef()
        val val1: Any = frame.popRef()
        if (val1 !== val2) {
            frame.nextPc = frame.pc + offset
        }
    }

    override fun format(): String {
        return "if_acmpne $offset"
    }
}