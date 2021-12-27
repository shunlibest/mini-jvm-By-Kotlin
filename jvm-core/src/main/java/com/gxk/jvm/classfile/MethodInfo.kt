package com.gxk.jvm.classfile

import com.gxk.jvm.classfile.attribute.CodeAttribute
import com.gxk.jvm.classfile.attribute.LineNumberTable

//method_info {
//    u2             access_flags;
//    u2             name_index;
//    u2             descriptor_index;
//    u2             attributes_count;
//    attribute_info attributes[attributes_count];
//    }
data class MethodInfo(
        val accessFlags: Int,           //方法可见性,final,abstract,native方法描述
        val name: String,               //方法名(包括路径名)
        val descriptor: Descriptor,
        val attributes: Attributes
) {
    val codeAttribute: CodeAttribute
        get() {
            for (attribute in attributes.attributes) {
                if (attribute is CodeAttribute) {
                    return attribute
                }
            }
            throw IllegalStateException("codeAttribute找不到")
        }
    val lineNumber: LineNumberTable
        get() {
//            if (this.codeAttribute == null) {
//                return null
//            }
            for (attribute in this.codeAttribute.attributes.attributes) {
                if (attribute is LineNumberTable) {
                    return attribute
                }
            }
            val line = LineNumberTable.Line(0, 0)
            val arrayListOf = arrayListOf(line)
            return LineNumberTable(arrayListOf.toTypedArray())
        }
}