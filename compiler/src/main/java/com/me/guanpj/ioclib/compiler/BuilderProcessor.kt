package com.me.guanpj.ioclib.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.me.guanpj.ioclib.annotations.Builder
import com.me.guanpj.ioclib.annotations.Optional
import com.me.guanpj.ioclib.annotations.Required
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

class BuilderProcess : AbstractProcessor() {
    private val supportedAnnotations =
        setOf(Builder::class.java, Optional::class.java, Required::class.java)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.RELEASE_7

    override fun getSupportedAnnotationTypes(): MutableSet<String> = supportedAnnotations.mapTo(HashSet<String>(), Class<*>::getCanonicalName)

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        roundEnv.getElementsAnnotatedWith(Builder::class.java).forEach {
            Logger.warn(it, it.simpleName.toString())
        }
        return true
    }

}