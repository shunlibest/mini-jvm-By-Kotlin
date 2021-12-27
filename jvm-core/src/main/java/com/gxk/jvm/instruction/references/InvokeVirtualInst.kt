package com.gxk.jvm.instruction.references

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.findNativeMethod
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.util.Utils
import java.lang.IllegalStateException

class InvokeVirtualInst(val clazz: String, val methodName: String, val methodDescriptor: String) : Instruction {
    override fun offset(): Int {
        return 3
    }

    override fun execute(frame: Frame) {
        if ("sun/misc/Unsafe" == clazz
                || "java/util/Properties" == clazz
                || "java/util/zip/ZipFile" == clazz) {
            val nativeMethod = findNativeMethod(Utils.genNativeMethodKey(clazz, methodName, methodDescriptor))
            if (nativeMethod != null) {
                nativeMethod.invoke(frame)
                return
            }
        }
        val clazz = findClass(clazz)
        var method = clazz!!.getMethod(methodName, methodDescriptor)
        if (method == null) {
            // try find interfaces
            if (clazz.interfaceNames.isEmpty()) {
                println(this.clazz + " " + methodName + " " + methodDescriptor)
                throw IllegalStateException()
            }

            // already load interface
            if (!clazz.interfaces.isEmpty()) {
                for (intClass in clazz.interfaces) {
                    method = intClass.getMethod(methodName, methodDescriptor)
                    if (method != null) {
                        break
                    }
                }
            } else {
                clazz.interfaceInit(frame)
                return
            }
        }
        checkNotNull(method)

        // super method
        // fill args
        val size = method.argSlotSize
        val self = frame.getThis(size)
        val implMethod = self.clazz.getMethod(methodName, methodDescriptor)
        val nm = findNativeMethod(Utils.genNativeMethodKey(implMethod))
        if (nm != null) {
            nm.invoke(frame)
            return
        }
        check(!implMethod.isNative)
        Utils.invokeMethod(implMethod)
    }

    override fun format(): String {
        return "invokevirtual $clazz $methodName $methodDescriptor"
    }

    override fun toString(): String {
        return "InvokeVirtualInst{" +
                "clazz='" + clazz + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodDescriptor='" + methodDescriptor + '\'' +
                '}'
    }
}