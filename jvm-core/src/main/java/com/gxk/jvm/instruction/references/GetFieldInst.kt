package com.gxk.jvm.instruction.references

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.UnionSlot
import com.gxk.jvm.util.Utils

class GetFieldInst(val clazz: String, val fieldName: String, val fieldDescriptor: String) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        // hack for java/nio/charset/Charset name Ljava/lang/String;
        if (clazz == "java/nio/charset/Charset" && fieldName == "name") {
            val obj = frame.popRef()
            val field = obj.getField(fieldName, fieldDescriptor)
            field.`val` = UnionSlot.of(Utils.str2Obj("UTF-8", obj.clazz.classLoader))
            field[frame]
            return
        }
        val obj = frame.popRef()
        val field = obj.getField(fieldName, fieldDescriptor)
        field[frame]
    }

    override fun format(): String {
        return "getfield $clazz $fieldName $fieldDescriptor"
    }

    override fun toString(): String {
        return "GetFieldInst{" +
                "clazz='" + clazz + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldDescriptor='" + fieldDescriptor + '\'' +
                '}'
    }
}