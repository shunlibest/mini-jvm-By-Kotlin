package com.gxk.jvm.nativebridge.java.io

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.rtda.heap.Heap.registerClass
import com.gxk.jvm.rtda.heap.Class
import com.gxk.jvm.rtda.heap.Heap
import com.gxk.jvm.rtda.heap.Instance
import com.gxk.jvm.rtda.heap.NativeMethod
import java.lang.UnsupportedOperationException
import com.gxk.jvm.rtda.heap.InstanceArray
import com.gxk.jvm.util.Utils
import java.io.File

object UnixFileSystemBridge {
    fun registerNatives0() {
        registerNativeMethod("java/io/UnixFileSystem_initIDs_()V") { frame: Frame? -> }
        registerNativeMethod("java/io/UnixFileSystem_getBooleanAttributes0_(Ljava/io/File;)I") { frame: Frame ->
            val fileObj = frame.popRef()
            val thisObj: Any = frame.popRef()
            val pathObj = fileObj.getField("path", "Ljava/lang/String;").`val`.ref
            val path = Utils.obj2Str(pathObj)
            val file = File(path)
            val exists = file.exists()
            val directory = file.isDirectory
            var ret = 0
            if (exists) {
                ret = ret or 0x01
            }
            if (directory) {
                ret = ret or 0x04
            }
            frame.pushInt(ret)
        }
        registerNativeMethod("java/io/UnixFileSystem_canonicalize0_(Ljava/lang/String;)Ljava/lang/String;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_checkAccess_(Ljava/io/File;I)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_getLastModifiedTime_(Ljava/io/File;)J") { frame: Frame ->
            val file = frame.popRef()
            frame.popRef()
            val path = file.getField("path", "Ljava/lang/String;")
            val pathStr = Utils.obj2Str(path.`val`.ref)
            val lm = File(pathStr).lastModified()
            frame.pushLong(lm)
        }
        registerNativeMethod("java/io/UnixFileSystem_getLength_(Ljava/io/File;)J") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_setPermission_(Ljava/io/File;IZZ)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_createFileExclusively_(Ljava/lang/String;)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_delete0_(Ljava/io/File;)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_list_(Ljava/io/File;)[Ljava/lang/String;") { frame: Frame ->
            val file = frame.popRef()
            frame.popRef()
            val path = file.getField("path", "Ljava/lang/String;")
            val pathStr = Utils.obj2Str(path.`val`.ref)
            val list = File(pathStr).list()
            val items = arrayOfNulls<Instance>(list.size)
            for (i in list.indices) {
                items[i] = Utils.str2Obj(list[i], frame.method.clazz.classLoader)
            }
            val name = "[Ljava/lang/String;"
            var clazz = findClass(name)
            if (clazz == null) {
                clazz = Class(1, name, frame.method.clazz.classLoader, null)
                clazz.superClass = findClass("java/lang/Object")
                clazz.stat = 2
                registerClass(name, clazz)
            }
            val arr = InstanceArray(clazz, items)
            frame.pushRef(arr)
        }
        registerNativeMethod("java/io/UnixFileSystem_createDirectory_(Ljava/io/File;)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_rename0_(Ljava/io/File;Ljava/io/File;)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_setLastModifiedTime_(Ljava/io/File;J)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_setReadOnly_(Ljava/io/File;)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/io/UnixFileSystem_getSpace_(Ljava/io/File;I)J") { frame: Frame? -> throw UnsupportedOperationException() }
        // hack
        registerNativeMethod("java/io/UnixFileSystem_normalize_(Ljava/lang/String;II)Ljava/lang/String;") { frame: Frame ->
            frame.popInt()
            frame.popInt()
        }
    }
}