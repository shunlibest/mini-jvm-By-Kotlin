package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.InstanceArray
import com.gxk.jvm.rtda.heap.PrimitiveArray
import com.gxk.jvm.util.Utils

object SystemBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/System_registerNatives_()V") { frame: Frame? -> }
        registerNativeMethod("java/lang/System_setIn0_(Ljava/io/InputStream;)V") { frame: Frame? -> }
        registerNativeMethod("java/lang/System_setOut0_(Ljava/io/PrintStream;)V") { frame: Frame? -> }
        registerNativeMethod("java/lang/System_setErr0_(Ljava/io/PrintStream;)V") { frame: Frame? -> }
        registerNativeMethod("java/lang/System_currentTimeMillis_()J") { frame: Frame -> frame.pushLong(System.currentTimeMillis()) }
        registerNativeMethod("java/lang/System_nanoTime_()J") { frame: Frame -> frame.pushLong(System.nanoTime()) }
        registerNativeMethod("java/lang/System_arraycopy_(Ljava/lang/Object;ILjava/lang/Object;II)V") { frame: Frame ->
            val len = frame.popInt()
            var dsp = frame.popInt()
            val dest = frame.popRef()
            if (dest is InstanceArray) {
                var ssp = frame.popInt()
                val sa = frame.popRef() as InstanceArray
                for (i in 0 until len) {
                    dest.items[dsp++] = sa.items[ssp++]
                }
            } else {
                val da = dest as PrimitiveArray
                var ssp = frame.popInt()
                val sa = frame.popRef() as PrimitiveArray
                for (i in 0 until len) {
                    if (da.ints != null) {
                        da.ints[dsp++] = sa.ints[ssp++]
                    } else if (da.longs != null) {
                        da.longs[dsp++] = sa.longs[ssp++]
                    } else if (da.floats != null) {
                        da.floats[dsp++] = sa.floats[ssp++]
                    } else {
                        da.doubles[dsp++] = sa.doubles[ssp++]
                    }
                }
            }
        }
        registerNativeMethod("java/lang/System_identityHashCode_(Ljava/lang/Object;)I") { frame: Frame -> frame.pushInt(frame.popRef().hashCode()) }
        registerNativeMethod("java/lang/System_initProperties_(Ljava/util/Properties;)Ljava/util/Properties;") { frame: Frame? -> }
        registerNativeMethod("java/lang/System_mapLibraryName_(Ljava/lang/String;)Ljava/lang/String;") { frame: Frame? -> }

        // hack
        registerNativeMethod("java/lang/System_getenv_(Ljava/lang/String;)Ljava/lang/String;") { frame: Frame ->
            val nameObj = frame.popRef()
            val `val` = System.getenv(Utils.obj2Str(nameObj))
            if (`val` == null) {
                frame.pushRef(null)
                return@registerNativeMethod
            }
            frame.pushRef(Utils.str2Obj(`val`, frame.method.clazz.classLoader))
        }
    }
}