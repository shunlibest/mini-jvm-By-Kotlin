package com.gxk.jvm.rtda

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.heap.Instance
import com.gxk.jvm.rtda.heap.Method

// 栈帧, 每一个方法, 加载的时候,会生成一个方法栈
class Frame {
    val method: Method
    private val localVars: LocalVars
    private val operandStack: OperandStack
    private val instructionMap: Map<Int, Instruction>
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
    fun pushInt(`val`: Int) {
        operandStack.pushInt(`val`)
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

    fun pushRef(`val`: Instance?) {
        operandStack.pushRef(`val`)
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