package com.gxk.jvm.instruction.references

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame
import java.lang.RuntimeException

class AThrowInst : Instruction {
    override fun execute(frame: Frame) {
        val thread = frame.thread
        val exc = frame.popRef()
        val name = exc.clazz.name
        var handlerPc = frame.method.getHandlerPc(frame.pc, name)
        while (handlerPc == null && !thread.empty()) {
            val ef = thread.popFrame()
            val msg = ef.currentMethodFullName + "(" + ef.getCurrentSource() + ":" + ef.currentLine + ")"
            System.err.println(msg)
            if (thread.empty()) {
                break
            }
            val f = thread.topFrame()
            handlerPc = f.method.getHandlerPc(f.pc, name)
        }

        // no exception handler ...
        if (handlerPc == null) {
            System.err.println(exc)
            throw RuntimeException("no exception handler")
        }
        thread.topFrame().pushRef(exc)
        thread.topFrame().nextPc = handlerPc
    }

    override fun format(): String {
        return "athrow"
    }
}