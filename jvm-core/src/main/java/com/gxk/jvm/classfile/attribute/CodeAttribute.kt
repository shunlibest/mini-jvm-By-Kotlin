package com.gxk.jvm.classfile.attribute

import com.gxk.jvm.classfile.Attribute
import com.gxk.jvm.classfile.Attributes
import com.gxk.jvm.classfile.ExceptionTable
import com.gxk.jvm.instruction.Instruction
import java.util.LinkedHashMap

/**
 * code属性是变长属性，位于 MethodInfo结构的属性表中。
 * code 属性中包含某个方法、实例初始化方法、类或接口初始化方法的Java 虚拟机指令及相关辅助信息。
 * 如果方法声明为native 或者 abstract 方法，那么method_info结构的屬性绝不能有code 属性。
 * 在其他情况下，method_ info 必须有且只能有一个code 属性。
 *
 * @see com.gxk.jvm.classfile.MethodInfo
 */
//  Code_attribute {
//    u2 attribute_name_index;
//    u4 attribute_length;
//    u2 max_stack;
//    u2 max_locals;
//    u4 code_length;
//    u1 code[code_length];
//    u2 exception_table_length;
//    {   u2 start_pc;
//      u2 end_pc;
//      u2 handler_pc;
//      u2 catch_type;
//    } exception_table[exception_table_length];
//    u2 attributes_count;
//    attribute_info attributes[attributes_count];
//  }
data class CodeAttribute(
        val maxStacks: Int,                     //操作数栈最大深度
        val maxLocals: Int,                     //局部变量表最大槽数
        val instructions: Array<Instruction>,   //解析出来指令集
        val exceptionTable: ExceptionTable,
        val attributes: Attributes) : Attribute() {

    override fun getAttributeName(): String {
        return "Code"
    }

    fun getInstructions(): Map<Int, Instruction> {
        val map: MutableMap<Int, Instruction> = LinkedHashMap(instructions.size)
        var pc = 0
        for (instruction in instructions) {
            map[pc] = instruction
            pc += instruction.offset()
        }
        return map
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CodeAttribute

        if (maxStacks != other.maxStacks) return false
        if (maxLocals != other.maxLocals) return false
        if (!instructions.contentEquals(other.instructions)) return false
        if (exceptionTable != other.exceptionTable) return false
        if (attributes != other.attributes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = maxStacks
        result = 31 * result + maxLocals
        result = 31 * result + instructions.contentHashCode()
        result = 31 * result + exceptionTable.hashCode()
        result = 31 * result + attributes.hashCode()
        return result
    }
}