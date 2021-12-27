package com.gxk.jvm.instruction.control

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame

class LookupSwitchInst(val offset: Int, val def: Int, val pairsCnt: Int, val table: Map<Int, Int>) : Instruction {
    override fun offset(): Int {
        return offset
    }

    override fun execute(frame: Frame) {
        val tmp = frame.popInt()
        val jump = table[tmp] ?: def
        frame.nextPc = frame.pc + jump
    }
}