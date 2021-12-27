package com.gxk.jvm.classfile

import com.gxk.jvm.classfile.attribute.BootstrapMethods
import com.gxk.jvm.classfile.attribute.SourceFile


/**
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1) * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1
 *
 * @property magic (魔数):唯一作用是确定这个文件是否为一个能被虚拟机所接受的Class文件。魔数值固定为 OxCAFEBABE，不会改变。
 * @property minorVersion  副版本号
 * @property majorVersion 主版本号, 共同构成了class文件的格式版本号。eg:52.0
 * @property constantPoolSize 常量池的大小:等于常量池表中的成员数加1。常量池表的索引值只有在大于0 且小于size
 * @property cpInfo  常量池: 是一种表结构,包含子符串常量、类或接口名、字段名和其他常量。第1个字节作为类型标记，用于确定该项的格式
 * @property accessFlags 方法(类)的访问权限和属性,包括:可见性(public,packet),final, abstract, interface,枚举,注解
 * @property thisClass 方法名,指向常量区的索引值,为com/shunli/HelloWorld
 * @property superClass 父类,如:java/lang/Object , 如果是Object类,则指向0
 * @property interfacesCount 该类实现的接口数量
 * @property interfaces 接口名称数组,数组大小为interfacesCount,执行常量区
 * @property fieldCount 类中变量的数量
 * @property fields
 * @property methodsCount
 * @property methods
 * @property attributesCount
 * @property attributes
 * @constructor Create empty Class file
 */
data class ClassFile(val magic: Int,
                     val minorVersion: Int,
                     val majorVersion: Int,
                     val constantPoolSize: Int,
                     val cpInfo: ConstantPool,
                     val accessFlags: Int,
                     val thisClass: Int,
                     val superClass: Int,
                     val interfacesCount: Int,
                     val interfaces: Interfaces,
                     val fieldCount: Int,
                     val fields: Fields,
                     val methodsCount: Int,
                     val methods: Methods,
                     val attributesCount: Int,
                     val attributes: Attributes) {
    // ext info
    var source: String? = null
    val sourceFile: String
        get() {
            for (attribute in attributes.attributes) {
                if (attribute is SourceFile) {
                    return attribute.name
                }
            }
            return "unknown"
        }
    val bootstrapMethods: BootstrapMethods?
        get() {
            for (attribute in attributes.attributes) {
                if (attribute is BootstrapMethods) {
                    return attribute
                }
            }
            return null
        }
}