package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod

object MathBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/Math_min_(II)I") { frame: Frame ->
            val v2 = frame.popInt()
            val v1 = frame.popInt()
            if (v1 <= v2) {
                frame.pushInt(v1)
            } else {
                frame.pushInt(v2)
            }
        }
        registerNativeMethod("java/lang/Math_max_(II)I") { frame: Frame ->
            val v2 = frame.popInt()
            val v1 = frame.popInt()
            if (v1 >= v2) {
                frame.pushInt(v1)
            } else {
                frame.pushInt(v2)
            }
        }
    }
}