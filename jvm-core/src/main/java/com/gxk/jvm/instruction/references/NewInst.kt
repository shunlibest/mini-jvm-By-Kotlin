package com.gxk.jvm.instruction.references

import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.heap.Heap.findNativeMethod
import com.gxk.jvm.interpret.Interpreter.execute
import com.gxk.jvm.rtda.Frame
import java.lang.ClassNotFoundException

class NewInst(val clazz: String) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        var cls = findClass(clazz)
        if (cls == null) {
            val loader = frame.method.clazz.classLoader
            cls = loader.loadClass(clazz)
        }
        checkNotNull(cls) { ClassNotFoundException::class.java.name }
        if (!cls.judgeStat()) {
            // interfaceInit
            val cinit = cls.clinitMethod
            if (cinit == null) {
                cls.stat = 2
                frame.nextPc = frame.pc
                return
            }
            val clNm = cinit.nativeMethodKey()
            val clm = findNativeMethod(clNm)
            if (clm != null) {
                clm.invoke(frame)
            } else {
                cls.stat = 1
                execute(cinit)
                cls.stat = 2
            }
        }
        val obj = cls.newInstance()
        frame.pushRef(obj)
    }

    override fun format(): String {
        return "new $clazz"
    }
}