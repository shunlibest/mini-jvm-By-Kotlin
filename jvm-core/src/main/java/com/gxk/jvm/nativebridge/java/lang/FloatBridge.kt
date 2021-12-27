package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod

object FloatBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/Float_intBitsToFloat_(I)F") { frame: Frame ->
            val tmp = frame.popInt()
            val v = java.lang.Float.intBitsToFloat(tmp)
            frame.pushFloat(v)
        }
        registerNativeMethod("java/lang/Float_floatToRawIntBits_(F)I") { frame: Frame ->
            val tmp = frame.popFloat()
            val v = java.lang.Float.floatToRawIntBits(tmp)
            frame.pushInt(v)
        }
    }
}