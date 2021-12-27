package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.rtda.UnionSlot

object IntegerBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/Integer_valueOf_(I)Ljava/lang/Integer;") { frame: Frame ->
            val clazz = findClass("java/lang/Integer")
            val instance = clazz!!.newInstance()
            val `val` = frame.popInt()
            instance.setField("value", "I", UnionSlot.of(`val`))
            frame.pushRef(instance)
        }
    }
}