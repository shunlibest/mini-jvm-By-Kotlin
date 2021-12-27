package com.gxk.jvm.nativebridge.java.security

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod

object AccessControllerBridge {
    fun registerNative0() {
        // static
        registerNativeMethod("java/security/AccessController_doPrivileged_(Ljava/security/PrivilegedAction;)Ljava/lang/Object;") { frame: Frame ->
            val thisObj = frame.popRef()
            val method = thisObj.clazz.getMethod("run", "()Ljava/lang/Object;")
            val newFrame = Frame(method)
            newFrame.setRef(0, thisObj)
            frame.thread.pushFrame(newFrame)
        }
        // static
        registerNativeMethod("java/security/AccessController_doPrivileged_(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;") { frame: Frame ->
            val thisObj = frame.popRef()
            val method = thisObj.clazz.getMethod("run", "()Ljava/lang/Object;")
            val newFrame = Frame(method)
            newFrame.setRef(0, thisObj)
            frame.thread.pushFrame(newFrame)
        }
    }
}