package com.gxk.jvm.classfile.attribute;

import com.gxk.jvm.classfile.Attribute;

/**
 * LineNumberTable属性用于描述Java源代码行号与字节码行号(字节码偏移量)之间的对应关系。
 * 它并不是运行时必须的属性，但默认会生成到Class文件之中，
 * 可以在Javac中使用-g:none或-g:lines选项来取消或要求生成这项信息。
 * <p>
 * 如果选择不生成LineNumberTable属性表，对程序运行产生的最主要的影响就是在抛出异常时，
 * 堆栈中将不会显示出错的行号，并且在调试程序的时候无法按照源码来设置断点
 * <code>
 * LineNumberTable_attribute {
 * u2 attribute_name_index;
 * u4 attribute_length;
 * u2 line_number_table_length;
 * {   u2 start_pc;
 * u2 line_number;
 * } line_number_table[line_number_table_length];
 * }
 * </code>
 */
public class LineNumberTable extends Attribute {
    public final Line[] lines;

    public LineNumberTable(Line[] lines) {
        this.lines = lines;
    }

    @Override
    public String getAttributeName() {
        return "LineNumberTable";
    }

    public static class Line {

        public final int startPc;       //字节码行号
        public final int lineNumber;    //Java行号

        public Line(int startPc, int lineNumber) {
            this.startPc = startPc;
            this.lineNumber = lineNumber;
        }
    }
}
