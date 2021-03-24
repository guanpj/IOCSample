package com.me.guanpj.ioclib.compiler.activity.method

import com.me.guanpj.ioclib.compiler.ActivityClassBuilder
import com.me.guanpj.ioclib.compiler.activity.ActivityClass
import com.me.guanpj.ioclib.compiler.activity.entiry.OptionalField
import com.me.guanpj.ioclib.compiler.activity.prebuilt.INTENT
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

class StartMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val startMethod = StartMethod(activityClass, ActivityClassBuilder.METHOD_NAME)

        val groupFields = activityClass.field.groupBy { it is OptionalField }
        val requiredFields = groupFields[false] ?: emptyList()
        val optionalFields = groupFields[true] ?: emptyList()

        startMethod.addAllFields(requiredFields)

        val startMethodNoOptional = startMethod.copy(ActivityClassBuilder.METHOD_NAME_NO_OPTIONAL)

        startMethod.addAllFields(optionalFields)
        startMethod.build(typeBuilder)

        if (optionalFields.isNotEmpty()) {
            startMethodNoOptional.build(typeBuilder)
        }

        if (optionalFields.size < 3) {
            optionalFields.forEach { field ->
                startMethodNoOptional.copy(ActivityClassBuilder.METHOD_NAME_FOR_OPTIONAL + field.name.capitalize())
                    .also { it.addField(field) }
                    .build(typeBuilder)
            }
        } else {
            val fillIntentMethodBuilder = MethodSpec.methodBuilder("fillIntent")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(INTENT.java, "intent")

            val builderName = activityClass.simpleName + ActivityClassBuilder.POSIX
            val builderClassName = ClassName.get(activityClass.packageName, builderName)

            optionalFields.forEach { field ->
                typeBuilder.addField(FieldSpec.builder(field.asJavaTypeName(), field.name, Modifier.PRIVATE).build())

                val builderMethod = MethodSpec.methodBuilder(field.name)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(field.asJavaTypeName(), field.name)
                    .addStatement("this.${field.name} = ${field.name}")
                    .returns(builderClassName)
                    .addStatement("return this")
                typeBuilder.addMethod(builderMethod.build())

                if (field.isPrimitive) {
                    fillIntentMethodBuilder.addStatement("intent.putExtra(\$S, \$L)", field.name, field.name)
                } else {
                    fillIntentMethodBuilder.beginControlFlow("if (\$L != null)", field.name)
                        .addStatement("intent.putExtra(\$S, \$L)", field.name, field.name)
                        .endControlFlow()
                }
            }

            typeBuilder.addMethod(fillIntentMethodBuilder.build())

            startMethodNoOptional.copy(ActivityClassBuilder.METHOD_NAME_FOR_OPTIONALS)
                .staticMethod(false)
                .build(typeBuilder)
        }
    }
}