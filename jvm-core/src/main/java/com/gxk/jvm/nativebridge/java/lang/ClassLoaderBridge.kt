package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import java.lang.UnsupportedOperationException

object ClassLoaderBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/ClassLoader_registerNatives_()V") { frame: Frame? -> }
        registerNativeMethod("java/lang/ClassLoader_defineClass0_(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/ClassLoader_defineClass1_(Ljava/lang/String;[BIILjava/security/ProtectionDomain;Ljava/lang/String;)Ljava/lang/Class;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/ClassLoader_defineClass2_(Ljava/lang/String;Ljava/nio/ByteBuffer;IILjava/security/ProtectionDomain;Ljava/lang/String;)Ljava/lang/Class;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/ClassLoader_resolveClass0_(Ljava/lang/Class;)V") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/ClassLoader_findBootstrapClass_(Ljava/lang/String;)Ljava/lang/Class;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/ClassLoader_findLoadedClass0_(Ljava/lang/String;)Ljava/lang/Class;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/ClassLoader_findBuiltinLib_(Ljava/lang/String;)Ljava/lang/String;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/ClassLoader_retrieveDirectives_()Ljava/lang/AssertionStatusDirectives;") { frame: Frame? -> throw UnsupportedOperationException() }
    }
}