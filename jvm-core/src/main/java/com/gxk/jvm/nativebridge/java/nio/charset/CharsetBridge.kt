package com.gxk.jvm.nativebridge.java.nio.charset

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod


object CharsetBridge {
    fun registerNative0() {
        // static
        registerNativeMethod("java/nio/charset/Charset_atBugLevel_(Ljava/lang/String;)Z") { frame: Frame ->
            frame.popRef()
            // false
            frame.pushInt(0)
        }
    }
}