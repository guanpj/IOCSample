package com.me.guanpj.ioclib.compiler.activity.method

import com.me.guanpj.ioclib.compiler.activity.ActivityClass
import com.me.guanpj.ioclib.compiler.activity.utils.camelToUnderLine
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

class ConstantBuilder(private val activityClass: ActivityClass) {
    //public static final String REQUIRE_USER_NAME = "user_name"
    fun build(typeBuilder: TypeSpec.Builder) {
        activityClass.field.forEach {
            typeBuilder.addField(
                FieldSpec.builder(
                    String::class.java,
                    it.prefix + it.name.camelToUnderLine().toLowerCase(),
                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL
                ).initializer("\$S", it.name).build()
            )
        }
    }
}