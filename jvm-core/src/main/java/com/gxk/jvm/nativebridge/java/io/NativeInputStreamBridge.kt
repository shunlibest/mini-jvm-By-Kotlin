package com.gxk.jvm.nativebridge.java.io

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import java.io.IOException
import java.io.InputStream
import java.lang.UnsupportedOperationException

object NativeInputStreamBridge {
    fun registerNatives0() {
        registerNativeMethod("java/io/NativeInputStream_read_()I") { frame: Frame ->
            val `is` = frame.popRef().extra as InputStream
            try {
                val read = `is`.read()
                frame.pushInt(read)
            } catch (e: IOException) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/NativeInputStream_available_()I") { frame: Frame ->
            val `is` = frame.popRef().extra as InputStream
            try {
                val available = `is`.available()
                frame.pushInt(available)
            } catch (e: IOException) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/NativeInputStream_close_()V") { frame: Frame ->
            val `is` = frame.popRef().extra as InputStream
            try {
                `is`.close()
            } catch (e: IOException) {
                throw UnsupportedOperationException()
            }
        }
    }
}