package com.gxk.jvm.instruction.references

import com.gxk.jvm.rtda.heap.Heap.findNativeMethod
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.util.Utils

class InvokeInterfaceInst(val clazzName: String, val methodName: String, val methodDescriptor: String, val count: Int, val zero: Int) : Instruction {
    override fun offset(): Int {
        return 5
    }

    override fun execute(frame: Frame) {
        val nm = findNativeMethod(Utils.genNativeMethodKey(clazzName, methodName, methodDescriptor))
        if (nm != null) {
            nm.invoke(frame)
            return
        }
        var clazz = findClass(clazzName)
        if (clazz == null) {
            clazz = frame.method.clazz.classLoader.loadClass(clazzName)
        }
        Utils.clinit(clazz)
        var method = clazz.getMethod(methodName, methodDescriptor)
        if (method == null) {
            // try find interfaces
            check(!clazz.interfaceNames.isEmpty())

            // already load interface
            if (clazz.interfaces.isEmpty()) {
                clazz.interfaceInit(frame)
            }
            if (!clazz.interfaces.isEmpty()) {
                for (intClass in clazz.interfaces) {
                    method = intClass.getMethod(methodName, methodDescriptor)
                    if (method != null) {
                        break
                    }
                }
            }
        }
        checkNotNull(method)
        check(!method.isNative) { "un impl native method call, $method" }
        val ref = frame.getThis(method.argSlotSize)
        var implMethod = ref.clazz.getMethod(methodName, methodDescriptor)
        // method is default method
        if (implMethod == null) {
            implMethod = method
        }
        Utils.invokeMethod(implMethod)
    }

    override fun format(): String {
        return "invokeinterface $clazzName $methodName $methodDescriptor"
    }
}