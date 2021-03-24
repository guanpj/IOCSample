package com.me.guanpj.ioclib.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.aptutils.types.isSubTypeOf
import com.me.guanpj.ioclib.annotations.Builder
import com.me.guanpj.ioclib.annotations.Optional
import com.me.guanpj.ioclib.annotations.Required
import com.me.guanpj.ioclib.compiler.activity.ActivityClass
import com.me.guanpj.ioclib.compiler.activity.entiry.Field
import com.me.guanpj.ioclib.compiler.activity.entiry.OptionalField
import com.sun.tools.javac.code.Symbol
import java.lang.Exception
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class BuilderProcessor : AbstractProcessor() {
    private val supportedAnnotations =
        setOf(Builder::class.java, Optional::class.java, Required::class.java)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.RELEASE_7

    override fun getSupportedAnnotationTypes(): MutableSet<String> = supportedAnnotations.mapTo(HashSet<String>(), Class<*>::getCanonicalName)

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val activityClasses = HashMap<Element, ActivityClass>()
        roundEnv.getElementsAnnotatedWith(Builder::class.java)
            .filter { it.kind.isClass }
            .forEach {
                try {
                    if (it.asType().isSubTypeOf("android.app.Activity")) {
                        activityClasses[it] = ActivityClass(it as TypeElement)
                    } else {
                        Logger.error(it, "Unsupported typeElement: ${it.simpleName}")
                    }
                } catch (e: Exception) {
                    Logger.logParsingError(it, Builder::class.java, e)
                }
            }

        roundEnv.getElementsAnnotatedWith(Required::class.java)
            .filter { it.kind == ElementKind.FIELD }
            .forEach {
                activityClasses[it.enclosingElement]?.field?.add(Field(it as Symbol.VarSymbol))
                    ?: Logger.error(it, "Field $it annotated as Required while ${it.enclosedElements} not annotated.")
            }

        roundEnv.getElementsAnnotatedWith(Optional::class.java)
            .filter { it.kind == ElementKind.FIELD }
            .forEach {
                activityClasses[it.enclosingElement]?.field?.add(OptionalField(it as Symbol.VarSymbol))
                    ?: Logger.error(it, "Field $it annotated as Optional while ${it.enclosedElements} not annotated.")
            }

        activityClasses.values.forEach {
            it.builder.build(AptContext.filer)
        }

        return true
    }

}