package com.gxk.jvm.instruction.references

import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.interpret.Interpreter.execute
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Class
import java.lang.IllegalStateException
import java.util.ArrayList

class GetStaticInst(val clazz: String, val fieldName: String, val fieldDescriptor: String) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        var cls = findClass(clazz)
        if (cls == null) {
            cls = frame.method.clazz.classLoader.loadClass(clazz)
        }
        if (!cls.judgeStat()) {
            val cinit = cls.getMethod("<clinit>", "()V") ?: throw IllegalStateException()
            cls.stat = 1
            execute(cinit)
            cls.stat = 2
        }
        var field = cls.getField(fieldName, fieldDescriptor)
        if (field == null) {
            // interface
            check(!cls.interfaceNames.isEmpty())

            // already load interface
            if (cls.interfaces.isEmpty()) {
                val interfaces: MutableList<Class> = ArrayList()
                for (interfaceName in cls.interfaceNames) {
                    var tmp = findClass(interfaceName)
                    if (tmp == null) {
                        tmp = frame.method.clazz.classLoader.loadClass(interfaceName)
                    }
                    interfaces.add(tmp)
                    if (!tmp.judgeStat()) {
                        val cinit = tmp.clinitMethod ?: throw IllegalStateException()
                        tmp.stat = 1
                        execute(cinit)
                        tmp.stat = 2
                    }
                }
                cls.interfaces = interfaces
            }
            if (!cls.interfaces.isEmpty()) {
                for (intClass in cls.interfaces) {
                    field = intClass.getField(fieldName, fieldDescriptor)
                    if (field != null) {
                        break
                    }
                }
            }
        }
        checkNotNull(field)
        checkNotNull(field.`val`)
        field[frame]
    }

    override fun format(): String {
        return "getstatic $clazz $fieldName $fieldDescriptor"
    }

    override fun toString(): String {
        return "GetStaticInst{" +
                "clazz='" + clazz + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldDescriptor='" + fieldDescriptor + '\'' +
                '}'
    }
}