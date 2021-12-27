package com.gxk.jvm.instruction.references

import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.rtda.heap.Heap.findNativeMethod
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import java.lang.IllegalStateException
import com.gxk.jvm.classfile.cp.MethodHandle
import com.gxk.jvm.classfile.cp.MethodType
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.*
import com.gxk.jvm.util.Utils
import java.util.*

class InvokeDynamicInst(val methodName: String, val methodDescriptor: String, val bsIdx: Int) : Instruction {
    override fun offset(): Int {
        return 5
    }

    override fun execute(frame: Frame) {
        val bootstrapMethods = frame.method.clazz.bootstrapMethods ?: throw IllegalStateException()
        val bootstrapMethod = bootstrapMethods.methods[bsIdx]
        val argsRef = bootstrapMethod.argsRefs[1]
        val info = frame.method.clazz.constantPool!!.infos[argsRef - 1] as MethodHandle
        val bsTargetClass = Utils.getClassNameByMethodDefIdx(frame.method.clazz.constantPool, info.referenceIndex)
        val bsTargetMethod = Utils.getMethodNameByMethodDefIdx(frame.method.clazz.constantPool, info.referenceIndex)
        val descRef0 = bootstrapMethod.argsRefs[0]
        val methodType0 = frame.method.clazz.constantPool!!.infos[descRef0 - 1] as MethodType
        val bstMethodDesc0 = Utils.getString(frame.method.clazz.constantPool, methodType0.descriptorIndex)
        val descRef = bootstrapMethod.argsRefs[2]
        val methodType = frame.method.clazz.constantPool!!.infos[descRef - 1] as MethodType
        val bstMethodDesc = Utils.getString(frame.method.clazz.constantPool, methodType.descriptorIndex)
        val clazz = findClass(bsTargetClass)
        val method = clazz!!.getMethod(bsTargetMethod)
        val maxLocals = method!!.maxLocals
        val lcname = frame.method.clazz.name + "$" + frame.method.name + "$" + bsTargetClass + "$" + bsTargetMethod
        val lcMehods: MutableList<Method> = ArrayList()
        val lm = Method(method.accessFlags, methodName, bstMethodDesc0, method.maxStacks, maxLocals + 1, null!!, null!!,
                null!!)
        lcMehods.add(lm)
        val format = Utils.genNativeMethodKey(lcname, lm.name, lm.descriptor)
        if (findNativeMethod(format) == null) {
            registerNativeMethod(format) { f: Frame ->
                val bsc = findClass(bsTargetClass)
                val bsm: Method = bsc!!.getMethod(bsTargetMethod)!!
                val args = bsm.args
                val bsSize = Utils.parseMethodDescriptor(bstMethodDesc).size
                val argObjs: MutableList<Any?> = ArrayList()
                for (i in bsSize - 1 downTo 0) {
                    val arg = args[i]
                    argObjs.add(Utils.pop(f, arg))
                }
                val ref = f.popRef() as LambdaObject
                Collections.reverse(argObjs)
                val newFrame = Frame(bsm)
                for (arg in ref.args) {
                    argObjs.add(0, arg)
                }
                var slotIdx = if (bsm.isStatic) 0 else 1
                var aoi = if (bsm.isStatic) 0 else 1
                for (i in args.indices) {
                    val arg = args[i]
                    val si = Utils.setLocals(newFrame, slotIdx, arg, argObjs[aoi])
                    slotIdx += si
                    aoi++
                }
                if (!bsm.isStatic) {
                    newFrame.setRef(0, argObjs[0] as Instance?)
                }
                f.thread.pushFrame(newFrame)
            }
        }
        val lcClazz = Class(1, lcname, "java/lang/Object", ArrayList(), lcMehods, ArrayList(), null, null, frame.method.clazz.classLoader, null)
        val realSize = method.args.size
        var bsSize = Utils.parseMethodDescriptor(bstMethodDesc).size
        val args: MutableList<Any?> = ArrayList(realSize - bsSize)
        while (realSize > bsSize) {
            val arg = method.args[bsSize]
            args.add(Utils.pop(frame, arg))
            bsSize++
        }
        if (!lm.isStatic) {
            // this
            args.add(frame.popRef())
        }
        val kObject = lcClazz.newLambdaObject(args)
        frame.pushRef(kObject)
    }

    override fun format(): String {
        return "invokedynamic $methodName $methodDescriptor $bsIdx"
    }
}