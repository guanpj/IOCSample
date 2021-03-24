package com.me.guanpj.ioclib.compiler.activity.method

import com.me.guanpj.ioclib.compiler.activity.ActivityClass
import com.me.guanpj.ioclib.compiler.activity.entiry.OptionalField
import com.me.guanpj.ioclib.compiler.activity.prebuilt.ACTIVITY
import com.me.guanpj.ioclib.compiler.activity.prebuilt.BUNDLE
import com.me.guanpj.ioclib.compiler.activity.prebuilt.BUNDLE_UTILS
import com.me.guanpj.ioclib.compiler.activity.prebuilt.INTENT
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

class InjectMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val methodBuilder = MethodSpec.methodBuilder("inject")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addParameter(ACTIVITY.java, "instance")
            .addParameter(BUNDLE.java, "savedInstanceState")
            .beginControlFlow("if (instance instanceof \$T)", activityClass.typeElement)
            .addStatement("\$T typedInstance = (\$T) instance", activityClass.typeElement, activityClass.typeElement)
            .addStatement("\$T extras = savedInstanceState == null ? typedInstance.getIntent().getExtras() : savedInstanceState", BUNDLE.java)
            .beginControlFlow("if (extras != null)")

        activityClass.field.forEach { field ->
            val name = field.name
            val typeName = field.asJavaTypeName().box()

            if (field is OptionalField) {
                methodBuilder.addStatement("\$T \$LValue = \$T.<\$T>get(extras, \$S, \$L)", typeName, name, BUNDLE_UTILS.java, typeName, name, field.defaultValue)
            } else {
                methodBuilder.addStatement("\$T \$LValue = \$T.<\$T>get(extras, \$S)", typeName, name, BUNDLE_UTILS.java, typeName, name)
            }

            if (field.isPrivate) {
                methodBuilder.addStatement("typedInstance.set\$L(\$LValue)", name.capitalize(), name)
            } else {
                methodBuilder.addStatement("typedInstance.\$L = \$LValue", name, name)
            }
        }

        methodBuilder.endControlFlow().endControlFlow()
        typeBuilder.addMethod(methodBuilder.build())
    }
}