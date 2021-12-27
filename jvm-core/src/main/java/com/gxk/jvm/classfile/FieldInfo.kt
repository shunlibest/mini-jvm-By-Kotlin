package com.gxk.jvm.classfile

///**
// *
// */
//field_info {
//    u2             access_flags;
//    u2             name_index;
//    u2             descriptor_index;
//    u2             attributes_count;
//    attribute_info attributes[attributes_count];
//    }
class FieldInfo(
        val accessFlags: Int,       //表示该字段的访问权限和属性,包括public,private,protected,final,transient,enum等
        val name: String,           //变量的名称, 索引值
        val descriptor: Descriptor, //当前变量的类型
        val attributes: Attributes, //
)