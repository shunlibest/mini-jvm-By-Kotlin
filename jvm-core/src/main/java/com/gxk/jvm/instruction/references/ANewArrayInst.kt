package com.gxk.jvm.instruction.references

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.rtda.heap.Heap.registerClass
import com.gxk.jvm.rtda.heap.Class
import com.gxk.jvm.rtda.heap.Heap
import com.gxk.jvm.rtda.heap.Instance
import com.gxk.jvm.rtda.heap.InstanceArray
import com.gxk.jvm.util.Utils

class ANewArrayInst(val className: String) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        val aClass = frame.method.clazz.classLoader.loadClass(className)
        Utils.clinit(aClass)
        val count = frame.popInt()
        val name = "[L" + aClass.name + ";"
        var clazz = findClass(name)
        if (clazz == null) {
            clazz = Class(1, name, aClass.classLoader, null)
            clazz.superClass = findClass("java/lang/Object")
            clazz.stat = 2
            registerClass(name, clazz)
        }
        val objs = arrayOfNulls<Instance>(count)
        val instanceArray = InstanceArray(clazz, objs)
        frame.pushRef(instanceArray)
    }

    override fun format(): String {
        return "anewarray"
    }
}