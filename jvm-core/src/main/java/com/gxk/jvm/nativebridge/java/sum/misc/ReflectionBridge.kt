package com.gxk.jvm.nativebridge.java.sum.misc

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.Instance

object ReflectionBridge {
    fun registerNatives0() {
        registerNativeMethod("sun/reflect/Reflection_getCallerClass_()Ljava/lang/Class;") { frame: Frame ->
            val callerFrame = frame.thread.callerFrame()
            val cls: Any? = callerFrame.method.clazz.runtimeClass
            frame.pushRef(cls as Instance?)
        }
    }
}