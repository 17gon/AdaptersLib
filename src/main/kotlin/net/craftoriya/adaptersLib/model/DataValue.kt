package net.craftoriya.adaptersLib.model

sealed class DataValue {
    data class ByteVal(val v: Byte): DataValue()
    data class IntVal(val v: Int): DataValue()
    data class DoubleVal(val v: Double): DataValue()
    data class StringVal(val v: String): DataValue()
}