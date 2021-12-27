package com.gxk.jvm.instruction.constants


import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.interpret.Interpreter.execute
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.InstanceArray
import com.gxk.jvm.rtda.UnionSlot
import com.gxk.jvm.rtda.heap.Class
import com.gxk.jvm.rtda.heap.Instance

class LdcWInst(val descriptor: String, val `val`: Any) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        when (descriptor) {
            "I" -> frame.pushInt((`val` as Int))
            "F" -> frame.pushFloat(`val` as Float)
            "Ljava/lang/String" -> {
                var klass = findClass("java/lang/String")
                if (klass == null) {
                    klass = frame.method.clazz.classLoader.loadClass("java/lang/String")
                }
                if (!klass.judgeStat()) {
                    klass.stat = 1
                    execute(klass.getMethod("<clinit>", "()V"))
                    klass.stat = 2
                }
                val `object` = klass.newInstance()
                val field = `object`.getField("value", "[C")
                val arrClazz = Class(1, "[C", frame.method.clazz.classLoader, null)
                val chars = `val`.toString().toCharArray()
                val characters = arrayOfNulls<Char>(chars.size)
                var i = 0
                while (i < chars.size) {
                    characters[i] = chars[i]
                    i++
                }
                val arr = InstanceArray(arrClazz, characters)
                field.`val` = UnionSlot.of(arr)
                frame.pushRef(`object`)
            }
            else -> frame.pushRef(`val` as Instance)
        }
    }

    override fun format(): String {
        return "ldcw $descriptor $`val`"
    }
}