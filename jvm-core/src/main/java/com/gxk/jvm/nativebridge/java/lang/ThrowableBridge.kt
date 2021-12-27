package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.Heap
import com.gxk.jvm.rtda.heap.NativeMethod

object ThrowableBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/Throwable_<clinit>_()V") { frame: Frame? -> }
        registerNativeMethod("java/lang/Throwable_<init>_(Ljava/lang/String)Ljava/lang/Throwable;") { frame: Frame? -> }
        registerNativeMethod("java/lang/Throwable_fillInStackTrace_(I)Ljava/lang/Throwable;") { frame: Frame? -> }
        registerNativeMethod("java/lang/Throwable_fillInStackTrace_()Ljava/lang/Throwable;") { frame: Frame? -> }
        registerNativeMethod("java/lang/Throwable_getStackTraceDepth_()I") { frame: Frame? -> }
        registerNativeMethod("java/lang/Throwable_getStackTraceElement_(I)Ljava/lang/StackTraceElement;") { frame: Frame? -> }
    }
}