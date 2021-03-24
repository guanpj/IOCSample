package com.me.guanpj.ioclib.compiler.activity

import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.aptutils.types.simpleName
import com.me.guanpj.ioclib.compiler.ActivityClassBuilder
import com.me.guanpj.ioclib.compiler.activity.entiry.Field
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

class ActivityClass(val typeElement: TypeElement) {
    val simpleName = typeElement.simpleName()
    val packageName = typeElement.packageName()

    val field = TreeSet<Field>()
    val isAbstract = typeElement.modifiers.contains(Modifier.ABSTRACT)
    val isKotlin = typeElement.getAnnotation(META_DATA) != null
    val builder = ActivityClassBuilder(this)

    override fun toString() = "$packageName.$simpleName[${field.joinToString()}]"

    companion object {
        val META_DATA = Class.forName("kotlin.Metadata") as Class<Annotation>
    }
}