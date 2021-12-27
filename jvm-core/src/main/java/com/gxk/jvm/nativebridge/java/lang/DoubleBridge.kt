package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.rtda.UnionSlot

object DoubleBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/Double_doubleToRawLongBits_(D)J") { frame: Frame ->
            val tmp = frame.popDouble()
            val v = java.lang.Double.doubleToRawLongBits(tmp)
            frame.pushLong(v)
        }
        registerNativeMethod("java/lang/Double_longBitsToDouble_(J)D") { frame: Frame ->
            val tmp = frame.popLong()
            val v = java.lang.Double.longBitsToDouble(tmp)
            frame.pushDouble(v)
        }
        registerNativeMethod("java/lang/Double_valueOf_(D)Ljava/lang/Double;") { frame: Frame ->
            val clazz = findClass("java/lang/Double")
            val instance = clazz!!.newInstance()
            val v2 = frame.popSlot()
            val v1 = frame.popSlot()
            instance.setField("value", "D", UnionSlot.of(v1, v2))
            frame.pushRef(instance)
        }
    }
}