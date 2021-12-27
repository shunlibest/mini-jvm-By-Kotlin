package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.UnionSlot
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod

object ExceptionBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/Exception_<init>_(Ljava/lang/String;)V") { frame: Frame ->
            val str = frame.popRef()
            val thisObj = frame.popRef()
            val msgField = thisObj.getField("detailMessage", "Ljava/lang/String;")
            msgField.`val` = UnionSlot.of(str)
        }
    }
}