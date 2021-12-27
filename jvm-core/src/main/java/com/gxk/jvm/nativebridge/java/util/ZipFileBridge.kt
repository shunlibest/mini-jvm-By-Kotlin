package com.gxk.jvm.nativebridge.java.util

import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.util.Utils
import java.io.IOException
import java.lang.UnsupportedOperationException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object ZipFileBridge {
    fun registerNatives0() {
        registerNativeMethod("java/util/zip/ZipFile_init_(Ljava/lang/String;)V") { frame: Frame ->
            val path = frame.popRef()
            val pathStr = Utils.obj2Str(path)
            val file: ZipFile
            file = try {
                ZipFile(pathStr)
            } catch (e: IOException) {
                throw UnsupportedOperationException("zip file not found")
            }
            frame.popRef().extra = file
        }
        registerNativeMethod("java/util/zip/ZipFile_getEntry_(Ljava/lang/String;)Ljava/util/zip/ZipEntry;") { frame: Frame ->
            val obj = frame.popRef()
            val file = frame.popRef().extra as ZipFile
            val entry = Utils.obj2Str(obj)
            val zipEntry = file.getEntry(entry)
            if (zipEntry == null) {
                frame.pushRef(null)
                return@registerNativeMethod
            }
            var cls = findClass("java/util/zip/ZipEntry")
            if (cls == null) {
                cls = frame.method.clazz.classLoader.loadClass("java/util/zip/ZipEntry")
            }
            val entryObj = cls.newInstance()
            entryObj.extra = zipEntry
            frame.pushRef(entryObj)
        }
        registerNativeMethod("java/util/zip/ZipFile_getInputStream_(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;") { frame: Frame ->
            val entry = frame.popRef().extra as ZipEntry
            val file = frame.popRef().extra as ZipFile
            try {
                val `is` = file.getInputStream(entry)
                var cls = findClass("java/io/NativeInputStream")
                if (cls == null) {
                    cls = frame.method.clazz.classLoader.loadClass("java/io/NativeInputStream")
                }
                val obj = cls!!.newInstance()
                obj.extra = `is`
                frame.pushRef(obj)
            } catch (e: IOException) {
                e.printStackTrace()
                throw UnsupportedOperationException("ioe")
            }
        }
        registerNativeMethod("java/util/zip/ZipFile_close_()V") { frame: Frame ->
            val file = frame.popRef().extra as ZipFile
            try {
                file.close()
            } catch (e: IOException) {
                e.printStackTrace()
                throw UnsupportedOperationException("zip file close")
            }
        }
    }
}