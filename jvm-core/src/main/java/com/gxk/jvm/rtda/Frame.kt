package com.gxk.jvm.rtda

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.heap.Instance
import com.gxk.jvm.rtda.heap.Method

// 栈帧, 每一个方法, 加载的时候,会生成一个方法栈
/**
 * 栈帧（frame）是用来存储数据和部分过程结果的数据结构，同时也用来处理动态链接方法返回值和异常分派。
 * 栈帧随着方法调用而创建，随着方法结束而销毁(包括正常完成和异常完成)
 *
 * 栈帧的存储空间由创建它的线程分配在Java虛拟机栈之中，
 * 每一个栈帧都有自己的本地变量表、操作数栈和指向当前方法所属的类的运行时常量池的引用。
 *
 * 调用新的方法时，新的栈帧也会随之而创建，并且会随着程序控制权移交到新方法而成为新的当前栈帧。
 * 方法返回之际，当前栈帧会传回此方法的执行结果给前一个栈帧，然后，虚拟机会丢弃当前栈帧，使得前一个栈帧重新成为当前栈帧。
 * @constructor Create empty Frame
 */
class Frame {
    //当前frame对应的method;
    val method: Method

    //局部变量表,最大长度在创建时就已经确认,为方法内的变量个数
    private val localVars: LocalVars
    //操作数栈
    private val operandStack: OperandStack
    private val instructionMap: Map<Int, Instruction>
    //当前线程, frame是线程私有的
    val thread: Thread
    var nextPc = 0
    var pc = 0
        private set
    var stat = 0

    constructor(method: Method) {
        this.method = method
        localVars = LocalVars(method.maxLocals)
        operandStack = OperandStack(method.maxStacks)
        thread = MetaSpace.getMainEnv()
        instructionMap = method.instructionMap
    }

    constructor(method: Method, localVars: LocalVars, thread: Thread) {
        this.method = method
        this.localVars = localVars
        operandStack = OperandStack(method.maxStacks)
        this.thread = thread
        instructionMap = method.instructionMap
    }

    val inst: Instruction?
        get() {
            pc = nextPc
            return instructionMap[pc]
        }

    // operand stack operation
    fun pushInt(value: Int) {
        operandStack.pushInt(value)
    }

    fun popInt(): Int {
        return operandStack.popInt()
    }

    fun pushLong(`val`: Long) {
        operandStack.pushLong(`val`)
    }

    fun popLong(): Long {
        return operandStack.popLong()
    }

    fun pushFloat(`val`: Float) {
        operandStack.pushFloat(`val`)
    }

    fun popFloat(): Float {
        return operandStack.popFloat()
    }

    fun pushDouble(`val`: Double) {
        operandStack.pushDouble(`val`)
    }

    fun popDouble(): Double {
        return operandStack.popDouble()
    }

    fun pushRef(value: Instance?) {
        operandStack.pushRef(value)
    }

    fun popRef(): Instance {
        return operandStack.popRef()
    }

    fun popSlot(): Slot {
        return operandStack.popSlot()
    }

    fun pushSlot(`val`: Slot?) {
        operandStack.pushSlot(`val`)
    }

    // local vars operation
    fun setInt(index: Int, `val`: Int) {
        localVars.setInt(index, `val`)
    }

    fun getInt(index: Int): Int {
        return localVars.getInt(index)
    }

    fun setFloat(index: Int, `val`: Float?) {
        localVars.setFloat(index, `val`!!)
    }

    fun getFloat(index: Int): Float {
        return localVars.getFloat(index)
    }

    fun getLong(index: Int): Long {
        return localVars.getLong(index)
    }

    fun setLong(index: Int, `val`: Long?) {
        localVars.setLong(index, `val`!!)
    }

    fun setDouble(index: Int, `val`: Double?) {
        localVars.setDouble(index, `val`!!)
    }

    fun getDouble(index: Int): Double {
        return localVars.getDouble(index)
    }

    fun setRef(index: Int, ref: Instance?) {
        localVars.setRef(index, ref)
    }

    fun getRef(index: Int): Instance {
        return localVars.getRef(index)
    }

    fun debugNextPc(space: String): String {
        return """
               ${space}nextPc = ${Integer.toString(nextPc)}
               
               """.trimIndent()
    }

    fun debugLocalVars(space: String?): String {
        val sb = StringBuilder()
        sb.append(localVars.debug(space))
        return sb.toString()
    }

    fun debugOperandStack(space: String?): String {
        val sb = StringBuilder()
        sb.append(operandStack.debug(space))
        return sb.toString()
    }

    val currentMethodFullName: String
        get() = method.clazz!!.name + "." + method.name
    val currentLine: Int
        get() = method.getLine(pc)

    fun getCurrentSource(): String? {
        return method.clazz.getSource()
    }

    fun pop(): Slot {
        return operandStack.popSlot()
    }

    fun push(`val`: Slot?) {
        operandStack.pushSlot(`val`)
    }

    operator fun set(i: Int, `val`: Slot?) {
        localVars[i] = `val`
    }

    fun getThis(size: Int): Instance {
        val slots = operandStack.slots
        return slots[operandStack.top - size].ref
    }
}