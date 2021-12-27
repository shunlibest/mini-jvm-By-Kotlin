package com.gxk.jvm.instruction.references

import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame

class PutStaticInst(val clazz: String, val fieldName: String, val fieldDescriptor: String) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        var aClass = findClass(clazz)
        if (aClass == null) {
            aClass = frame.method.clazz.classLoader.loadClass(clazz)
        }
        val field = aClass.getField(fieldName, fieldDescriptor)
        field.set(frame)
    }

    override fun format(): String {
        return "putstatic $clazz $fieldName $fieldDescriptor"
    }

    override fun toString(): String {
        return "PutStaticInst{" +
                "clazz='" + clazz + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldDescriptor='" + fieldDescriptor + '\'' +
                '}'
    }
}