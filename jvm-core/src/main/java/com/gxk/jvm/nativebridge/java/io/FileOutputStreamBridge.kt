package com.gxk.jvm.nativebridge.java.io

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import java.io.FileOutputStream
import java.io.FileDescriptor
import java.io.IOException
import java.lang.IllegalStateException
import com.gxk.jvm.rtda.heap.PrimitiveArray

object FileOutputStreamBridge {
    fun registerNatives0() {
        registerNativeMethod("java/io/FileOutputStream_open0_(Ljava/lang/String;Z)V") { frame: Frame? -> }
        registerNativeMethod("java/io/FileOutputStream_initIDs_()V") { frame: Frame? -> }
        registerNativeMethod("java/io/FileOutputStream_close0_()V") { frame: Frame ->
            // TODO real close
            frame.popRef()
        }
        registerNativeMethod("java/io/FileDescriptor_sync_()V") { frame: Frame -> frame.popRef() }
        registerNativeMethod("java/io/FileOutputStream_write_(IZ)V") { frame: Frame ->
            val append = frame.popInt() == 1
            val `val` = frame.popInt()
            val thisObj = frame.popRef()
            val fd = thisObj.getField("fd", "Ljava/io/FileDescriptor;")
            val fdObj = fd.`val`.ref
            val realFd = fdObj.getField("fd", "I").`val`.int
            // out
            if (realFd == 1) {
                try {
                    FileOutputStream(FileDescriptor.out).write(`val`)
                } catch (e: IOException) {
                    throw IllegalStateException()
                }
            }
            if (realFd == 2) {
                try {
                    FileOutputStream(FileDescriptor.err).write(`val`)
                } catch (e: IOException) {
                    throw IllegalStateException()
                }
            }
        }
        registerNativeMethod("java/io/FileOutputStream_writeBytes_([BIIZ)V") { frame: Frame ->
            val append = frame.popInt() == 1
            val len = frame.popInt()
            val off = frame.popInt()
            val arg1 = frame.popRef() as PrimitiveArray
            val bytes = ByteArray(len)
            for (i in off until len) {
                bytes[i - off] = arg1.ints[i].toByte()
            }
            val thisObj = frame.popRef()
            val fd = thisObj.getField("fd", "Ljava/io/FileDescriptor;")
            val fdObj = fd.`val`.ref
            val realFd = fdObj.getField("fd", "I").`val`.int
            // out
            if (realFd == 1) {
                try {
                    FileOutputStream(FileDescriptor.out).write(bytes)
                } catch (e: IOException) {
                    throw IllegalStateException()
                }
            }
            if (realFd == 2) {
                try {
                    FileOutputStream(FileDescriptor.err).write(bytes)
                } catch (e: IOException) {
                    throw IllegalStateException()
                }
            }
        }
    }
}