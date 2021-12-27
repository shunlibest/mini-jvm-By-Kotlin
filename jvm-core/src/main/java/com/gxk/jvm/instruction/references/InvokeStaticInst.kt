package com.gxk.jvm.instruction.references

import com.gxk.jvm.rtda.heap.Heap.findNativeMethod
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.util.Utils

class InvokeStaticInst(val clazzName: String, val methodName: String, val descriptor: String) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        val nm = findNativeMethod(Utils.genNativeMethodKey(clazzName, methodName, descriptor))
        if (nm != null) {
            nm.invoke(frame)
            return
        }
        var aClass = findClass(clazzName)
        if (aClass == null) {
            aClass = frame.method.clazz.classLoader.loadClass(clazzName)
        }
        Utils.clinit(aClass)
        val method = aClass.getMethod(methodName, descriptor)
        check(!method.isNative) { "un impl native method call, $method" }
        Utils.invokeMethod(method)
    }

    override fun format(): String {
        return "invokestatic $clazzName $methodName $descriptor"
    }

    override fun toString(): String {
        return "InvokeStaticInst{" +
                "clazzName='" + clazzName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}'
    }
}