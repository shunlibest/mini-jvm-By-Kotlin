package com.gxk.jvm.nativebridge.sun.misc

import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.UnionSlot
import com.gxk.jvm.rtda.Frame
import java.nio.ByteBuffer

object UnsafeBridge {
    private val mem: MutableMap<Long, ByteArray> = HashMap()
    private var next = 1L
    fun registerNatives0() {
        registerNativeMethod("sun/misc/Unsafe_registerNatives_()V") { frame: Frame? -> }
        registerNativeMethod("sun/misc/Unsafe_getUnsafe_()Lsun/misc/Unsafe;") { frame: Frame -> frame.pushRef(null) }
        registerNativeMethod("sun/misc/Unsafe_objectFieldOffset_(Ljava/lang/reflect/Field;)J") { frame: Frame ->
            frame.popRef()
            frame.popRef()
            frame.pushLong(1L)
        }
        registerNativeMethod("sun/misc/Unsafe_getAndAddInt_(Ljava/lang/Object;JI)I") { frame: Frame ->
            val delta = frame.popInt()
            val offset = frame.popLong()
            val thisObj: Any = frame.popRef()
            val field = frame.popRef().getField("value", "I")
            val `val` = field.`val`.int + delta
            field.`val` = UnionSlot.of(`val`)
            frame.pushInt(`val` - delta)
        }
        registerNativeMethod("sun/misc/Unsafe_allocateMemory_(J)J") { frame: Frame ->
            val `val` = frame.popLong()
            frame.popRef()
            val data = ByteArray(`val`.toInt())
            mem[next] = data
            next += `val`.toInt().toLong()
            frame.pushLong(`val`)
        }
        registerNativeMethod("sun/misc/Unsafe_putLong_(JJ)V") { frame: Frame ->
            val v2 = frame.popLong()
            val v1 = frame.popLong()
            frame.popRef() // this
            val buffer = ByteBuffer.allocate(8)
            buffer.putLong(0, v2)
            val bytes = buffer.array()
            mem[v1] = bytes
        }
        registerNativeMethod("sun/misc/Unsafe_getByte_(J)B") { frame: Frame ->
            val arg = frame.popLong()
            frame.popRef()
            val bytes = mem[arg]
            val b = bytes!![0]
            frame.pushInt(b.toInt())
        }
    }
}