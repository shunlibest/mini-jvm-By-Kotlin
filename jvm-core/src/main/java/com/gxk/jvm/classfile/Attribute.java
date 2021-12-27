package com.gxk.jvm.classfile;

public class Attribute {
    protected String attributeName;     //指向常量池的索引, 会写死标记当的attribute值
    protected String attributeLength;   //当前属性的长度

    public String getAttributeName() {
        return attributeName;
    }
}
