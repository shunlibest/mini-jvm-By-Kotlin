package com.gxk.jvm.instruction.constants

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame


/**
 * 格式       nop
 * 结构       nop=0 (0x0)
 * 操作数栈   无变化
 * 描述       什么事情都不做
 */
class NopInst : Instruction {

    override fun execute(frame: Frame) {


    }
}