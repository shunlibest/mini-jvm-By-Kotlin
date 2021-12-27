package com.gxk.jvm.nativebridge.java.io

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
object FileDescriptorBridge {
    fun registerNative0() {
        registerNativeMethod("java/io/FileDescriptor_initIDs_()V") { frame: Frame? -> }
    }
}