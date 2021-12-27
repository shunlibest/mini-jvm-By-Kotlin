package com.gxk.jvm.interpret

import com.gxk.jvm.rtda.heap.Heap.clear
import com.gxk.jvm.interpret.Interpreter.runMain
import com.gxk.jvm.VirtualMachine.Companion.initVm
import com.gxk.jvm.classloader.ClassLoader
import com.gxk.jvm.rtda.heap.Heap
import com.gxk.jvm.util.EnvHolder
import java.lang.IllegalStateException
import java.nio.file.Paths
import com.gxk.jvm.classpath.Classpath
import com.gxk.jvm.rtda.heap.Class
import com.gxk.jvm.rtda.heap.Method
import com.gxk.jvm.util.Utils
import org.junit.After

abstract class BaseInterpreterTest {
    @After
    fun tearDown() {
        clear()
    }

    protected fun testMain(hello: String?) {
        val method = loadAndGetMainMethod(hello)
        runMain(method, arrayOfNulls(0))
    }

    protected fun loadAndGetMainMethod(clazzName: String?): Method {
        val clazz = loadAndGetClazz(clazzName)
        return clazz.getMethod("Main")!!
    }

    protected fun loadAndGetClazz(clazzName: String?): Class {
        // check MINI_JVM_HOME ready
        // 1. env
        var miniJvmHome = System.getenv("MINI_JVM_HOME")
        if (miniJvmHome == null) {
            // 1.2 check current dir
            val userDir = System.getProperty("user.dir")
            if (userDir.endsWith("jvm-core")) {
                val idx = userDir.lastIndexOf(EnvHolder.FILE_SEPARATOR)
                miniJvmHome = userDir.substring(0, idx)
            } else if (userDir.endsWith("mini-jvm")) {
                miniJvmHome = userDir
            }
        }
        checkNotNull(miniJvmHome) { "MINI_JVM_HOME not found" }
        val exampleJarPath = Paths.get(miniJvmHome, "example", "target", "example.jar")
        check(exampleJarPath.toFile().exists()) { "example.jar not found" }
        val classpath = Utils.classpath(exampleJarPath.toFile().absolutePath)
        val entry = Classpath.parse(classpath)
        val loader = ClassLoader("boot", entry)
        initVm(loader)
        return loader.loadClass(clazzName!!)
    }
}