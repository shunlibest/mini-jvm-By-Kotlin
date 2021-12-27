package com.gxk.jvm.nativebridge.java.io

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.UnsupportedOperationException
import java.io.FileDescriptor
import java.io.IOException
import com.gxk.jvm.rtda.heap.PrimitiveArray
import com.gxk.jvm.util.Utils
import java.lang.Exception

object FileInputStreamBridge {
    fun registerNatives0() {
        registerNativeMethod("java/io/FileInputStream_open0_(Ljava/lang/String;)V") { frame: Frame ->
            val fileName = frame.popRef()
            val thisObj = frame.popRef()
            val filePath = Utils.obj2Str(fileName)
            try {
                val fis = FileInputStream(filePath)
                thisObj.extra = fis
            } catch (e: FileNotFoundException) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/FileInputStream_<init>_(Ljava/io/FileDescriptor;)V") { frame: Frame ->
            val fd = frame.popRef()
            val thisObj = frame.popRef()
            val fdInt = fd.getField("fd", "I").`val`.int
            try {
                if (fdInt == 0) {
                    val fis = FileInputStream(FileDescriptor.`in`)
                    thisObj.extra = fis
                    return@registerNativeMethod
                }
                throw UnsupportedOperationException()
            } catch (e: Exception) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/FileInputStream_available0_()I") { frame: Frame ->
            val thisObj = frame.popRef()
            val extra = thisObj.extra as FileInputStream
            try {
                val available = extra.available()
                frame.pushInt(available)
            } catch (e: IOException) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/FileInputStream_close0_()V") { frame: Frame ->
            val thisObj = frame.popRef()
            val extra = thisObj.extra as FileInputStream
            try {
                extra.close()
            } catch (e: IOException) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/FileInputStream_read0_()I") { frame: Frame ->
            val thisObj = frame.popRef()
            val extra = thisObj.extra as FileInputStream
            try {
                val read = extra.read()
                frame.pushInt(read)
            } catch (e: IOException) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/FileInputStream_skip0_(J)J") { frame: Frame ->
            val `val` = frame.popLong()
            val thisObj = frame.popRef()
            val extra = thisObj.extra as FileInputStream
            try {
                val skip = extra.skip(`val`)
                frame.pushLong(skip)
            } catch (e: IOException) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/FileInputStream_readBytes_([BII)I") { frame: Frame ->
            val v3 = frame.popInt()
            val v2 = frame.popInt()
            val v1 = frame.popRef() as PrimitiveArray
            val ba = ByteArray(v1.len)
            val thisObj = frame.popRef()
            val extra = thisObj.extra as FileInputStream
            try {
                val read = extra.read(ba, v2, v3)
                for (i in v2 until v2 + v3) {
                    v1.ints[i] = ba[i].toInt()
                }
                frame.pushInt(read)
            } catch (e: IOException) {
                throw UnsupportedOperationException()
            }
        }
        registerNativeMethod("java/io/FileInputStream_initIDs_()V") { frame: Frame? -> }
    }
}