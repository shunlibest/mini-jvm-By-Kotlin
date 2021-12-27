package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.PrimitiveArray
import com.gxk.jvm.util.Utils

object StringBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/String_intern_()Ljava/lang/String;") { frame: Frame? -> }
        registerNativeMethod("java/lang/String_getBytes_()[B") { frame: Frame ->
            val obj = frame.popRef()
            val str = Utils.obj2Str(obj)
            val bytes = str.toByteArray()
            val array = PrimitiveArray.byteArray(bytes.size)
            for (i in bytes.indices) {
                array.ints[i] = bytes[i].toInt()
            }
            frame.pushRef(array)
        }
    }
}