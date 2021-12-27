package com.gxk.jvm

import com.gxk.jvm.classloader.ClassLoader
import com.gxk.jvm.classpath.Classpath
import com.gxk.jvm.interpret.Interpreter
import com.gxk.jvm.nativebridge.java.io.*
import com.gxk.jvm.nativebridge.java.lang.*
import com.gxk.jvm.nativebridge.java.nio.charset.CharsetBridge
import com.gxk.jvm.nativebridge.java.security.AccessControllerBridge
import com.gxk.jvm.nativebridge.java.sum.misc.ReflectionBridge
import com.gxk.jvm.nativebridge.java.util.PropertiesBridge
import com.gxk.jvm.nativebridge.java.util.RandomBridge
import com.gxk.jvm.nativebridge.java.util.ZipFileBridge
import com.gxk.jvm.nativebridge.java.util.concurrent.AtomicLongBridge
import com.gxk.jvm.nativebridge.sun.misc.UnsafeBridge
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.MetaSpace
import com.gxk.jvm.rtda.Thread
import com.gxk.jvm.rtda.UnionSlot
import com.gxk.jvm.rtda.heap.Heap
import com.gxk.jvm.util.EnvHolder
import com.gxk.jvm.util.Utils

public class VirtualMachine {

    fun run(cmd: Args) {
        if (cmd.verbose) {
            EnvHolder.verbose = true
        }
        if (cmd.verboseTrace) {
            EnvHolder.verboseTrace = true
        }
        if (cmd.verboseCall) {
            EnvHolder.verboseCall = true
        }
        if (cmd.verboseClass) {
            EnvHolder.verboseClass = true
        }
        if (cmd.verboseDebug) {
            EnvHolder.verboseDebug = true
        }

        val classpath = Utils.classpath(cmd.classpath + ":/Users/shunli/Documents/codeJava/mini-jvm-By-Kotlin/test.jar")

        println("load: $classpath")
        val entry = Classpath.parse(classpath)

        //使用bootloader加载类
        val classLoader = ClassLoader("boot", entry)
        initVm(classLoader)

        //加载Main类（Main方法所在的类）
        val mainClass = Utils.replace(cmd.clazz, '.', EnvHolder.FILE_SEPARATOR.toCharArray()[0])
        classLoader.loadClass(mainClass)
        //加载其中的main方法
        val clazz = Heap.findClass(mainClass) ?: throw Exception("not found class: $mainClass")
        val method = clazz.getMethod("main") ?: throw IllegalStateException("not found main method")
        //执行main方法
        Interpreter.runMain(method, cmd.args)
    }

    companion object {
        fun initVm(classLoader: ClassLoader) {
            MetaSpace.main = Thread(1024)
            loadLibrary()
            loadFoundationClass(classLoader)
            initSystemOut(classLoader)
            initSystemErr(classLoader)
        }

        private fun initSystemErr(classLoader: ClassLoader) {
            val fdCls = classLoader.loadClass("java/io/FileDescriptor")
            val outFdObj = fdCls.newInstance()
            val fdInitMethod = fdCls.getMethod("<init>", "(I)V")
            val f1 = Frame(fdInitMethod)
            f1.setRef(0, outFdObj)
            f1.setInt(1, 2)
            Interpreter.execute(f1)
            val fosCls = classLoader.loadClass("java/io/FileOutputStream")
            val fosObj = fosCls.newInstance()
            val fosInitMethod = fosCls.getMethod("<init>", "(Ljava/io/FileDescriptor;)V")
            val f2 = Frame(fosInitMethod)
            f2.setRef(0, fosObj)
            f2.setRef(1, outFdObj)
            Interpreter.execute(f2)
            val psCls = classLoader.loadClass("java/io/PrintStream")
            val psObj = psCls.newInstance()
            val psInitMethod = psCls.getMethod("<init>", "(Ljava/io/OutputStream;Z)V")
            val frame = Frame(psInitMethod)
            frame.setRef(0, psObj)
            frame.setRef(1, fosObj)
            frame.setInt(2, 1)
            Interpreter.execute(frame)
            val sysCls = classLoader.loadClass("java/lang/System")
            val outField = sysCls.getField("err", "Ljava/io/PrintStream;")
            outField.`val` = UnionSlot.of(psObj)
        }

        private fun initSystemOut(classLoader: ClassLoader) {
            val fdCls = classLoader.loadClass("java/io/FileDescriptor")
            val outFdObj = fdCls.newInstance()
            val fdInitMethod = fdCls.getMethod("<init>", "(I)V")
            val f1 = Frame(fdInitMethod)
            f1.setRef(0, outFdObj)
            f1.setInt(1, 1)
            Interpreter.execute(f1)
            val fosCls = classLoader.loadClass("java/io/FileOutputStream")
            val fosObj = fosCls.newInstance()
            val fosInitMethod = fosCls.getMethod("<init>", "(Ljava/io/FileDescriptor;)V")
            val f2 = Frame(fosInitMethod)
            f2.setRef(0, fosObj)
            f2.setRef(1, outFdObj)
            Interpreter.execute(f2)
            val psCls = classLoader.loadClass("java/io/PrintStream")
            val psObj = psCls.newInstance()
            val psInitMethod = psCls.getMethod("<init>", "(Ljava/io/OutputStream;Z)V")
            val frame = Frame(psInitMethod)
            frame.setRef(0, psObj)
            frame.setRef(1, fosObj)
            frame.setInt(2, 1)
            Interpreter.execute(frame)
            val sysCls = classLoader.loadClass("java/lang/System")
            val outField = sysCls.getField("out", "Ljava/io/PrintStream;")
            outField.`val` = UnionSlot.of(psObj)
        }

        //加载方法库
        fun loadLibrary() {
            ObjectBridge.registerNatives0()         //Object里的方法
            ClassBridge.registerNatives0()          //
            SystemBridge.registerNatives0()
            FileOutputStreamBridge.registerNatives0()
            FileDescriptorBridge.registerNative0()
            MathBridge.registerNatives0()
            UnsafeBridge.registerNatives0()
            IntegerBridge.registerNatives0()
            FloatBridge.registerNatives0()
            DoubleBridge.registerNatives0()
            StringBridge.registerNatives0()
            RandomBridge.registerNatives0()
            ExceptionBridge.registerNatives0()
            ThrowableBridge.registerNatives0()
            AtomicLongBridge.registerNatives0()
            ReflectionBridge.registerNatives0()
            ClassLoaderBridge.registerNatives0()
            AccessControllerBridge.registerNative0()
            PropertiesBridge.registerNative0()
            CharsetBridge.registerNative0()
            UnixFileSystemBridge.registerNatives0()
            FileInputStreamBridge.registerNatives0()
            ZipFileBridge.registerNatives0()
            NativeInputStreamBridge.registerNatives0()
        }

        private fun loadFoundationClass(classLoader: ClassLoader) {
            // class
            val metaClass = classLoader.loadClass("java/lang/Class")
            for (cls in Heap.getClasses()) {
                if (cls.runtimeClass == null) {
                    val obj = metaClass.newInstance()
                    cls.runtimeClass = obj
                    obj.metaClass = cls
                }
            }

            // primitive
            classLoader.loadPrimitiveClass("char")
            classLoader.loadPrimitiveClass("boolean")
            classLoader.loadPrimitiveClass("byte")
            classLoader.loadPrimitiveClass("short")
            classLoader.loadPrimitiveClass("int")
            classLoader.loadPrimitiveClass("long")
            classLoader.loadPrimitiveClass("float")
            classLoader.loadPrimitiveClass("double")
            classLoader.loadPrimitiveClass("void")

            // string
            classLoader.loadClass("java/lang/String")

            // primitive wrapper
            classLoader.loadClass("java/lang/Character")
            classLoader.loadClass("java/lang/Boolean")
            classLoader.loadClass("java/lang/Byte")
            classLoader.loadClass("java/lang/Short")
            classLoader.loadClass("java/lang/Integer")
            classLoader.loadClass("java/lang/Long")
            classLoader.loadClass("java/lang/Float")
            classLoader.loadClass("java/lang/Double")
            classLoader.loadClass("java/lang/Void")

            // primitvie Arry class
            classLoader.loadPrimitiveArrayClass("[B")
            classLoader.loadPrimitiveArrayClass("[C")
            classLoader.loadPrimitiveArrayClass("[Z")
            classLoader.loadPrimitiveArrayClass("[S")
            classLoader.loadPrimitiveArrayClass("[I")
            classLoader.loadPrimitiveArrayClass("[F")
            classLoader.loadPrimitiveArrayClass("[L")
            classLoader.loadPrimitiveArrayClass("[D")
        }
    }
}