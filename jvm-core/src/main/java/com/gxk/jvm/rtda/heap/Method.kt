package com.gxk.jvm.rtda.heap

import com.gxk.jvm.classfile.ExceptionTable
import com.gxk.jvm.classfile.attribute.LineNumberTable
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.util.Utils
import java.util.Objects

//加载class后, 会解析其中的方法
class Method(val accessFlags: Int,
             val name: String,
             val descriptor: String,
             val maxStacks: Int,
             val maxLocals: Int,
             val instructionMap: Map<Int, Instruction>,
             val exceptionTable: ExceptionTable,
             val lineNumberTable: LineNumberTable) {

    lateinit var clazz: Class
    val returnType: String
        get() = descriptor.substring(descriptor.indexOf(")") + 1)
    val args: List<String>
        get() = Utils.parseMethodDescriptor(descriptor)
    val argSlotSize: Int
        get() {
            var cnt = 0
            for (it in Utils.parseMethodDescriptor(descriptor)) {
                if ("J" == it) {
                    cnt += 2
                    continue
                }
                if ("D" == it) {
                    cnt += 2
                    continue
                }
                cnt++
            }
            if (!Utils.isStatic(accessFlags)) {
                cnt++
            }
            return cnt
        }

    override fun toString(): String {
        return "KMethod{" +
                "accessFlags=" + accessFlags +
                ", name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", maxStacks=" + maxStacks +
                ", maxLocals=" + maxLocals +
                ", instructionMap=" + instructionMap +
                ", clazz=" + clazz!!.name +
                '}'
    }

    val isNative: Boolean
        get() = accessFlags and 0x0100 != 0
    val isStatic: Boolean
        get() = accessFlags and 0x0008 != 0

    fun nativeMethodKey(): String {
        return Utils.genNativeMethodKey(this)
    }

    fun getHandlerPc(pc: Int, name: String?): Int? {
        for (exception in exceptionTable.exceptions) {
            if (exception.clazz == null || exception.clazz == name) {
                if (pc >= exception.startPc && pc < exception.endPc) {
                    return exception.handlerPc
                }
            }
        }
        return null
    }

    fun getLine(pc: Int): Int {
        var ret = 0
        for (line in lineNumberTable.lines) {
            ret = if (line.startPc <= pc) {
                line.lineNumber
            } else {
                break
            }
        }
        return ret
    }

    val key: String
        get() = Utils.genNativeMethodKey(this)
}