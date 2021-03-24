package com.me.guanpj.ioclib.compiler.activity.entiry

import com.bennyhuo.aptutils.types.asJavaTypeName
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.sun.tools.javac.code.Symbol

open class Field(private val symbol: Symbol.VarSymbol): Comparable<Field> {

    open val prefix = "REQUIRED_"
    val name = symbol.qualifiedName.toString()
    val isPrivate = symbol.isPrivate
    val isPrimitive = symbol.type.isPrimitive

    fun  asJavaTypeName() = symbol.type.asJavaTypeName()
    open fun asKotlinTypeName() = symbol.type.asKotlinTypeName()

    override fun compareTo(other: Field) = name.compareTo(other.name)

    override fun toString() = "$name:${symbol.type}"
}