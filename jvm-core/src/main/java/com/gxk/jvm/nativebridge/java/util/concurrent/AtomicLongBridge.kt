package com.gxk.jvm.nativebridge.java.util.concurrent

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
object AtomicLongBridge {
    fun registerNatives0() {
        registerNativeMethod("java/util/concurrent/atomic/AtomicLong_VMSupportsCS8_()Z") { frame: Frame -> frame.pushInt(0) }
        registerNativeMethod("java/util/concurrent/atomic/AtomicLong_<clinit>_()V") { frame: Frame? -> }
    }
}