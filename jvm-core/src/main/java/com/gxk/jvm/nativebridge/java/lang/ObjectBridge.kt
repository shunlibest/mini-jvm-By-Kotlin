package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.Instance
import java.lang.CloneNotSupportedException

/**
 * 注册Object方法
 */
object ObjectBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/Object_registerNatives_()V") {

        }
        registerNativeMethod("java/lang/Object_clone_()Ljava/lang/Object;") { frame: Frame ->
            val obj = frame.popRef()
            var newObj: Any? = null
            try {
                newObj = obj.clone()
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
            }
            frame.pushRef(newObj as Instance?)
        }
        registerNativeMethod("java/lang/Object_getClass_()Ljava/lang/Class;") { frame: Frame ->
            val `val` = frame.popRef()
            frame.pushRef(`val`.clazz.runtimeClass)
        }
        registerNativeMethod("java/lang/Object_wait_(J)V") { frame: Frame? -> }
        registerNativeMethod("java/lang/Object_notify_()V") { frame: Frame? -> }
        registerNativeMethod("java/lang/Object_notifyAll_()V") { frame: Frame? -> }
        registerNativeMethod("java/lang/Object_hashCode_()I") { frame: Frame ->
            val `val` = frame.popRef().hashCode()
            frame.pushInt(`val`)
        }
    }
}