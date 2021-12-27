package com.gxk.jvm.ext.bc

import com.gxk.jvm.classfile.ClassFile
import com.gxk.jvm.classfile.ClassReader.read
import com.gxk.jvm.classfile.MethodInfo
import java.io.IOException
import com.gxk.jvm.ext.bc.ByteCodeGenerator
import com.gxk.jvm.rtda.heap.Method
import java.io.File
import java.util.stream.Collectors

object ByteCodeGenerator {
    fun gen(clazzPath: String, methodName: String) {
        val file = File(clazzPath)
        if (!file.exists() || !file.isFile) {
            println("class missing, or illegal path")
            return
        }
        var cf: ClassFile? = null
        cf = try {
            read(clazzPath)
        } catch (e: IOException) {
            println("parse class err, $clazzPath")
            return
        }
        var target: MethodInfo? = null
        for (methodInfo in cf!!.methods.methodInfos) {
            if (methodInfo.name == methodName) {
                target = methodInfo
            }
        }
        if (target == null) {
            println("not found method, $methodName")
            return
        }
        val method = map(target)
        val header = "main " + method.maxStacks + " " + method.maxLocals + " " + method.args.size
        println(header)
        val keys = method.instructionMap.keys.stream().sorted()
                .collect(Collectors.toList())
        for (key in keys) {
            val instruction = method.instructionMap[key]
            println(key.toString() + " " + instruction!!.format())
        }
    }

    private fun map(cfMethodInfo: MethodInfo): Method {
        val codeAttribute = cfMethodInfo.codeAttribute
        return Method(cfMethodInfo.accessFlags, cfMethodInfo.name, cfMethodInfo.descriptor.descriptor,
                codeAttribute.maxStacks, codeAttribute.maxLocals, codeAttribute.getInstructions(), null!!, null!!)
    }
}