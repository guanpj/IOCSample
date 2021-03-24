package com.me.guanpj.ioclib.compiler

import com.me.guanpj.ioclib.compiler.activity.ActivityClass
import com.me.guanpj.ioclib.compiler.activity.method.ConstantBuilder
import com.me.guanpj.ioclib.compiler.activity.method.InjectMethodBuilder
import com.me.guanpj.ioclib.compiler.activity.method.SaveStateMethodBuilder
import com.me.guanpj.ioclib.compiler.activity.method.StartMethodBuilder
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer

class ActivityClassBuilder(private val activityClass: ActivityClass) {
    companion object {
        const val POSIX = "Builder"
        const val METHOD_NAME = "start"
        const val METHOD_NAME_NO_OPTIONAL = METHOD_NAME + "WithoutOptional"
        const val METHOD_NAME_FOR_OPTIONAL = METHOD_NAME + "WithOptional"
        const val METHOD_NAME_FOR_OPTIONALS = METHOD_NAME + "WithOptionals"
    }

    fun build(filer: Filer) {
        if (activityClass.isAbstract) return
        val typeBuilder = TypeSpec.classBuilder(activityClass.simpleName + POSIX)

        ConstantBuilder(activityClass).build(typeBuilder)
        StartMethodBuilder(activityClass).build(typeBuilder)
        SaveStateMethodBuilder(activityClass).build(typeBuilder)
        InjectMethodBuilder(activityClass).build(typeBuilder)

        writeJavaToFile(filer, typeBuilder.build())
    }

    private fun writeJavaToFile(filer: Filer, typeSpec: TypeSpec) {
        JavaFile.builder(activityClass.packageName, typeSpec).build().writeTo(filer)
    }
}