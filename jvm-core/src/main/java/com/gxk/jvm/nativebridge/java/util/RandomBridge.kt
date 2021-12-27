package com.gxk.jvm.nativebridge.java.util

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import java.util.*

object RandomBridge {
    fun registerNatives0() {
        // random
        registerNativeMethod("java/util/Random_<clinit>_()V") { frame: Frame? -> }
        registerNativeMethod("java/util/Random_<init>_()V") { frame: Frame -> frame.popRef() }
        registerNativeMethod("java/util/Random_<init>_(J)V") { frame: Frame ->
            frame.popLong()
            frame.popRef()
        }
        registerNativeMethod("java/util/Random_nextInt_()I") { frame: Frame ->
            val tmp = Random().nextInt()
            frame.popRef()
            frame.pushInt(tmp)
        }
        registerNativeMethod("java/util/Random_nextInt_(I)I") { frame: Frame ->
            val rg = frame.popInt()
            frame.popRef()
            val tmp = Random().nextInt(rg)
            frame.pushInt(tmp)
        }
    }
}