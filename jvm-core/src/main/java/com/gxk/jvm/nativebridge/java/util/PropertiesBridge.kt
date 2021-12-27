package com.gxk.jvm.nativebridge.java.util

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.util.Utils

object PropertiesBridge {
    fun registerNative0() {

        // mock
        registerNativeMethod("java/util/Properties_<init>_()V") { frame: Frame -> frame.popRef() }
        // mock
        registerNativeMethod("java/util/Properties_getProperty_(Ljava/lang/String;)Ljava/lang/String;") { frame: Frame ->
            val nameObj = frame.popRef()
            frame.popRef()
            val `val` = System.getProperty(Utils.obj2Str(nameObj))
            if (`val` == null) {
                frame.pushRef(null)
                return@registerNativeMethod
            }
            frame.pushRef(Utils.str2Obj(`val`, frame.method.clazz.classLoader))
        }
    }
}