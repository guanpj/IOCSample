package com.me.guanpj.ioclib.compiler.activity.entiry

import com.bennyhuo.aptutils.types.isSameTypeWith
import com.me.guanpj.ioclib.annotations.Optional
import com.squareup.kotlinpoet.TypeName
import com.sun.tools.javac.code.Symbol
import javax.lang.model.type.TypeKind

class OptionalField(symbol: Symbol.VarSymbol): Field(symbol) {
    var defaultValue: Any? = null
        private set
    override val prefix: String
        get() = "OPTIONAL_"

    init {
        val optional = symbol.getAnnotation(Optional::class.java)
        defaultValue = when (symbol.type.kind) {
            TypeKind.BOOLEAN -> optional.booleanValue
            TypeKind.CHAR -> "${optional.charValue}"
            TypeKind.BYTE -> "(byte) ${optional.byteValue}"
            TypeKind.SHORT -> "(short) ${optional.shortValue}"
            TypeKind.INT -> optional.intValue
            TypeKind.LONG -> "${optional.longValue}L"
            TypeKind.FLOAT -> "${optional.floatValue}f"
            TypeKind.DOUBLE -> optional.doubleValue
            else -> if (symbol.type.isSameTypeWith(String.javaClass)) {
                //注意字面量的引号
                """"${optional.stringValue}""""
            } else {
                null
            }
        }
    }

    override fun compareTo(other: Field) = if(other is OptionalField) super.compareTo(other) else 1
}